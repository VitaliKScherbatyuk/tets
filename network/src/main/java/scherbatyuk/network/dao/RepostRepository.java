package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.Repost;
import scherbatyuk.network.domain.User;

import java.util.List;

public interface RepostRepository extends JpaRepository<Repost, Integer> {
    List<Repost> getRepostsByUser(User user);
}
