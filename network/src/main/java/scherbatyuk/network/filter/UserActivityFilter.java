package scherbatyuk.network.filter;

import scherbatyuk.network.domain.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@WebFilter("/*")
public class UserActivityFilter implements Filter {
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
            // Оновіть час активності користувача тут, наприклад:
            user.setLastActivityTime(LocalDateTime.now());
        }

        // Продовжити ланцюг фільтрів
        filterChain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {
        // Звільнення ресурсів фільтра
        Filter.super.destroy();
    }
}
