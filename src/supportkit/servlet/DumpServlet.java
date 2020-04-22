package supportkit.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Cookie、SessionScope、ApplicationScopeに格納されている変数を全て表示します
 *
 * @author mano
 *
 */
@WebServlet("/dump")
public class DumpServlet extends HttpServlet {

	private List<String> skipList = Collections.unmodifiableList(Arrays.asList("org.apache", "javax"));

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Map<String, String> cookieMap = new LinkedHashMap<>();
		Map<String, String> sessionMap = new LinkedHashMap<>();
		Map<String, String> servlectContextMap = new LinkedHashMap<>();

		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			cookies = new Cookie[0];
		}

		// クッキーにアクセス
		System.out.println("1. クッキーには「" + cookies.length + "件」の変数が保持されています");
		for (Cookie cookie : cookies) {

			String name = cookie.getName();
			String value = cookie.getValue();
			cookieMap.put(name, value);
			System.out.println("　　クッキーの変数「" + name + "」には「" + value + "」が代入されています");
		}

		// セッションにアクセス
		HttpSession session = request.getSession();

		int sessionCount = 0;
		Enumeration<String> temp1Name = session.getAttributeNames();
		while (temp1Name.hasMoreElements()) {
			temp1Name.nextElement();
			sessionCount++;
		}

		System.out.println();
		System.out.println("2. セッションスコープには「" + sessionCount + "件」の変数が保持されています");
		Enumeration<String> sessionKeyNames = session.getAttributeNames();
		while (sessionKeyNames.hasMoreElements()) {

			String key = sessionKeyNames.nextElement();
			String value = Objects.toString(session.getAttribute(key));

			sessionMap.put(key, value);

			System.out.println("　　セッションスコープの変数「" + key + "」には「" + value + "」が代入されています");
		}

		// applicationスコープにアクセス
		ServletContext servletContext = getServletContext();

		int appCount = 0;
		Enumeration<String> temp2Names = servletContext.getAttributeNames();
		while (temp2Names.hasMoreElements()) {
			String key = temp2Names.nextElement();
			if (isSkipKey(key, skipList)) {
				continue;
			}
			appCount++;
		}

		System.out.println();

		System.out.println("3. アプリケーションスコープには「" + appCount + "件」の変数が保持されています");
		Enumeration<String> applicationKeyNames = servletContext.getAttributeNames();
		while (applicationKeyNames.hasMoreElements()) {
			String key = applicationKeyNames.nextElement();
			String value = Objects.toString(servletContext.getAttribute(key));

			if (isSkipKey(key, skipList)) {
				continue;
			}
			servlectContextMap.put(key, value);

			System.out.println("　　アプリケーションスコープの変数「" + key + "」には「" + value + "」が代入されています");
		}

		// アクセスした人に応答するためのHTMLファイルを用意する
		PrintWriter pw = response.getWriter();

		// ファイルにHTMLを書き込み
		pw.append("<!DOCTYPE html5>");
		pw.append("<html>");
		pw.append("<head>");
		pw.append("<meta http-equiv=\"content-type\" content=\"\text/html; charset=utf-8\" />");
		pw.append("<style type=\"text/css\">");

		pw.append("table {");
		pw.append("	/* 枠線 */");
		pw.append("	border-collapse: collapse;");
		pw.append("	border-right: 1px solid #999;");
		pw.append("}");

		pw.append("table th {");
		pw.append("	/* 内部余白 */");
		pw.append("	padding: 10px;");
		pw.append("	/* 枠線 */");
		pw.append("	border-top: 1px solid #fff;");
		pw.append("	/* 文字色 */");
		pw.append("	color: #fff;");
		pw.append("	/* 文字表示 */");
		pw.append("	text-align: left;");
		pw.append("	vertical-align: top;");
		pw.append("	/* 背景色 */");
		pw.append("	background-color: #555;");
		pw.append("}");

		pw.append("table td {");
		pw.append("	/* 内部余白 */");
		pw.append("	padding: 6px;");
		pw.append("	/* 枠線 */");
		pw.append("	border: 1px solid #999;");
		pw.append("	/* 背景色 */");
		pw.append("	background-color: #fff;");
		pw.append("}");
		pw.append("</style>");
		pw.append("</head>");
		pw.append("<body>");
		pw.append("<table>");
		pw.append("<th>分類</th>");
		pw.append("<th>キー</th>");
		pw.append("<th>値</th>");

		for (Entry<String, String> entry : cookieMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			pw.append("<tr>");
			pw.append("	<td>クッキー</td>");
			pw.append("	<td>" + key + "</td>");
			pw.append("	<td>" + value + "</td>");
			pw.append("</tr>");
		}

		for (Entry<String, String> entry : sessionMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			pw.append("<tr>");
			pw.append("	<td>セッションスコープ</td>");
			pw.append("	<td>" + key + "</td>");
			pw.append("	<td>" + value + "</td>");
			pw.append("</tr>");
		}

		for (Entry<String, String> entry : servlectContextMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			pw.append("<tr>");
			pw.append("	<td>アプリケーションスコープ</td>");
			pw.append("	<td>" + key + "</td>");
			pw.append("	<td>" + value + "</td>");
			pw.append("</tr>");
		}

		pw.append("</table>");
		pw.append("</body>");
		pw.append("</html>");
	}

	private boolean isSkipKey(String key, List<String> skipList) {
		for (String skipKey : skipList) {
			if (key.contains(skipKey)) {
				return true;
			}
		}
		return false;
	}

}
