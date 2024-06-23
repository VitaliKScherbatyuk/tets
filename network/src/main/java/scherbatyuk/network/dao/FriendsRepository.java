/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.FriendshipStatus;
import scherbatyuk.network.domain.User;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Integer>, CrudRepository<Friends, Integer> {

    List<Friends> findByFriendAndStatus(User friend, FriendshipStatus status);

    List<Friends> findByUserAndStatus(User user, FriendshipStatus status);

    int countByFriendAndStatus(User friend, FriendshipStatus status);

    boolean existsByFriendAndUser(User friend, User user);

    @Query("SELECT f.id FROM Friends f WHERE ((f.user.id = :userId AND f.friend.id = :friendId) OR (f.user.id = :friendId AND f.friend.id = :userId)) AND f.status = :status")
    Integer requestIdByFriendAndUser(@Param("userId") Integer userId, @Param("friendId") Integer friendId, @Param("status") FriendshipStatus status);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM Friends f WHERE ((f.user.id = ?1 AND f.friend.id = ?2) OR (f.user.id = ?2 AND f.friend.id = ?1)) AND f.status = 'ACCEPTED'")
    boolean areFriends(Integer userId, Integer friendId);

    Optional<Friends> findByFriendAndUser(User friend, User user);
}

