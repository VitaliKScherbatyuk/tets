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
import scherbatyuk.network.domain.Message;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.FriendsService;
import scherbatyuk.network.service.MessageService;
import scherbatyuk.network.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Allows users to send messages to each other, view and reply to their messages.
 */
@Controller
public class MessageController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;
    @Autowired
    private FriendsService friendsService;

    /**
     * Controller method for saving a new message in the database.
     * @param userId The ID of the user to whom the message is being sent.
     * @param messageText The text content of the message.
     * @param model to add attributes for rendering the view.
     * @return the redirect view name ("/home") after saving the message.
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

        messageService.saveMessage(message);

        return "redirect:/home";
    }

    /**
     * Controller method for displaying all messages for the authenticated user.
     * @param model to add attributes for rendering the view.
     * @return The view name ("viewMessages") to render after processing.
     */
    @GetMapping("/viewMessages")
    public String viewMessages(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        List<Message> messages = messageService.getMessagesForUser(currentUser.getId());
        messages.sort(Comparator.comparing(Message::getCreateMessage).reversed());

        for (Message message : messages) {
            message.setReadMessage(true);
            messageService.updateMessage(message);
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

        List<User> friends = friendsService.getFriends(currentUser.getId());
        int countRequests = friendsService.countIncomingFriendRequests(currentUser.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(currentUser.getId());
        model.addAttribute("countMessages", countMessages);

        return "viewMessages";
    }

    /**
     * Method for replying to a message.
     * @param messageId The ID of the message to reply to.
     * @param replyText The text content of the reply.
     * @param model to add attributes for rendering the view.
     * @return The redirect view name ("/viewMessages") after saving the reply.
     */
    @PostMapping("/replyMessage")
    public String replyMessage(@RequestParam("messageId") Integer messageId,
                               @RequestParam("replyText") String replyText,
                               Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        Message originalMessage = messageService.findById(messageId);

        Message reply = new Message();
        reply.setUser(currentUser);
        reply.setFriend(originalMessage.getUser());
        reply.setMessage(replyText);
        reply.setCreateMessage(LocalDateTime.now());
        reply.setParentMessage(originalMessage);

        messageService.saveMessage(reply);

        return "redirect:/viewMessages";
    }

}

