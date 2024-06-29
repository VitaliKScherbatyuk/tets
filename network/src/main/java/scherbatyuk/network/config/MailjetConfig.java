package scherbatyuk.network.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailjetConfig {

    private static final String YOUR_API_KEY = "b2c704e0030d4775b5e3aa3801de51cc";
    private static final String YOUR_API_SECRET = "32fafac77581286e33bbecb008c8834d";

    @Bean
    public MailjetClient mailjetClient() {
        return new MailjetClient(YOUR_API_KEY, YOUR_API_SECRET, new ClientOptions("v3.1"));
    }
}
