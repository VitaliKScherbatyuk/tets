/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * The CustomAuthenticationSuccessHandler class is an implementation of the AuthenticationSuccessHandler
 * interface used to handle the successful user authentication event in Spring Security.
 * This class allows you to configure different behavior depending on the role of the logged in user.
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * method converts a list of user roles (authentication.getAuthorities()) to a set of roles (Set<String>).
     * This allows you to conveniently check for specific roles using the contains method.
     * If the user has the "Admin" role, he is redirected to the /admin page.
     * Otherwise (if the user does not have the "Admin" role), he is redirected to the /home page
     * @param request go to page /admin
     * @param response go to page /home
     * @param authentication an object containing user authentication information
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("Admin")) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/home");
        }
    }
}
