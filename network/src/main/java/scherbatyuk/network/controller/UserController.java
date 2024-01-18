/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 24.12.2023
 */

package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.domain.UserRole;
import scherbatyuk.network.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


/**
 * Spring MVC controller that handles HTTP requests and manages
 * user interaction in your web application
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * method is responsible for processing HTTP GET requests to the URL path "/login".
     * It initializes a User object and returns a "login" page along with a model
     * that contains that object to create a login form
     *
     * @param model
     * @return
     */
    @GetMapping("/")
    public String loginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    /**
     * method handles HTTP POST requests to the URL path "/login" to authenticate the user.
     * It will try to find the user by email, check the password and redirect to
     * the "home" page if the authentication is successful. If authentication fails,
     * it will display an error message on the "login" page
     *
     * @param user
     * @param model
     * @return
     */
    @PostMapping("/")
    public String loginSubmit(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null && passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid login or password.");
            return "login";
        }
    }

    /**
     * method is responsible for processing HTTP GET requests to the URL path "/registration".
     * It initializes a User object and returns a "registration" page along with a model
     * that contains this object to create a user registration form
     * ф
     *
     * @param model
     * @return
     */
    @GetMapping("/registration")
    public String registrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    /**
     * method handles HTTP POST requests to the URL path "/registration" to create a new user.
     * It uses userService to save the new user and redirects the user to the "login"
     * page after successful registration
     *
     * @param user
     * @param model
     * @return
     */
    @PostMapping("/registration")
    public String registrationSubmit(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(user);
        return "redirect:/";
    }

    /**
     * method handles HTTP GET requests to the URL path "/logout". It calls the
     * request.logout() method to log the user out and redirects the user to the "login"
     * page after logging out
     *
     * @param request
     * @return
     * @throws ServletException
     */

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/login";
    }


    /**
     * Gets a list of all users using the getAllUser method of the UserService service.
     * Adds the resulting list of users to the model for use on the page.
     * Returns the name of the "users" page.
     *
     * @param model
     * @return
     */
    @RequestMapping("/users")
    public String userPage(Model model) {
        List<User> users = userService.getAllUser();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/profileUpdate")
    public String showProfileUpdatePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        model.addAttribute("user", user);
        return "profileUpdate";
    }

    @PostMapping("/profileUpdate")
    public String updateProfile() throws IOException {
        // Отримати користувача з бази даних за email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);


        return "redirect:/home";
    }




    /**
     * method is responsible for processing HTTP GET requests to the URL path "/home"
     * and returns the page "home"
     *
     * @param model
     * @return
     */
    @GetMapping("/home")
    public String homePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        UserRole role = user.getRole();

        List<User> userList = userService.getAllUser();
        model.addAttribute("users", userList);
        model.addAttribute("role", role);
        return "home";
    }

}
