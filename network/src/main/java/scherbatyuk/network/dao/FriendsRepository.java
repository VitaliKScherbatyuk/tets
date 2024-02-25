package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.User;

import java.util.List;

public interface FriendsRepository extends JpaRepository<Friends, Integer>, CrudRepository<Friends, Integer>{

    List<Friends> findByFriendAndAccepted(User friend, boolean accepted);

    int countByFriendAndAcceptedAndAnswer(User friend, boolean accepted, boolean answer);

    boolean existsByFriendAndUser(User friend, User user);

}
