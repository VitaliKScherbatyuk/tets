package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.FriendsRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.FriendshipStatus;
import scherbatyuk.network.domain.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    /**
     * Метод що відповідає за створення запиту дружби між користувачами
     * @param friendId
     * @param userId
     */
    public void sendFriendRequest(Integer friendId, Integer userId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Sender with id " + friendId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Sender with id " + userId + " not found"));

        boolean existingFriendRequest = friendsRepository.existsByFriendAndUser(friend, user) ||
                friendsRepository.existsByFriendAndUser(user, friend);

        if (!friend.equals(user) && !existingFriendRequest) {
            Friends friends = new Friends();
            friends.setFriend(friend);
            friends.setStatus(FriendshipStatus.PENDING);
            friends.setUser(user);
            friendsRepository.save(friends);
        }
    }

    /**
     * Метод що відповідає за погодження або відхилення дружби двох користувачів на основі id та статусу із html
     * @param userId
     * @param friendId
     * @param status
     */
    public void responseFriendRequest(Integer userId, Integer friendId, String status) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + friendId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        boolean existingFriendRequest = friendsRepository.existsByFriendAndUser(friend, user) ||
                friendsRepository.existsByFriendAndUser(user, friend);

        if (existingFriendRequest) {
            // Знайдемо id запиту дружби
            Integer requestId = friendsRepository.requestIdByFriendAndUser(user.getId(), friend.getId(), FriendshipStatus.PENDING);

            // Прийняти або відхилити запит дружби
            acceptOrRejectFriendshipRequest(requestId, status);
        }
    }

    /**
     * Метод що змінює статус в БД на основі статусу що надійшов для індентифікації прийняття або відхилення дружби
     * @param requestId
     * @param status
     */
    private void acceptOrRejectFriendshipRequest(int requestId, String status) {
        Optional<Friends> friendshipRequest = friendsRepository.findById(requestId);

        friendshipRequest.ifPresent(friendship -> {
            // Змінюємо статус на ACCEPTED або REJECTED
            friendship.setStatus(status.equals("ACCEPTED") ? FriendshipStatus.ACCEPTED : FriendshipStatus.REJECTED);
            friendsRepository.save(friendship);
        });
    }

    /**
     * Метод що повертає на сторінку answer-request.html всі не підтверджені запити дружби
     * @param userId
     * @return
     */
    public List<User> findUsersWithFriendRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        List<Friends> friendRequests = friendsRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING);

        return friendRequests.stream()
                .map(Friends::getUser)
                .collect(Collectors.toList());
    }

    /**
     * Метод що повертає всіх підтерджених друзів залогіненого користувача
     * @param userId
     * @return
     */
    public List<User> getFriends(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        List<Friends> friendReq = friendsRepository.findByFriendAndStatus(user, FriendshipStatus.ACCEPTED);
        List<Friends> userReq = friendsRepository.findByUserAndStatus(user, FriendshipStatus.ACCEPTED);

        List<Friends> friendRequests = new ArrayList<>();
        friendRequests.addAll(friendReq);
        friendRequests.addAll(userReq);

        List<User> friendsList = friendRequests.stream()
                .map(friendRequest -> user.equals(friendRequest.getUser()) ? friendRequest.getFriend() : friendRequest.getUser())
                .collect(Collectors.toList());

        return friendsList;
    }

    /**
     * Метод що підраховує кількість запитів дружби
     * @param userId
     * @return
     */
    public int countIncomingFriendRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return friendsRepository.countByFriendAndStatus(user, FriendshipStatus.PENDING);
    }

    /**
     * Метод що відповідає за збереження зміни в БД
     * @param friendship
     */
    public void saveFriendship(Friends friendship) {
        // Логіка для збереження дружби в базі даних
        friendsRepository.save(friendship);
    }

    public boolean areFriends(Integer userId, Integer friendId) {
        // Логика проверки, являются ли пользователи друзьями
        return friendsRepository.areFriends(userId, friendId);
    }


    private boolean isUserOnline(User user) {
        // В цьому прикладі, якщо користувач має останню активність менше ніж 5 хвилин тому, ми вважаємо його "онлайн"
        // Ви можете змінити цю логіку відповідно до вашого варіанту реалізації

        // Отримати час останньої активності користувача (це може бути власне поле в об'єкті User або інші дані про активність)
        LocalDateTime lastActivityTime = user.getLastActivityTime(); // Припустимо, що це поле дати і часу останньої активності користувача

        // Порівняти час останньої активності з поточним часом
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(lastActivityTime, currentTime);

        // Перевірити, чи була активність менше ніж 5 хвилин тому
        // Якщо так, повернути true, що вказує на "онлайн" статус, в іншому випадку, повернути false
        return duration.toMinutes() < 5; // Залежно від вашого варіанту, ви можете вибрати інтервал часу для визначення "онлайн" статусу
    }


}
