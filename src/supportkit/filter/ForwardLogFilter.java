package supportkit.filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import supportkit.utils.CollectionUtils;
import supportkit.utils.FileSearcher;
import supportkit.utils.Sleeper;
import supportkit.wrapper.AttributeKeyCacheRequest;

/**
 * ServletからJSPにディスパッチされる場合にパラメータをログ出力するためのフィルターです
 * ※あるServletから別サーブレットへディスパッチされるようなケースには非対応なメッセージになっています
 *
 * @author mano
 *
 */
@WebFilter(urlPatterns = "*", dispatcherTypes = { DispatcherType.FORWARD })
public class ForwardLogFilter implements Filter {

	/** 行折り返しの最大値 */
	private int lineLimit = 200;

	@Override
	public void init(FilterConfig config) throws ServletException {
		// 何もしない
	}

	@Override
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest servletReq, ServletResponse servletRes, FilterChain chain) throws IOException, ServletException {

		if (!(servletReq instanceof AttributeKeyCacheRequest)) {
			// 設定不備の場合は何もしない
			chain.doFilter(servletReq, servletRes);
			return;
		}

		// 指定のリクエストラップが行われていた場合に限り、パラメータダンプする
		AttributeKeyCacheRequest request = AttributeKeyCacheRequest.class.cast(servletReq);
		HttpServletResponse response = HttpServletResponse.class.cast(servletRes);

		// JSPへ連携用のパラメータを全て出力する
		List<String> keys = request.getAttributeKeys();
		for (String key : keys) {
			String value = Objects.toString(request.getAttribute(key));

			if (value.length() > lineLimit) {
				// 出力する文字列が長すぎる場合は、外部ディスプレイを全員が使えない場合を加味して改行する
				System.out.println("ServletからJSPに渡した変数「" + key + "」には");
				System.out.println("　　" + value);
				System.out.println("が代入されています");

			} else {
				System.out.println("ServletからJSPに渡した変数「" + key + "」には「" + value + "」が代入されています");
			}
		}

		// URLとサーブレットのマッピングデータ保存用
		Map<String, String> urlServletMap = new HashMap<>();

		// 遷移元のサーブレット名を取得
		Map<String, ? extends ServletRegistration> servletRegistrations = request.getServletContext().getServletRegistrations();
		for (Object obj : servletRegistrations.entrySet()) {
			Map.Entry<String, ServletRegistration> entry = (Map.Entry<String, ServletRegistration>) obj;
			ServletRegistration registration = entry.getValue();
			String className = registration.getClassName();

			Collection<String> mappings = registration.getMappings();
			for (String mapping : mappings) {
				urlServletMap.put(mapping, className.replace(".", "/"));
			}
		}

		// Servletクラス名を取得
		String previousServletName = request.getPreviousServletName();
		String previousServletFileName = urlServletMap.get(previousServletName);

		// ディスパッチ先を出力
		String dispatcherRequestPath = request.getDispatcherRequestPath();
		System.out.println("サーブレット「" + previousServletFileName + "」から「" + dispatcherRequestPath + "」に移動します");

		// 存在しないファイルを指定した時にログを出力させたいため、WebContent以下に配備されているファイルを取得
		// アクセスの度では無くファイルがデプロイされたタイミングでリソース一覧を取得すれば良いが、簡易実装のため毎回生成
		String contextPath = request.getServletContext().getRealPath("/");
		String contextName = request.getServletContext().getContextPath();

		// リソースの一覧を保持するためのリスト
		List<String> resoruceList = new ArrayList<>();

		// 存在する遷移先かどうか判定
		FileSearcher searcher = new FileSearcher();
		for (File file : searcher.listFilesWithPattern(contextPath, ".*\\.(jsp|html)")) {
			String resourcePath = file.getAbsolutePath().substring(contextPath.length()).replace("\\", "/");
			resoruceList.add(contextName + "/" + resourcePath);
		}

		StringBuffer requestURL = request.getRequestURL();
		String url = requestURL.toString();

		// 存在するリソースを要求しているか判定
		if (!CollectionUtils.contais(url, resoruceList)) {
			// 存在しないリソースを指定していた場合の処理

			// バッファの関係上出力順が異なる場合があるためフラッシュ
			System.out.flush();

			Sleeper.sleepALittle();
			System.err.println("サーブレットからの移動先である「" + dispatcherRequestPath + "」が見当たりませんでした");
			System.err.println("サーブレット「" + previousServletFileName + "」の「RequestDispatcher dispatcher = req.getRequestDispatcher(\"" + dispatcherRequestPath + "\");」が正しい移動先を指定しているか確認しましょう");
			System.err.flush();
			Sleeper.sleepALittle();
		}

		// サーブレットコンテキストを上書き
		String servletPath = request.getServletPath();
		request.setPreviousServletName(servletPath);

		// 本処理を続行
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// 何もしない
	}

}
