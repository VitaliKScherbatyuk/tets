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
 * Spring MVC controller that handles HTTP requests and manages user interaction in your web application.
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

    /**
     * Displays the login form.
     *
     * @return the name of the start page.
     */
    @GetMapping("/")
    public String showLoginForm() {

        return "start";
    }

    /**
     * Displays the login form and initializes a User object.
     *
     * @param model to add attributes to.
     * @return the name of the login page.
     */
    @GetMapping("/login")
    public String loginForm(Model model) {

        model.addAttribute("user", new User());
        return "login";
    }

    /**
     * Authenticates the user. Redirects to the home page if successful,
     * or displays an error message on the login page if authentication fails.
     *
     * @param user    object containing login credentials.
     * @param model   to add attributes to.
     * @param request the HTTP request.
     * @return the name of the view to render.
     */
    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {
        User existingUser = userService.findByEmail(user.getEmail());

        if (existingUser != null && passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            if ("Admin".equals(existingUser.getRole())) {
                return "redirect:/admin";
            }
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid login or password.");
            return "login";
        }
    }

    /**
     * Displays the registration form and initializes a User object.
     *
     * @param model to add attributes to.
     * @return the name of the registration page.
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
     * Handles the user registration process. Saves the new user and redirects to the login page if successful.
     *
     * @param user object containing registration details.
     * @param bindingResult the binding result for validation.
     * @param session the HTTP session.
     * @param model to add attributes to.
     * @return the name of the view to render.
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

        if ("Russian Federation".equals(user.getCountry())) {
            bindingResult.rejectValue("country", "error.user", "Registration from Russia is not allowed.");
            List<String> countryNames = Arrays.stream(CountryCode.values())
                    .map(CountryCode::getName)
                    .collect(Collectors.toList());
            model.addAttribute("countries", countryNames);
            return "registration";
        }

        String verificationCode = VerificationCodeGenerator.generateCode();
        emailService.sendVerificationCode(user.getEmail(), verificationCode);

        session.setAttribute("user", user);
        session.setAttribute("verificationCode", verificationCode);

        return "redirect:/verifyCode";
    }

    /**
     * Displays the verification code form.
     *
     * @return the name of the verification code page.
     */
    @GetMapping("/verifyCode")
    public String verifyCodeForm() {
        return "verifyCode";
    }

    /**
     * Verifies the submitted verification code. Saves the user and redirects to the login page if successful.
     *
     * @param code    the verification code submitted by the user.
     * @param session the HTTP session.
     * @param model   to add attributes to.
     * @return the name of the view to render.
     */
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
     * Logs the user out and redirects to the login page.
     *
     * @param request the HTTP request.
     * @return the name of the login page.
     * @throws ServletException if logout fails.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {

        request.logout();

        return "redirect:/login";
    }

    /**
     * Displays the profile update page for the authenticated user.
     *
     * @param model     to add attributes to.
     * @param principal the authenticated user principal.
     * @return the name of the profile update page.
     */
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

    /**
     * Updates the profile of the authenticated user.
     *
     * @param updatedUser the updated user details.
     * @param principal   the authenticated user principal.
     * @param model       to add attributes to.
     * @return the name of the view to render.
     */
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

    /**
     * Updates the profile image of the authenticated user.
     *
     * @param imageData the new profile image data.
     * @param principal the authenticated user principal.
     * @return the name of the view to render.
     */
    @PostMapping("/profileImageUpdate")
    public String updateProfileImage(@RequestParam("imageData") MultipartFile[] imageData, Principal principal) {
        String userEmail = principal.getName();

        for (MultipartFile image : imageData) {
            userService.uploadImage(image, userEmail);
        }

        return "redirect:/home";
    }

    /**
     * Displays the home page with user information and posts.
     *
     * @param model to add attributes to.
     * @return the name of the home page.
     */
    @GetMapping("/home")
    public String homePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        UserRole role = user.getRole();

        List<User> friends = friendsService.getFriends(user.getId());
        List<PostNews> postFromUser = postNewsService.getPostsByUsers(friends);
        List<PostNews> myPost = postNewsService.getPostsByUser(user);
        List<Repost> repostsFromFriends = repostService.getRepostsByUser(user);

        List<PostNews> posts = new ArrayList<>(postFromUser);
        for (Repost repost : repostsFromFriends) {
            posts.add(repost.getPost());
        }
        posts.addAll(myPost);

        posts.sort(Comparator.comparing(PostNews::getAddPostNews).reversed());

        model.addAttribute("posts", posts);

        List<User> userList = userService.getAllUser();
        model.addAttribute("users", userList);
        model.addAttribute("role", role);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageDate = user.getImageData();

        model.addAttribute("user", user);
        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageDate", imageDate);

        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "home";
    }

    /**
     * Displays the user details page for the specified user.
     * @param id  the ID of the user to view.
     * @param model to add attributes to.
     * @param principal the authenticated user principal.
     * @return the name of the user details page.
     */
    @GetMapping("/user/{id}")
    public String viewUserDetails(@PathVariable Integer id, Model model, Principal principal) {

        User user = userService.getUserById(id);
        model.addAttribute("user", user);

        boolean isFriend = false;
        if (principal != null) {
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail);

            isFriend = friendsService.areFriends(currentUser.getId(), id);
        }
        model.addAttribute("isFriend", isFriend);

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

    /**
     * Deletes the account of the authenticated user.
     * @param payload the payload containing the user login.
     * @return the response entity indicating the result.
     */
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
