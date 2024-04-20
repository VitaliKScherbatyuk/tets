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

    /**
     * Метод для додавання запиту дружби, автоматично присвоюється значення PENDING
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

    /**
     * Метод для відображення всіх запитів дружби для залогіненого користувача.
     * На html сторінці присутні кнопки для підтвердження або відхилення дружби.
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

        return "answer-request";
    }

    /**
     * Метод для погодження або відхилення запиту дружби, яка створюється на основі прийнятого значення status.
     * Значення двох юзерів та статус передається в сервіс, який опрацьовує відповідне підтвердження або відхилення.
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
     * Метод який відображає всіх підтверджених друзів на основі фільтруванню за статусом: ACCEPTED
     * @param model
     * @return
     */
    @GetMapping("/friends")
    public String getFriends(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        // Отримуємо список друзів
        List<User> friendsListAcceped = friendsService.getFriends(user.getId());
        if (friendsListAcceped.isEmpty()) {
            return "redirect:/home";
        }
        model.addAttribute("users", friendsListAcceped);

        return "friends";
    }

}




