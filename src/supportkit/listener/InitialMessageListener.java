package supportkit.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitialMessageListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {

		String contextPath = event.getServletContext().getContextPath();

		// 初回メッセージ作成用
		System.out.println("=======================================================");
		System.out.println("Webサーバを起動しました");
		System.out.println("Webアプリケーション名は「" + contextPath + "」です");
		System.out.println("=======================================================");

	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// 何もしない
	}

}
