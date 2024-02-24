package scherbatyuk.network.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import scherbatyuk.network.domain.Message;

public interface MessageRepository extends JpaRepository<Message, Integer>, CrudRepository<Message,Integer> {
}
