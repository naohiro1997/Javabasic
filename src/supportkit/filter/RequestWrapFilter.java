package supportkit.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import supportkit.wrapper.AttributeKeyCacheRequest;

/**
 * Servlet→JSP間でのパラメータのやり取りをダンプしたいのでsetAttributeされたキーを保持させる必要があり、
 * HttpServletRequstWrapperを被せるためのフィルタ
 *
 * @author mano
 *
 */
@WebFilter("*")
public class RequestWrapFilter implements Filter {

	@Override
	public void init(FilterConfig paramFilterConfig) throws ServletException {
		// 何もしない
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {

			AttributeKeyCacheRequest attributeKeyCacheRequest = new AttributeKeyCacheRequest((HttpServletRequest) req);

			// サーブレットパスを保存
			String servletPath = attributeKeyCacheRequest.getServletPath();
			attributeKeyCacheRequest.setPreviousServletName(servletPath);

			// 本処理へ
			chain.doFilter(attributeKeyCacheRequest, res);

		} else {
			// 念のためこの分岐も作っておく
			chain.doFilter(req, res);
		}
	}

	@Override
	public void destroy() {
		// 何もしない
	}

}
