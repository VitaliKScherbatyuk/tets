/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.filter;


import scherbatyuk.network.domain.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Is used to track user activity in a web application. It updates the user's last activity
 * time on each request.
 */
@WebFilter("/*")
public class UserActivityFilter implements Filter {

    private static final Logger logger = Logger.getLogger(UserActivityFilter.class.getName());

    /**
     * Filter initialization.
     * @param filterConfig the filter configuration.
     * @throws ServletException on initialization error.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * Processing each request. Updates the time of the user's last activity, if he is authorized.
     * @param servletRequest the request.
     * @param servletResponse the response.
     * @param filterChain filter chain.
     * @throws IOException on I/O error.
     * @throws ServletException if there is an error processing the request.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpSession session = httpRequest.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            user.setLastActivityTime(LocalDateTime.now());
            logger.info("User activity updated: " + user.getLastActivityTime());
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Freeing filter resources.
     */
    @Override
    public void destroy() {

        Filter.super.destroy();
    }
}
