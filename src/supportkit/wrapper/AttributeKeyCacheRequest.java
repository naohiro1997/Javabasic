package supportkit.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * setAttributeで指定されたキー名を取得します
 *
 * @author mano
 *
 */
public class AttributeKeyCacheRequest extends HttpServletRequestWrapper {

	private List<String> keyList = new ArrayList<String>();

	private static final List<String> SPECIAL_KEY_LIST = Collections.unmodifiableList(Arrays.asList("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", "org.apache.catalina.core.DISPATCHER_TYPE"));

	private String dispatcherRequestPath = "";
	private String dispacherType = "";

	/** アクセスされるサーブレット名 */
	private String previousServletName;

	public AttributeKeyCacheRequest(HttpServletRequest request) {
		super(request);
	}

	@Override
	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);

		if ("org.apache.catalina.core.DISPATCHER_REQUEST_PATH".equals(name)) {
			dispatcherRequestPath = Objects.toString(value);
		}

		if ("org.apache.catalina.core.DISPATCHER_TYPE".equals(name)) {
			dispacherType = Objects.toString(value);
		}

		if (!isSpecial4Catalina(name)) {
			keyList.add(name);
		}
	}

	/**
	 * setAttributeされたキーの一覧を取得します
	 */
	public List<String> getAttributeKeys() {
		return keyList;
	}

	/**
	 * ディスパッチ先のURLを取得します
	 *
	 * @return ディスパッチ先のURL
	 */
	public String getDispatcherRequestPath() {
		return dispatcherRequestPath;
	}

	/**
	 * ディスパッチ種別を取得します
	 *
	 * @return ディスパッチ種別
	 */
	public String getDispacherType() {
		return dispacherType;
	}

	public void setPreviousServletName(String previousServletName) {
		this.previousServletName = previousServletName;
	}

	public String getPreviousServletName() {
		return previousServletName;
	}

	/**
	 * Catalinaで設定される特別なキーかどうかの判定
	 *
	 * @param name
	 * @return
	 */
	private boolean isSpecial4Catalina(String name) {
		for (String key : SPECIAL_KEY_LIST) {
			if (key.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
