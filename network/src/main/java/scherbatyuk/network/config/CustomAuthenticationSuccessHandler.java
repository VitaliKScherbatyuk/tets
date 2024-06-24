/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.config;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import scherbatyuk.network.domain.User;

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

    Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    /**
     * method converts a list of user roles (authentication.getAuthorities()) to a set of roles (Set<String>).
     * This allows you to conveniently check for specific roles using the contains method.
     * If the user has the "Admin" role, he is redirected to the /admin page.
     * Otherwise (if the user does not have the "Admin" role), he is redirected to the /home page
     * @param request go to page /admin
     * @param response go to page /home
     * @param authentication an object containing user authentication information
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        try {
            if (roles.contains("Admin")) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/home");
            }
        } catch (IOException e) {
            logger.error("CustomAuthenticationSuccessHandler -> onAuthenticationSuccess error in response.sendRedirect, User ID: " + user.getId(), e);
            throw new RuntimeException(e);
        }
    }
}
