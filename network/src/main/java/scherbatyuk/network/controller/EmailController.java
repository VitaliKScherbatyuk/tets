package scherbatyuk.network.controller;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private MailjetClient mailjetClient;

    @GetMapping("/sendEmail")
    public String sendEmail() {
        try {
            MailjetRequest email = new MailjetRequest(Email.resource)
                    .property(Email.FROMEMAIL, "your@email.com")
                    .property(Email.FROMNAME, "Your Name")
                    .property(Email.SUBJECT, "Test Subject")
                    .property(Email.TEXTPART, "Text content")
                    .property(Email.HTMLPART, "<h3>HTML content</h3>")
                    .property(Email.RECIPIENTS, "[{\"Email\":\"recipient@email.com\"}]");

            MailjetResponse response = mailjetClient.post(email);
            return "Email sent successfully! Response: " + response.getStatus();
        } catch (MailjetException | MailjetSocketTimeoutException e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
}
