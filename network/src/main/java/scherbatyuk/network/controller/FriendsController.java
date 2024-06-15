package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.FriendshipStatus;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.FriendsService;
import scherbatyuk.network.service.MessageService;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.UserService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * responsible for handling HTTP requests related to a user's friends, including adding friends,
 * responding to friend requests, and displaying a list of friends
 */
@Controller
public class FriendsController {

    @Autowired
    private UserService userService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private PostNewsService postNewsService;
    @Autowired
    private MessageService messageService;

    /**
     * the method handles sending a friend request between a user and another user;
     * Creates a Friends object that represents the friend request and stores it in the database;
     * А friend request message is sent
     * @param friendId
     * @param model
     * @return
     */
    @PostMapping("/addFriends")
    public String sendFriendRequest(@RequestParam Integer friendId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        try {
            Friends friendship = new Friends();
            friendship.setUser(user);
            User friend = userService.findById(friendId);
            friendship.setFriend(friend);
            friendship.setStatus(FriendshipStatus.PENDING);
            friendsService.saveFriendship(friendship);

            friendsService.sendFriendRequest(friendId, user.getId());

            model.addAttribute("friendId", friendId);
            model.addAttribute("userId", user.getId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("Error", "Friend request not sent");
        }
        return "redirect:/home";
    }

    /**
     * Displays all friend requests for the logged in user.
     * Returns a page where the user can approve or decline friend requests.
     * @param model
     * @return
     */
    @GetMapping("/answer-request")
    public String responseToFriendRequest(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        List<User> friendsList = friendsService.findUsersWithFriendRequests(user.getId());
        if (friendsList.isEmpty()) {
            return "redirect:/home";
        }
        model.addAttribute("users", friendsList);

        model.addAttribute("user", user);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        List<User> friends = friendsService.getFriends(user.getId());
        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "answer-request";
    }

    /**
     * Handles responses to friend requests.
     * Changes the friendship status depending on whether the request was accepted
     * @param id
     * @param model
     * @return
     */
    @PostMapping("/responseRequest/{id}")
    public String responseToFriendRequest(@PathVariable Integer id, @RequestParam String status, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        friendsService.responseFriendRequest(currentUser.getId(), id, status);

        return "redirect:/home";
    }

    /**
     * Displays all verified friends of the user.
     * Gets a list of friends and their online status.
     * Returns a page with a list of friends and their online status
     * @param model
     * @return
     */
    @GetMapping("/friends/{id}")
    public String getFriends(@PathVariable Integer id, Model model) {
        User user = userService.findById(id);

        List<User> friendsListAcceped = friendsService.getFriends(user.getId());
        if (friendsListAcceped.isEmpty()) {
            return "redirect:/home";
        }

        Map<User, Boolean> friendsOnlineStatus = new HashMap<>();
        for (User friend : friendsListAcceped) {
            friendsOnlineStatus.put(friend, friendsService.isUserOnline(friend));
        }

        model.addAttribute("users", friendsListAcceped);
        model.addAttribute("friendsOnlineStatus", friendsOnlineStatus);

        model.addAttribute("user", user);
        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        List<User> friends = friendsService.getFriends(user.getId());
        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "friends";
    }

    /**
     * Displays a page with the posts of the user's friends.
     * Gets a list of the user's friends and their posts, sorted by date.
     * Adds information about the user (age, country, hobbies, etc.) and their posts to the model
     * to be displayed on the page.
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/friendliesPage/{id}")
    public String friendliesPage (@PathVariable Integer id, Model model){
        User user = userService.findById(id);
        List<User> friends = friendsService.getFriends(user.getId());
        List<PostNews> posts = postNewsService.getPostsByUsers(friends);
        posts.sort(Comparator.comparing(PostNews::getAddPostNews).reversed());

        model.addAttribute("posts", posts);

        model.addAttribute("user", user);
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

        return "friendliesPage";
    }

}




