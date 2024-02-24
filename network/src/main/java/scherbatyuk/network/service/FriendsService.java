package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.FriendsRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    public void sendFriendRequest(Integer friendId, Integer userId) {
        // Перевірка наявності користувачів
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Sender with id " + friendId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Sender with id " + userId + " not found"));

        if(!friend.equals(user)){
            // Відправлення запиту
            Friends friends = new Friends();
            friends.setFriend(friend);
            friends.setAccepted(false);// Запит не прийнято
            friends.setUser(user);
            friends.setAnswer(false);
            friendsRepository.save(friends);
        }
    }

    public void acceptFriendRequest(Integer userId, Integer friendId) {
        // Перевірка користувача, що подав запит
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        // Перевірка запиту користувача що отримав запит
        Friends friendNew = friendsRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request with id " + friendId + " not found"));

        // Перевірка, чи користувач не відправляє собі запит
        if (friendNew.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cannot accept your own friend request");
        }

        // Оновлення списків друзів
        user.getFriendsList().add(friendNew);
        friendNew.setAccepted(true);
        friendNew.setAnswer(true);
        friendsRepository.save(friendNew);

//        friendNew.getFriend().getFriendsList().add(user);
//        userRepository.save(friendNew.getFriend());
//
//        userRepository.save(user);
    }

    public int countPendingFriendRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return friendsRepository.countByUserAndAcceptedAndAnswer(user, false, false);
    }

    public List<User> findUsersWithFriendRequests(Integer userId) {
        // Перевірка користувача
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        // Отримання списку користувачів, які пропонують дружбу
        List<Friends> friendRequests = friendsRepository.findByUserAndAccepted(user, false);

        // Вибірка користувачів із списку запитів
        return friendRequests.stream()
                .map(Friends::getUser)
                .collect(Collectors.toList());
    }

    public int countUsersWithFriendRequests(Integer userId) {
        return findUsersWithFriendRequests(userId).size();
    }

    public void acceptAnswerFriend(Integer userId, Integer friendId){
        Optional<User> user = userRepository.findById(userId);
        Optional<User> friendNew = userRepository.findById(friendId);
        if((user.isEmpty() || friendNew.isEmpty() || friendNew.equals(user))){
            throw new IllegalArgumentException("Cannot accept your own friend request");
        }

    }
}