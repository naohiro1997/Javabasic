package supportkit.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class JdbcDriverLoadListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {

		try {
			// ドライバのロード

			// OracleのJDBCドライバ
			// SQLログを出力しない場合はこちらを有効にする
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// SQLログを出力したい場合は下記のDriverSpyのドライバをロードしておく
			// SQLログ出力する場合はclasspathにlog4jdbc-4-12.jar,logback-classic-1.1.3.jar,logback-core-1.1.3.jarを通して
			// ドライバをロードしておく
			Class.forName("net.sf.log4jdbc.DriverSpy");

		} catch (ClassNotFoundException e) {
			throw new RuntimeException(String.format("JDBCドライバのロードに失敗しました。詳細：[%s]", e.getMessage()), e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// 何もしない
	}

}
