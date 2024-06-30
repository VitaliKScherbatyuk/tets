/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Mailjet API client initialization.
 */
@Configuration
public class MailjetConfig {

    private static final String YOUR_API_KEY = "b2c704e0030d4775b5e3aa3801de51cc";
    private static final String YOUR_API_SECRET = "61cfe9961bb591c90dc20962a03a00a5";

    /**
     * Bean definition to create and configure the MailjetClient instance.
     *
     * @return MailjetClient instance configured with the provided API credentials.
     */
    @Bean
    public MailjetClient mailjetClient() {
        ClientOptions options = ClientOptions.builder()
                .apiKey(YOUR_API_KEY)
                .apiSecretKey(YOUR_API_SECRET)
                .build();
        return new MailjetClient(options);
    }
}
