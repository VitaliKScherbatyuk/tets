package scherbatyuk.network.filter;


import scherbatyuk.network.domain.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;


@WebFilter("/*")
public class UserActivityFilter implements Filter {

    private static final Logger logger = Logger.getLogger(UserActivityFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Ініціалізація фільтра
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // Оновити час активності користувача тут

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpSession session = httpRequest.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            user.setLastActivityTime(LocalDateTime.now());
            logger.info("User activity updated: " + user.getLastActivityTime());
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {

        Filter.super.destroy();
    }
}
