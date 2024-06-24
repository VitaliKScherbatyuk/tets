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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.FriendsService;
import scherbatyuk.network.service.MessageService;
import scherbatyuk.network.service.SearchService;
import scherbatyuk.network.service.UserService;

import java.util.List;

/**
 * Controller for handling search functionality.
 */
@Controller
public class SearchController {

    Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;
    @Autowired
    private UserService userService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private MessageService messageService;

    /**
     * Handles search requests. If the search term starts with "#", it searches for posts.
     * Otherwise, it searches for users. Adds the search results to the model.
     * @param searchTerm The term to search for.
     * @param model to which the search results will be added.
     * @return The view name "searchResults".
     */
    @PostMapping("/search")
    public String search(@RequestParam String searchTerm, Model model) {

        if (searchTerm.startsWith("#")) {
            try {
                List<PostNews> posts = searchService.searchPost(searchTerm);
                model.addAttribute("posts", posts);
            } catch (Exception e){
                logger.error("SearchController -> search: Error search by '#'", e);
            }
        } else {

            try {
                List<User> users = searchService.searchUsers(searchTerm);
                model.addAttribute("users", users);
            } catch (Exception e){
                logger.error("SearchController -> search: Error search by 'users'", e);
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
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

        return "searchResults";
    }

}
