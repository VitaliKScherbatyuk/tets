package scherbatyuk.network.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import scherbatyuk.network.domain.Message;
import scherbatyuk.network.domain.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer>, CrudRepository<Message,Integer> {

    List<Message> findByUser_IdOrderByCreateMessageDesc(Integer userId);

    int countByUser_Id(Integer userId);

    int countByFriendAndReadMessage(User friend, boolean readMessage);
}
