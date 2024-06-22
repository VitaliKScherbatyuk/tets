package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.SupportService;
import scherbatyuk.network.service.UserService;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private SupportService supportService;

    /**
     * Gets a list of all users using the getAllUser method of the UserService service.
     * Adds the resulting list of users to the model for use on the page.
     * Returns the name of the "users" page.
     *
     * @param model
     * @return
     */
    @RequestMapping("/users")
    public String userPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Admin"))) {
            List<User> users = userService.getAllUser();
            model.addAttribute("users", users);
            return "users";
        }
        return "403"; // або інша сторінка помилки
    }

    @GetMapping("/letter")
    public String letters(){
        return "letter";
    }

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Admin"))) {
            int countSupport = supportService.countSupportLetters();
            model.addAttribute("countSupport", countSupport);
        }
        return "admin";
    }


    @GetMapping("/sendLetter")
    public String sendLetter() {
        return "sendLetter";
    }

    @PostMapping("/sendLetter")
    public String handleLetterSubmission(@RequestParam("fname") String firstName,
                                         @RequestParam("comment") String comment,
                                         Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        supportService.saveLetter(user, firstName, comment);

        return "redirect:/home";
    }

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


}


