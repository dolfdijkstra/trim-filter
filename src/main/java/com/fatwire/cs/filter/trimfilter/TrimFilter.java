package com.fatwire.cs.filter.trimfilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 *  <pre>
 *  &lt;filter&gt;
        &lt;filter-name&gt;TrimFilter&lt;/filter-name&gt;
        &lt;filter-class&gt;com.fatwire.cs.filter.trimfilter.TrimFilter&lt;/filter-class&gt;
    &lt;/filter&gt;
[...]
    &lt;filter-mapping&gt;
        &lt;filter-name&gt;TrimFilter&lt;/filter-name&gt;
        &lt;url-pattern&gt;/ContentServer/*&lt;/url-pattern&gt;
    &lt;/filter-mapping&gt;
</pre>

 * 
 * @author Dolf.Dijkstra
 * @since Sep 19, 2009
 */
public class TrimFilter implements Filter {

    @SuppressWarnings("unused")
    private FilterConfig config;

    private boolean ssAware = true;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        ssAware = !"false".equals(config.getInitParameter("ss-aware"));

    }

    public void destroy() {
        this.config = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (shouldTrim(request)) {
            TrimResponseWrapper wrapper = new TrimResponseWrapper(
                    (HttpServletResponse) response);

            chain.doFilter(request, wrapper);
            wrapper.compress();
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean shouldTrim(ServletRequest r) {
        if (r instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) r;
            if ("true".equals(r.getParameter(HelperStrings.NOTRIM))) {
                return false;
            }
            if (!ssAware)
                return true;
            if ("true"
                    .equals(r.getParameter(HelperStrings.SS_CLIENT_INDICATOR))
                    || "true".equals(r
                            .getParameter(HelperStrings.SS_PAGEDATA_REQUEST))) {
                return true;
            }
            Cookie[] c = request.getCookies();
            if (c == null)
                return false;
            for (Cookie cookie : c) {
                if (HelperStrings.SS_CLIENT_INDICATOR.equals(cookie.getName())
                        && "true".equals(cookie.getValue())) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }
}
