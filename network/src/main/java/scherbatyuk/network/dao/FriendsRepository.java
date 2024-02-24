package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import scherbatyuk.network.domain.Friends;
import scherbatyuk.network.domain.User;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Integer>, CrudRepository<Friends, Integer>{
    List<Friends> findByUser(User user);

    List<Friends> findByUserAndAccepted(User user, boolean accepted);

    int countByUserAndAcceptedAndAnswer(User user, boolean b, boolean b1);
}
