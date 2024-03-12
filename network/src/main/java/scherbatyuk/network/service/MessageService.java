package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.MessageRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.FriendshipStatus;
import scherbatyuk.network.domain.Message;
import scherbatyuk.network.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    public void saveMessage(Message message) {
        // Логіка для збереження повідомлення в базі даних
        message.setCreateMessage(LocalDate.now()); // Встановлюємо поточну дату
        message.setReadMessage(false); // Позначаємо повідомлення як непрочитане

        // Перевіряємо, чи не null message.getFriend(), щоб уникнути NullPointerException
        if (message.getFriend() != null) {
            // Отримуємо друга для повідомлення
            User friend = userRepository.findById(message.getFriend().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Friend not found with id: " + message.getFriend().getId()));

            // Встановлюємо отримувача повідомлення (друга)
            message.setFriend(friend);
        }

        messageRepository.save(message);
    }

    public List<Message> getMessagesForUser(Integer friendId) {
        return messageRepository.findByFriend_Id(friendId);
    }

    public int countIncomingFriendMessage(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return messageRepository.countByFriendAndReadMessage(user, false);
    }

    public void updateMessage(Message message) {
        messageRepository.save(message);
    }

}
