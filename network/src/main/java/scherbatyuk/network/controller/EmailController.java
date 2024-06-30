/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.controller;

import com.mailjet.client.MailjetClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import scherbatyuk.network.config.VerificationCodeGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * REST controller for handling email-related operations.
 */
@RestController
public class EmailController {

    @Autowired
    private MailjetClient mailjetClient;

    /**
     * GET endpoint to simulate sending an email with a verification code.
     *
     * @param request the HTTP request object
     * @return a message indicating the email was sent successfully
     */
    @GetMapping("/sendEmail")
    public String sendEmail(HttpServletRequest request) {
        String verificationCode = VerificationCodeGenerator.generateCode();

        HttpSession session = request.getSession();
        session.setAttribute("verificationCode", verificationCode);

        return "Email sent successfully!";
    }
}

