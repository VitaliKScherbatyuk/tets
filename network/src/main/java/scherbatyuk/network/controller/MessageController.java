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
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    /**
     * Контроллер для збереження нового повідомлення в БД.
     *
     * @param userId
     * @param messageText
     * @param model
     * @return
     */
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
        message.setCreateMessage(LocalDateTime.now());

        // Збереження повідомлення
        messageService.saveMessage(message);

        return "redirect:/home";
    }

    /**
     * Контроллер для відображення всіх повідомлень у авторизованого користувача
     *
     * @param model
     * @return
     */
    @GetMapping("/viewMessages")
    public String viewMessages(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        List<Message> messages = messageService.getMessagesForUser(currentUser.getId());
        messages.sort(Comparator.comparing(Message::getCreateMessage).reversed());

        // Оновлення значення readMessage на true для кожного повідомлення
        for (Message message : messages) {
            message.setReadMessage(true);
            messageService.updateMessage(message); // Збереження змін у базу даних
        }

        model.addAttribute("messages", messages);
        model.addAttribute("user", currentUser);

        int age = currentUser.getAge();
        String country = currentUser.getCountry();
        String hobby = currentUser.getHobby();
        String imageData = currentUser.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        return "viewMessages";
    }


    @PostMapping("/replyMessage")
    public String replyMessage(@RequestParam("messageId") Integer messageId,
                               @RequestParam("replyText") String replyText,
                               Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        Message originalMessage = messageService.findById(messageId);

        // Створюємо нове повідомлення з відповіддю
        Message reply = new Message();
        reply.setUser(currentUser);
        reply.setFriend(originalMessage.getUser()); // Отримувач відповіді - автор початкового повідомлення
        reply.setMessage(replyText);
        reply.setCreateMessage(LocalDateTime.now());
        reply.setParentMessage(originalMessage); // Встановлюємо батьківське повідомлення для відповіді
        // Збереження відповіді
        messageService.saveMessage(reply);

        return "redirect:/viewMessages";
    }

}

