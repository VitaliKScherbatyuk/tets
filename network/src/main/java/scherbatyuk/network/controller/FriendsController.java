package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import scherbatyuk.network.domain.Friends;
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

    /**
     * Метод відповідає за здійснення запиту дружби у іншого користувача
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
            friendsService.sendFriendRequest(friendId, user.getId());
            model.addAttribute("friendId", friendId);
            model.addAttribute("userId", user.getId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("Error", "Friend request not sent");
        }
        return "redirect:/home";
    }

    /**
     * Метод виводить список активних запитів на дружбу
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
        model.addAttribute("id", user.getId());
        return "answer-request";
    }

    @GetMapping("/answerAccept/{id}")
    public String acceptFriendRequest(@PathVariable Integer id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        try {
            friendsService.acceptAnswerFriend(currentUser.getId(), id);
            model.addAttribute("successMessage", "Friend request accepted successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Error accepting friend request");
        }

        return "redirect:/friends";
    }

    @GetMapping("/friends")
    public String getFriends(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        List<Friends> friendsList = user.getFriendsList();
        model.addAttribute("friends", friendsList);

        return "friends";
    }
}



