package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationCode(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Verification Code");
            message.setText("Your verification code is: " + code);
            emailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }


}
