package supportkit.listener;

import java.util.Objects;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * セッションスコープに値が設定された際にロギングするためのリスナ
 *
 * @author mano
 *
 */
@WebListener
public class SessionLoggingListener implements HttpSessionAttributeListener {

	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		String name = event.getName();
		String value = Objects.toString(event.getValue());
		if (value.length() > 500) {
			value = value + "...";
		}

		System.out.flush();
		System.out.println("セッションの変数「" + name + "」が新規に登録され「" + value + "」が代入されました");
		System.out.flush();
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		String name = event.getName();

		System.out.flush();
		System.out.println("セッションの変数「" + name + "」が破棄されました");
		System.out.flush();
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
		String name = event.getName();
		String value = Objects.toString(event.getValue());

		if (value.length() > 500) {
			value = value + "...";
		}

		System.out.flush();
		System.out.println("セッションの変数「" + name + "」が「" + value + "」に上書きされました");
		System.out.flush();
	}

}
