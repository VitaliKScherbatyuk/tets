/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 24.12.2023
 */

package scherbatyuk.network.controller;

import com.neovisionaries.i18n.CountryCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scherbatyuk.network.config.VerificationCodeGenerator;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.*;
import scherbatyuk.network.service.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Spring MVC controller that handles HTTP requests and manages
 * user interaction in your web application
 */
@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private PostNewsService postNewsService;
    @Autowired
    private PostLikesService postLikesService;
    @Autowired
    private RepostService repostService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String showLoginForm() {
        return "start";
    }

    /**
     * method is responsible for processing HTTP GET requests to the URL path "/login".
     * It initializes a User object and returns a "login" page along with a model
     * that contains that object to create a login form
     *
     * @param model
     * @return
     */
    @GetMapping("/login")
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
    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {
        User existingUser = userService.findByEmail(user.getEmail());

        if (existingUser != null && passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            if ("Admin".equals(existingUser.getRole())) {
                return "redirect:/users";
            }
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

        List<String> countryNames = Arrays.stream(CountryCode.values())
                .map(CountryCode::getName)
                .collect(Collectors.toList());
        model.addAttribute("countries", countryNames);

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
    public String registrationSubmit(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {

            List<String> countryNames = Arrays.stream(CountryCode.values())
                    .map(CountryCode::getName)
                    .collect(Collectors.toList());
            model.addAttribute("countries", countryNames);

            return "registration";
        }

//        String verificationCode = VerificationCodeGenerator.generateCode();
//        emailService.sendVerificationCode(user.getEmail(), verificationCode);
//
//        session.setAttribute("user", user);
//        session.setAttribute("verificationCode", verificationCode);
//
//        return "redirect:/verifyCode";

        if ("Russian Federation".equals(user.getCountry())) {
            bindingResult.rejectValue("country", "error.user", "Registration from Russia is not allowed.");
            List<String> countryNames = Arrays.stream(CountryCode.values())
                    .map(CountryCode::getName)
                    .collect(Collectors.toList());
            model.addAttribute("countries", countryNames);
            return "registration";
        }

        userService.save(user);
        return "redirect:/";
    }

    @GetMapping("/verifyCode")
    public String verifyCodeForm() {
        return "verifyCode";
    }

    @PostMapping("/verifyCode")
    public String verifyCodeSubmit(@RequestParam("code") String code, HttpSession session, Model model) {
        String storedCode = (String) session.getAttribute("verificationCode");
        User user = (User) session.getAttribute("user");

        if (storedCode != null && storedCode.equals(code)) {
            userService.save(user);
            session.removeAttribute("verificationCode");
            session.removeAttribute("user");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Invalid verification code.");
            return "verifyCode";
        }
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
    public String showProfileUpdatePage(Model model, Principal principal) {
        String userEmail = principal.getName();
        User user = userService.findByEmail(userEmail);

        model.addAttribute("user", user);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "profileUpdate";
    }

    @PostMapping("/profileUpdate")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Principal principal, Model model) {
        String userEmail = principal.getName();
        User existingUser = userService.findByEmail(userEmail);
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setAge(updatedUser.getAge());
            existingUser.setCountry(updatedUser.getCountry());
            existingUser.setCity(updatedUser.getCity());
            existingUser.setHobby(updatedUser.getHobby());
            try {
                userService.updateProfile(Collections.singletonList(existingUser));
            } catch (Exception e) {
                model.addAttribute("error", "Your profile is nit update");
            }
        }
        return "redirect:/home";
    }

    @PostMapping("/profileImageUpdate")
    public String updateProfileImage(@RequestParam("imageData") MultipartFile[] imageData, Principal principal) {
        String userEmail = principal.getName();

        for (MultipartFile image : imageData) {
            userService.uploadImage(image, userEmail); // передайте зображення, а не масив зображень
        }
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

        List<User> friends = friendsService.getFriends(user.getId()); // отримати список друзів користувача
        List<PostNews> postFromUser = postNewsService.getPostsByUsers(friends); // отримати пости друзів
        List<PostNews> myPost = postNewsService.getPostsByUser(user);
        List<Repost> repostsFromFriends = repostService.getRepostsByUser(user);

        List<PostNews> posts = new ArrayList<>(postFromUser);
        for (Repost repost : repostsFromFriends) {
            posts.add(repost.getPost());
        }
        posts.addAll(myPost);

        posts.sort(Comparator.comparing(PostNews::getAddPostNews).reversed()); // сортувати по даті

        model.addAttribute("posts", posts); // додати пости в модель

        List<User> userList = userService.getAllUser();
        model.addAttribute("users", userList);
        model.addAttribute("role", role);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageDate = user.getImageData();
        model.addAttribute("user", user); // Додати об'єкт користувача в модель
        model.addAttribute("age", age); // Додати вік в модель
        model.addAttribute("country", country); // Додати країну в модель
        model.addAttribute("hobby", hobby); // Додати хобі в модель
        model.addAttribute("imageDate", imageDate);

        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

            return "home";
    }

    @GetMapping("/user/{id}")
    public String viewUserDetails(@PathVariable Integer id, Model model, Principal principal) {

        User user = userService.getUserById(id);
        model.addAttribute("user", user);

        // Перевірка чи авторизований користувач є товаришем користувача, що відображається на сторінці
        boolean isFriend = false;
        if (principal != null) {
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail);

            isFriend = friendsService.areFriends(currentUser.getId(), id);
        }
        model.addAttribute("isFriend", isFriend);

        // Додайте новий об'єкт FriendsRequest до моделі для використання в формі
        Friends friendsRequest = new Friends();
        friendsRequest.setId(id);
        model.addAttribute("friendsRequest", friendsRequest);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User userMy = userService.findByEmail(userEmail);

        int countRequests = friendsService.countIncomingFriendRequests(userMy.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(userMy.getId());
        model.addAttribute("countMessages", countMessages);

        return "userDetail";
    }

    @PostMapping("/deleteAccount")
    @ResponseBody
    public ResponseEntity<String> deleteAccount(@RequestBody Map<String, String> payload) {
        String login = payload.get("login").trim();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName().trim();
        User user = userService.findByEmail(userEmail);

        if (login.equals(userEmail)) {
            userService.deleteUser(user);
            SecurityContextHolder.getContext().setAuthentication(null);

            return ResponseEntity.ok("redirect:/");
        } else {
            return ResponseEntity.ok("redirect:/profileUpdate");
        }
    }

}
