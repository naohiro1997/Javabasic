package supportkit.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * 文字コードの設定用フィルタ
 *
 * @author mano
 *
 */
@WebFilter("*")
public class EncodingFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
		// 何もしない
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// 文字コード設定
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// 何もしない
	}

}
