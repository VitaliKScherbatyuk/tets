/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Configuration class for handling email services.
 */
@Configuration
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    /**
     * Sends a verification code to the specified email address.
     * @param to the recipient's email address
     * @param code the verification code to be sent
     */
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

    /**
     * Sends an email with the specified subject and text to the specified email address.
     * @param to the recipient's email address
     * @param subject the subject of the email
     * @param text of the email
     */
    public void sendEmail(String to, String subject, String text) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an email with the specified subject  to the specified email address.
     * @param to the recipient's email address
     * @param subject the subject of the email
     */
    public void sendMessageAndComment(String to, String subject) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            emailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
