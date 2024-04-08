package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;

import java.util.List;

public interface PostNewsRepository extends JpaRepository <PostNews, Integer> {

    List<PostNews> findByUserOrderByAddPostNewsDesc(User user);

    List<PostNews> findByUser(User user);
}
