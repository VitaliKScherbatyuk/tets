/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.MessageRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.Message;
import scherbatyuk.network.domain.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing messages between users.
 */
@Service
public class MessageService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    /**
     * Saves a message to the database.
     *
     * @param message The message to be saved
     */
    public void saveMessage(Message message) {

        message.setCreateMessage(LocalDateTime.now());
        message.setReadMessage(false);

        if (message.getFriend() != null) {
            User friend = userRepository.findById(message.getFriend().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Friend not found with id: " + message.getFriend().getId()));
            message.setFriend(friend);
        }

        messageRepository.save(message);
    }

    /**
     * Retrieves messages for a specific user by their friend's ID.
     *
     * @param friendId The ID of the friend
     * @return List of messages
     */
    public List<Message> getMessagesForUser(Integer friendId) {
        return messageRepository.findByFriend_Id(friendId);
    }

    /**
     * Counts the number of unread messages for a user.
     *
     * @param userId The ID of the user
     * @return The count of unread messages
     */
    public int countIncomingFriendMessage(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return messageRepository.countByFriendAndReadMessage(user, false);
    }

    /**
     * Updates a message in the database.
     *
     * @param message The message to be updated
     */
    public void updateMessage(Message message) {
        messageRepository.save(message);
    }

    /**
     * Finds a message by its ID.
     *
     * @param messageId The ID of the message
     * @return The found message or null if not found
     */
    public Message findById(Integer messageId) {
        return messageRepository.findById(messageId)
                .orElse(null);
    }

    /**
     * Deletes a message by its ID.
     *
     * @param id The ID of the message
     */
    public void deleteById(Integer id) {
        messageRepository.deleteById(id);
    }

    /**
     * Retrieves all messages sent to a specific user.
     *
     * @param userId The ID of the user
     * @return List of messages
     */
    public List<Message> getMessagesToUser(Integer userId) {
        return messageRepository.findAllMessageByUserId(userId);
    }

    /**
     * Deletes a reply by its ID.
     * @param replyId The ID of the reply
     */
    public void deleteReplyById(Integer replyId) {
        messageRepository.deleteById(replyId);
    }
}
