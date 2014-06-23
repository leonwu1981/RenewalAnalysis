package filters;

import java.io.IOException;

import javax.servlet.*;

public class SetCharacterEncodingFilter implements Filter {
	protected FilterConfig filterConfig = null;

	protected boolean ignore = true;

	public void destroy() {
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding("GBK");
		chain.doFilter(request, response);
                                  
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
