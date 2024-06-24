/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scherbatyuk.network.domain.Support;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.EmailService;
import scherbatyuk.network.service.SupportService;
import scherbatyuk.network.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles requests related to administrative functions such as user management, user support, etc.
 */
@Controller
public class AdminController {

    Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private SupportService supportService;
    @Autowired
    private EmailService emailService;

    /**
     * Handles requests related to administrative functions.
     *
     * @param model          for passing data to the page
     * @param authentication an object containing user authentication information
     * @return the name of the corresponding HTML page to display
     */
    @RequestMapping("/users")
    public String userPage(Model model, Authentication authentication) {

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Admin"))) {
            List<User> users = userService.getAllUser();
            model.addAttribute("users", users);

            int countSupport = supportService.countSupportLetters();
            model.addAttribute("countSupport", countSupport);

            return "users";
        }

        return "403";
    }

    /**
     * Displays the support letters page.
     *
     * @param model for transferring data to the page
     * @return the name of the corresponding HTML page to display
     */
    @GetMapping("/letter")
    public String letters(Model model) {

        int countSupport = supportService.countSupportLetters();
        model.addAttribute("countSupport", countSupport);

        List<Support> listLetter = supportService.getAllLetters().stream()
                .filter(support -> !support.isAnswerLetter())
                .sorted(Comparator.comparing(Support::getCommentCreate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("listLetter", listLetter);

        return "letter";
    }

    /**
     * Displays the administration panel
     *
     * @param model model for transferring data to the page
     * @param authentication an object containing user authentication information
     * @return the name of the corresponding HTML page to display
     */
    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Admin"))) {
            int countSupport = supportService.countSupportLetters();
            model.addAttribute("countSupport", countSupport);
        }

        int countAllUser = userService.getCountAllUser();
        model.addAttribute("countAllUser", countAllUser);
        return "admin";
    }

    /**
     * Method handles GET requests to the /sendLetter page
     *
     * @return the HTML page name "sendLetter"
     */
    @GetMapping("/sendLetter")
    public String sendLetter() {
        return "sendLetter";
    }

    /**
     * Method handles POST requests to the /sendLetter page.
     *
     * @param firstName The parameter passed as fname in the POST request.
     * @param comment   A parameter that is passed as a comment in a POST request.
     * @return after saving the support letter, redirects the user to the /home page.
     */
    @PostMapping("/sendLetter")
    public String handleLetterSubmission(@RequestParam("fname") String firstName,
                                         @RequestParam("comment") String comment) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        try {
            supportService.saveLetter(user, firstName, comment);
        } catch (Exception e) {
            logger.error("AdminController -> handleLetterSubmission: Error send letter by UserId: ", user.getId(), e);
        }

        return "redirect:/home";
    }

    /**
     * Method determines where to redirect the user based on their role.
     *
     * @return redirects to a page depending on the user's role.
     */
    @GetMapping("/redirectHome")
    public String redirectHome() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Admin"))) {
            return "redirect:/admin";
        } else {
            return "redirect:/home";
        }
    }

    /**
     * Method displays a page for responding to a support request.
     *
     * @param id    the ID of the support letter, which is passed in the URL as a path parameter.
     * @param model the model object to which the Support object is added for display on the page.
     * @return the HTML name of the requestAnswerSupport page.
     */
    @GetMapping("/requestAnswerSupport/{id}")
    public String requestAnswerSupport(@PathVariable Integer id, Model model) {

        Support letter = supportService.findById(id);
        model.addAttribute("letter", letter);

        return "requestAnswerSupport";
    }

    /**
     * Method handles POST requests to respond to a support request.
     *
     * @param letterId the ID of the support letter passed as a query parameter.
     * @param answer   the answer text that is passed as a query parameter.
     * @return the HTML name of the requestAnswerSupport page.
     */
    @PostMapping("/requestAnswerSupport")
    public String handleAnswerSubmission(@RequestParam("letterId") Integer letterId,
                                         @RequestParam("answer") String answer) {

        Support letter = supportService.findById(letterId);

        try {
            supportService.saveLetter(letter, answer);
            emailService.sendEmail(letter.getUser().getEmail(), "Answer to your request", answer);
        } catch (Exception e) {
            logger.error("AdminController -> handleAnswerSubmission: Error send request answer to letterId: ", letterId, e);
        }
        return "redirect:/letter";
    }
}


