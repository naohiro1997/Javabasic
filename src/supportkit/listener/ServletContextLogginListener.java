package supportkit.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.annotation.WebListener;

/**
 * アプリケーションスコープに値が設定された際にロギングするためのリスナ
 *
 * @author mano
 *
 */
@WebListener
public class ServletContextLogginListener implements ServletContextAttributeListener {

	private List<String> skipList = Arrays.asList("org.apache");

	@Override
	public void attributeAdded(ServletContextAttributeEvent event) {
		String name = event.getName();
		if (isSkipKey(name)) {
			return;
		}

		String value = Objects.toString(event.getValue());

		if (value.length() > 500) {
			value = value + "...";
		}

		System.out.flush();
		System.out.println("アプリケーションスコープの変数「" + name + "」が新規に登録され「" + value + "」が代入されました");
		System.out.flush();
	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent event) {
		String name = event.getName();
		if (isSkipKey(name)) {
			return;
		}

		System.out.flush();
		System.out.println("アプリケーションスコープの変数「" + name + "」が破棄されました");
		System.out.flush();
	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent event) {
		String name = event.getName();
		if (isSkipKey(name)) {
			return;
		}

		String value = Objects.toString(event.getValue());

		if (value.length() > 500) {
			value = value + "...";
		}

		System.out.flush();
		System.out.println("アプリケーションスコープの変数「" + name + "」が「" + value + "」に上書きされました");
		System.out.flush();
	}

	private boolean isSkipKey(String key) {

		for (String skipKey : skipList) {
			if (key.contains(skipKey)) {
				return true;
			}
		}
		return false;
	}

}
