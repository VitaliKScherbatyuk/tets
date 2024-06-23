/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

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

/**
 * Service for managing friendship requests and interactions between users.
 */
@Service
public class FriendsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendsRepository friendsRepository;

    /**
     * Sends a friend request from one user to another.
     * @param friendId ID of the user to whom the friend request is sent
     * @param userId ID of the user sending the friend request
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
     * Processes a friend request by accepting or rejecting it.
     * @param userId ID of the user accepting or rejecting the friend request
     * @param friendId ID of the user who sent the friend request
     * @param status Status of the request (ACCEPTED or REJECTED)
     */
    public void responseFriendRequest(Integer userId, Integer friendId, String status) {

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + friendId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        boolean existingFriendRequest = friendsRepository.existsByFriendAndUser(friend, user) ||
                friendsRepository.existsByFriendAndUser(user, friend);

        if (existingFriendRequest) {
            Integer requestId = friendsRepository.requestIdByFriendAndUser(user.getId(), friend.getId(), FriendshipStatus.PENDING);
            acceptOrRejectFriendshipRequest(requestId, status);
        }
    }

    /**
     * Accepts or rejects a friendship request.
     * @param requestId ID of the friendship request
     * @param status Status of the request (ACCEPTED or REJECTED)
     */
    private void acceptOrRejectFriendshipRequest(int requestId, String status) {

        Optional<Friends> friendshipRequest = friendsRepository.findById(requestId);

        friendshipRequest.ifPresent(friendship -> {
            friendship.setStatus(status.equals("ACCEPTED") ? FriendshipStatus.ACCEPTED : FriendshipStatus.REJECTED);
            friendsRepository.save(friendship);
        });
    }

    /**
     * Finds users who have sent friend requests.
     * @param userId ID of the user
     * @return List of users who have sent friend requests
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
     * Gets a list of friends of the user.
     * @param userId ID of the user
     * @return List of the user's friends
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
     * Counts the number of incoming friend requests for the user.
     * @param userId ID of the user
     * @return Number of incoming friend requests
     */
    public int countIncomingFriendRequests(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return friendsRepository.countByFriendAndStatus(user, FriendshipStatus.PENDING);
    }

    /**
     * Saves a friendship in the database.
     * @param friendship Friendship object to save
     */
    public void saveFriendship(Friends friendship) {
        friendsRepository.save(friendship);
    }

    /**
     * Checks if two users are friends.
     * @param userId ID of the first user
     * @param friendId ID of the second user
     * @return true if the users are friends, otherwise false
     */
    public boolean areFriends(Integer userId, Integer friendId) {
        return friendsRepository.areFriends(userId, friendId);
    }

    /**
     * Checks if a user is online.
     * @param user to check
     * @return true if the user is online, otherwise false
     */
    public boolean isUserOnline(User user) {

        LocalDateTime lastActivityTime = user.getLastActivityTime();
        if (lastActivityTime == null) {
            return false;
        }

        Duration duration = Duration.between(lastActivityTime, LocalDateTime.now());
        return duration.toMinutes() < 5;
    }

    /**
     * Deletes a friendship between two users.
     * @param friend Friend of the user
     * @param user User
     */
    public void deleteFriend(User friend, User user) {

        if (areFriends(friend.getId(), user.getId())) {

            Optional<Friends> friendship = friendsRepository.findByFriendAndUser(friend, user);
            if (!friendship.isPresent()) {
                friendship = friendsRepository.findByFriendAndUser(user, friend);
            }

            friendship.ifPresent(f -> {
                friendsRepository.delete(f);
            });
        }
    }

}
