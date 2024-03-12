package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scherbatyuk.network.domain.Message;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.MessageService;
import scherbatyuk.network.service.UserService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam("userId") Integer userId,
                              @RequestParam("messageText") String messageText,
                              Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        User friend = userService.findById(userId);

        Message message = new Message();
        message.setUser(currentUser);
        message.setFriend(friend);
        message.setMessage(messageText);
        message.setCreateMessage(LocalDate.now());

        // Збереження повідомлення
        messageService.saveMessage(message);

        return "redirect:/home";
    }

    @GetMapping("/viewMessages")
    public String viewMessages(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        List<Message> messages = messageService.getMessagesForUser(currentUser.getId());

        // Оновлення значення readMessage на true для кожного повідомлення
        for (Message message : messages) {
            message.setReadMessage(true);
            messageService.updateMessage(message); // Збереження змін у базу даних
        }
        model.addAttribute("messages", messages);

        return "viewMessages";
    }

}

