package supportkit.filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import supportkit.utils.CollectionUtils;
import supportkit.utils.FileSearcher;
import supportkit.utils.Sleeper;

/**
 * アクセスログ出力用フィルタ
 *
 * @author mano
 *
 */
@WebFilter("*")
public class RequestLogFilter implements Filter {

	/** アクセスログをスキップする拡張子 */
	private List<String> skipList = Arrays.asList(".css", ".js", ".ico", ".jpg", ".gif", ".png");

	/** 存在チェックする拡張子 */
	private List<String> fileExistCheckList = Arrays.asList(".html", ".jsp");

	private List<String> loadSkipDirList = Arrays.asList("WEB-INF", "META-INF");

	/** 前回アクセスしたファイル */
	private String previousAccessFile = "";

	private long accessCont = 0;

	/** コンテキストの絶対パス */
	private String contextPath;

	/** コンテキストの名称（例：system1） */
	private String contextName;

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.contextPath = config.getServletContext().getRealPath("/");
		this.contextName = config.getServletContext().getContextPath();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest servletReq, ServletResponse servletRes, FilterChain chain) throws IOException, ServletException {

		// 初回アクセスかどうか判定
		if (accessCont == 0) {
			// 初回アクセスの場合は改行
			System.out.println();
			System.out.println();
		}

		// アクセス数をインクリメント
		accessCont++;

		HttpServletRequest request = HttpServletRequest.class.cast(servletReq);
		HttpServletResponse resopnse = HttpServletResponse.class.cast(servletRes);

		// リソースの一覧を保持するためのリスト
		List<String> resoruceList = new ArrayList<>();

		// 存在しないファイルを指定した時にログを出力させたいため、WebContent以下に配備されているファイルを取得
		// アクセスの度では無くファイルがデプロイされたタイミングでリソース一覧を取得すれば良いが、簡易実装のため毎回生成
		// WebContent以下（Tomcatではwebapp以下）のファイルを探索
		FileSearcher searcher = new FileSearcher();
		for (File file : searcher.listFiles(contextPath)) {
			if (isLoadSkipDir(file.getAbsolutePath())) {
				continue;
			}
			String resourcePath = file.getAbsolutePath().substring(contextPath.length()).replace("\\", "/");
			resoruceList.add(contextName + "/" + resourcePath);
		}

		StringBuffer requestURL = request.getRequestURL();
		String url = requestURL.toString();

		// 最後がスラッシュで終わっている場合は除去
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}

		// クエリURLを取得
		String queryString = request.getQueryString();

		if (constainsSkipSuffix(url)) {
			// スキップ対象の場合

			// 存在するリソースを要求しているか判定
			if (!CollectionUtils.contais(url, resoruceList)) {
				// 存在しないリソースを指定していた場合の処理

				// コンソールにアクセスログ出力
				if (url.contains("localhost") || url.contains("127.0.0.1")) {
					System.out.println("あなたは" + requestURL + "にアクセスしました");
				} else {
					System.out.println("だれかが" + requestURL + "にアクセスしてきました");
				}

				String tmp = url.replaceAll("http://.*" + contextName + "/", "");
				String resourceName = tmp.replaceAll("\\?.*", "");

				// 前回のアクセス先が存在するかどうか判定
				if (previousAccessFile == null || previousAccessFile.length() == 0) {
					// 何もなければファイル名は出さない（出せない）
					previousAccessFile = "HTMLやJSPファイル";
				}

				// 前回のファイル名が.htmlや.jsp以外の場合も出さない
				// Servletの場合の追跡も仕組み上は可能だが、コードがやや複雑になるので一旦諦める
				if (!previousAccessFile.endsWith(".html") && !previousAccessFile.endsWith(".jsp")) {
					// HTMLでもJSPでも無ければ完全なファイル名の指定はしない
					previousAccessFile = "HTMLやJSPファイル";
				}

				// コンソールへの出力順を正しくするためフラッシュしておく
				System.out.flush();

				// 更に順序を正しくさせるため数百ms処理を止める
				// やや強引だが、異常系の場合のみなので許容する
				Sleeper.sleepALittle();
				;
				System.err.println("「" + resourceName + "」のファイルが見当たりませんでした。");
				System.err.println("「" + previousAccessFile + "」を開いてHTMLやJSPの<link>タグや<img>タグで指定しているファイルパスが正しいか確認しましょう。");
				if (previousAccessFile.endsWith(".jsp")) {
					// JSPファイルの場合は追加メッセージ
					System.err.println("また「" + previousAccessFile + "」がServlet経由でアクセスしていないためCSSや画像ファイルが読み込めない可能性があります");
				}

				System.err.flush();
				Sleeper.sleepALittle();

				// 本処理を実行
				chain.doFilter(request, resopnse);

				// エラーログを出力した場合のみ改行する
				System.out.println();
				System.out.flush();

				return;
			}

			chain.doFilter(request, resopnse);
			return;
		}

		// スキップ対象以外はログ出力する
		// クエリパラメータを復元する
		if (queryString != null) {
			requestURL.append("?");
			requestURL.append(queryString);
		}

		// コンソールにアクセスログ出力
		if (url.contains("localhost") || url.contains("127.0.0.1")) {
			System.out.println("あなたは" + requestURL + "にアクセスしました");

		} else {
			System.out.println("だれかが" + requestURL + "にアクセスしてきました");
		}

		// ServletとHTML/JSPの場合で処理を切り分ける
		if (containsExistsCheckSuffix(url)) {
			// HTMLやJSPファイル

			// 存在するリソースを要求しているか判定
			if (!CollectionUtils.contais(url, resoruceList) && !isIndexFileAccess(url)) {
				// 存在しないリソースを指定していた場合の処理

				String tmp = url.replaceAll("http://.*" + contextName + "/", "");
				String resourceName = tmp.replaceAll("\\?.*", "");

				System.out.flush();
				Sleeper.sleepALittle();
				System.err.println("「" + resourceName + "」のファイルが見当たりませんでした。WebContent以下にファイルが存在するか確認しましょう。");
				System.err.flush();
				Sleeper.sleepALittle();

				// 本処理を実行
				chain.doFilter(request, resopnse);

				// エラーログを出力した場合のみ改行する
				System.out.println();

				// フラッシュ
				System.out.flush();

				return;

			}

			// コンテキスト指定の場合はインデックスファイルが存在するかどうか判定
			if (isIndexFileAccess(url) && !hasIndexFile(resoruceList)) {
				// indexファイルが存在しない場合

				System.out.flush();
				Sleeper.sleepALittle();
				System.err.println("index.htmlまたはindex.jspが見当たりませんでした。WebContent以下にファイルが存在するか確認しましょう。");
				System.err.flush();
				Sleeper.sleepALittle();

				// 本処理を実行
				chain.doFilter(request, resopnse);

				// エラーログを出力した場合のみ改行する
				System.out.println();

				// フラッシュ
				System.out.flush();

				return;
			}

			// 存在するファイルだった場合
			// Servletの本処理を実行
			chain.doFilter(request, resopnse);

			// アクセスが成功したファイル名を保存しておく
			String tmp = requestURL.toString().replaceAll("http://.*" + contextName + "/", "");
			String resourceName = tmp.replaceAll("\\?.*", "");
			previousAccessFile = resourceName;

			// 最後に改行を入れておく
			System.out.println("");

		} else {
			// Servletの場合
			// クエリパラメータをコンソールに出力
			Map<String, String[]> parameterMap = request.getParameterMap();

			for (Entry<String, String[]> entry : parameterMap.entrySet()) {

				String key = entry.getKey();
				String[] values = entry.getValue();

				if (values.length == 0) {
					System.out.println("クエリパラメータ「" + key + "」には値が設定されていません");

				} else if (values.length == 1) {
					String value = values[0];
					System.out.println("クエリパラメータ「" + key + "」には「" + value + "」が代入されています");

				} else {
					System.out.println("クエリパラメータ「" + key + "」には複数の値が設定されており" + Arrays.toString(values) + "が代入されています");
				}
			}

			// サーブレット名を出力する
			// URLとサーブレットのマッピングデータ保存用
			Map<String, String> urlServletMap = new HashMap<>();

			// 遷移元のサーブレット名を取得
			Map<String, ? extends ServletRegistration> servletRegistrations = request.getServletContext().getServletRegistrations();
			for (Object obj : servletRegistrations.entrySet())

			{
				Map.Entry<String, ServletRegistration> entry = (Map.Entry<String, ServletRegistration>) obj;
				ServletRegistration registration = entry.getValue();
				String className = registration.getClassName();

				Collection<String> mappings = registration.getMappings();
				for (String mapping : mappings) {
					urlServletMap.put(mapping, className.replace(".", "/"));
				}
			}

			// 一致するサーブレットファイル名を取得
			String servletFileName = urlServletMap.get(request.getServletPath());

			if (servletFileName == null || servletFileName.length() == 0) {
				// 一致するサーブレットファイル名が存在しなかった時は

				String servletPath = request.getServletPath();

				// バッファの関係上出力順が異なる場合があるためフラッシュ
				System.out.flush();

				Sleeper.sleepALittle();
				System.err.println("URLの「" + servletPath + "」に一致するサーブレットが見当たりませんでした。Servletファイルの@WebServletの指定が正しいか確認しましょう。");
				System.err.flush();
				Sleeper.sleepALittle();

			} else {
				// サーブレットのファイル名を出力
				System.out.println("URLに「" + request.getServletPath() + "」が指定されているのでサーブレット「" + servletFileName + "」に移動します");
			}

			// Servletの本処理を実行
			chain.doFilter(request, resopnse);

			// アクセスが成功したファイル名を保存しておく
			String tmp = requestURL.toString().replaceAll("http://.*" + contextName + "/", "");
			String resourceName = tmp.replaceAll("\\?.*", "");
			previousAccessFile = resourceName;

			// 最後に改行を入れておく
			System.out.println("");

			// 最後にフラッシュ
			System.out.flush();

		}

	}

	@Override
	public void destroy() {
	}

	/**
	 * スキップ対象の拡張子かどうか判定します
	 *
	 * @param url URL
	 * @return 拡張子の場合はtrue, そうでない場合はfalse
	 */
	private boolean constainsSkipSuffix(String url) {
		for (int i = 0; i < skipList.size(); i++) {
			if (url.endsWith(skipList.get(i))) {
				return true;
			}
		}
		return false;
	}

	private boolean containsExistsCheckSuffix(String url) {
		for (int i = 0; i < fileExistCheckList.size(); i++) {
			if (url.endsWith(fileExistCheckList.get(i))) {
				return true;
			}
		}

		if (url.endsWith(contextName)) {
			// コンテキスト指定ではindex.htmlやindex.jspを意味するのでtrueを返す
			return true;
		}

		return false;
	}

	private boolean isIndexFileAccess(String url) {
		if (url.endsWith(contextName)) {
			// コンテキスト指定ではindex.htmlやindex.jspを意味するのでtrueを返す
			return true;
		}
		return false;
	}

	private boolean isLoadSkipDir(String path) {
		for (String skipDir : loadSkipDirList) {
			if (path.contains(skipDir)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasIndexFile(List<String> list) {
		for (String name : list) {
			if (name.contains("/index")) {
				return true;
			}
		}
		return false;
	}

}
