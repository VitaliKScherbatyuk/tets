package scherbatyuk.network.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;
import scherbatyuk.network.filter.UserActivityFilter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Component
public class ServletInitializer implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addFilter("userActivityFilter", UserActivityFilter.class).addMappingForUrlPatterns(null, false, "/*");
    }
}
