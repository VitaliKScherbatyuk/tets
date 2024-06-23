/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

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
 * Is responsible for processing requests related to the friendship of users in the social network.
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
     * A method to send a friend request between a user and another user.
     * Creates a Friends object representing the friend request and stores it in the database.
     * Sends a friend request message.
     * @param friendId the ID of the user to send the friend request to
     * @param model for transferring data to the page
     * @return redirect to /home page
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
     * Display all friend requests for an authorized user.
     * Returns a page where the user can approve or decline friend requests.
     * @param model for transferring data to the page
     * @return the name of the view to display the friend request page
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
     * Handling responses to friend requests.
     * Changes the friendship status depending on whether the request was accepted.
     * @param id of the user who sent the friend request
     * @param status of the response to the friend request (accepted / rejected)
     * @param model for transferring data to the page
     * @return redirect to /home page
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
     * Display of all verified friends of the user.
     * Gets a list of friends and their online status.
     * Returns a page with a list of friends and their online status.
     * @param model for transferring data to the page
     * @return the name of the view to display the friends list page
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
     * @param id dynamic parameter that contains the user ID.
     * @param model is used to transfer data to the page.
     * @return friendliesPage
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




