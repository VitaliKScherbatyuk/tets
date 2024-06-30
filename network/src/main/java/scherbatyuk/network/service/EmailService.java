/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;


import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetRateLimitException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling email operations using Mailjet.
 */
@Service
public class EmailService {

    @Autowired
    private MailjetClient mailjetClient;

    /**
     * Sends a verification code to the specified email address.
     * @param to the recipient's email address
     * @param code the verification code to be sent
     */
    public void sendVerificationCode(String to, String code) {
        sendEmail(to, "Verification Code", "Your verification code is: " + code);
    }

    /**
     * Sends an email with the specified subject and text to the specified email address.
     * @param to the recipient's email address
     * @param subject the subject of the email
     * @param text the text of the email
     */
    public void sendEmail(String to, String subject, String text) {

        MailjetRequest request;
        MailjetResponse response;
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "vitaliktuhata@gmail.com")
                                        .put("Name", "Hobby network"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", to)
                                                .put("Name", "Recipient")))
                                .put(Emailv31.Message.SUBJECT, subject)
                                .put(Emailv31.Message.TEXTPART, text)));
        try {
            response = mailjetClient.post(request);
            System.out.println(response.getStatus());
            System.out.println(response.getData());
        } catch (MailjetRateLimitException e) {
            System.err.println("Rate limit exceeded: " + e.getMessage());
        } catch (MailjetException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    /**
     * Sends an email with the specified subject to the specified email address.
     * @param to the recipient's email address
     * @param subject the subject of the email
     */
    public void sendMessageAndComment(String to, String subject) {

        String text = "You have received a new message on our site. Please login to check.";
        sendEmail(to, subject, text);
    }
}



