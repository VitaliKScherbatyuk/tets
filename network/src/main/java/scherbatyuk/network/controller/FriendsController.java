package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.FriendshipStatus;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.FriendsService;
import scherbatyuk.network.service.UserService;

import java.util.List;


@Controller
public class FriendsController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendsService friendsService;

    @PostMapping("/addFriends")
    public String sendFriendRequest(@RequestParam Integer friendId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        try {
            // Створення та збереження дружби
            Friends friendship = new Friends();
            friendship.setUser(user);
            User friend = userService.findById(friendId);
            friendship.setFriend(friend);
            friendship.setStatus(FriendshipStatus.PENDING);
            friendsService.saveFriendship(friendship);

            // Виклик методу для відправки запиту на дружбу
            friendsService.sendFriendRequest(friendId, user.getId());

            model.addAttribute("friendId", friendId);
            model.addAttribute("userId", user.getId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("Error", "Friend request not sent");
        }
        return "redirect:/home";
    }


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

//        List<Integer> friendIds = friendsService.getFriendIds(friendsList);
//        model.addAttribute("friendIds", friendIds);

        return "answer-request";
    }

    @PostMapping("/answerAccept/{id}")
    public String acceptFriendRequest(@PathVariable Integer id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        friendsService.acceptFriendRequest(currentUser.getId(), id);
        return "redirect:/friend";
    }

    @GetMapping("/friends")
    public String getFriends(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        System.err.println(user.getName());

        // Отримуємо список друзів
        List<User> friendsListAcceped = friendsService.getFriends(user.getId());
        if (friendsListAcceped.isEmpty()) {
            return "redirect:/home";
        }
        model.addAttribute("users", friendsListAcceped);

        return "friends";
    }
}




