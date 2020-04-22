package supportkit.filter;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * キャッシュをさせないためのフィルタ
 *
 * @author mano
 *
 */
@WebFilter("*")
public class NoCacheFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
		// 何もしない
	}

	@Override
	public void doFilter(ServletRequest servletReq, ServletResponse servletRes, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = HttpServletRequest.class.cast(servletReq);
		HttpServletResponse response = HttpServletResponse.class.cast(servletRes);

		// キャッシュをさせないための各種設定
		Calendar lastModifiedCalendar = Calendar.getInstance();
		Calendar expiresCalendar = Calendar.getInstance();
		expiresCalendar.set(1970, 0, 1, 0, 0, 0);

		// レスポンスヘッダに設定
		response.setDateHeader("Last-Modified", lastModifiedCalendar.getTime().getTime());
		response.setDateHeader("Expires", expiresCalendar.getTime().getTime());
		response.setHeader("pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// 何もしない
	}

}
