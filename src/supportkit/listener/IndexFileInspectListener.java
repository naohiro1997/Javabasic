package supportkit.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import supportkit.utils.FileSearcher;

/**
 * Indexファイルが重複して存在しないかチェックするリスナー
 *
 * @author mano
 *
 */
@WebListener
public class IndexFileInspectListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {

		String contextPath = event.getServletContext().getRealPath("/");

		// リソースの一覧を保持するためのリスト
		List<String> resoruceList = new ArrayList<>();

		// 存在しないファイルを指定した時にログを出力させたいため、WebContent以下に配備されているファイルを取得
		// アクセスの度では無くファイルがデプロイされたタイミングでリソース一覧を取得すれば良いが、簡易実装のため毎回生成
		// WebContent以下（Tomcatではwebapp以下）のファイルを探索
		FileSearcher searcher = new FileSearcher();
		for (File file : searcher.listFiles(contextPath, null, FileSearcher.Options.TYPE_FILE, false, 0)) {
			String resourcePath = file.getAbsolutePath().substring(contextPath.length()).replace("\\", "/");
			if (resourcePath.contains("index.html") || resourcePath.contains("index.jsp")) {
				resoruceList.add(resourcePath);
			}
		}

		if (resoruceList.size() > 1) {
			System.out.flush();
			System.out.flush();

			System.out.println("=======================================================");
			System.out.println("WebContentフォルダ以下に複数のウェルカムページが見つかりました");
			System.out.println(resoruceList.toString());
			System.out.println("ウェルカムページに使われるのは" + resoruceList.get(0) + "です");
			System.out.println("=======================================================");

			System.err.flush();

		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// 何もしない
	}

}
