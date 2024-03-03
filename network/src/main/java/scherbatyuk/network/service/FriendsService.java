package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.FriendsRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.FriendshipStatus;
import scherbatyuk.network.domain.User;

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

    public void acceptFriendRequest(Integer userId, Integer friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + friendId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        boolean existingFriendRequest = friendsRepository.existsByFriendAndUser(friend, user) ||
                friendsRepository.existsByFriendAndUser(user, friend);

        if (existingFriendRequest) {
            // Знайдемо id запиту дружби
            Integer requestId = friendsRepository.requestIdByFriendAndUser(user.getId(), friend.getId(), FriendshipStatus.PENDING);
            System.err.println(requestId);

            // Прийняти запит дружби, змінивши його статус
            acceptFriendshipRequest(requestId);
        }
    }

    private void acceptFriendshipRequest(int requestId) {
        Optional<Friends> friendshipRequest = friendsRepository.findById(requestId);

        friendshipRequest.ifPresent(friendship -> {
            // Здійснюйте потрібні дії для прийняття запиту дружби, наприклад, змінюйте статус
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendsRepository.save(friendship);
        });
    }


    public List<User> findUsersWithFriendRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        List<Friends> friendRequests = friendsRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING);

        return friendRequests.stream()
                .map(Friends::getUser)
                .collect(Collectors.toList());
    }

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



    public int countIncomingFriendRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return friendsRepository.countByFriendAndStatus(user, FriendshipStatus.PENDING);
    }

    public void saveFriendship(Friends friendship) {
        // Логіка для збереження дружби в базі даних
        friendsRepository.save(friendship);
    }
}
