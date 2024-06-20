package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.Repost;
import scherbatyuk.network.domain.User;

import java.util.List;
import java.util.Optional;

public interface RepostRepository extends JpaRepository<Repost, Integer> {
    List<Repost> getRepostsByUser(User user);

    Optional<Repost> findByUserAndPost(User user, PostNews post);

}
