/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;
import scherbatyuk.network.filter.UserActivityFilter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * implements the ServletContextInitializer interface and is used to set the servlet
 * context when the application starts.
 */
@Component
public class ServletInitializer implements ServletContextInitializer {

    /**
     * The onStartup method is called automatically when the servlet context is initialized.
     * In this method, you add a UserActivityFilter to the servlet context using the addFilter method.
     * This filter will be applied to all URL paths (/*) in the application
     * @param servletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addFilter("userActivityFilter", UserActivityFilter.class).addMappingForUrlPatterns(null, false, "/*");
    }
}
