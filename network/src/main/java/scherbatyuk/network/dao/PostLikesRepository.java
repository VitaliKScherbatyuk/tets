package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import scherbatyuk.network.domain.PostLikes;
import scherbatyuk.network.domain.User;

import java.util.List;

public interface PostLikesRepository extends JpaRepository <PostLikes, Integer> {


    PostLikes findByPostIdAndUserId(Integer postId, Integer userId);

    void deleteByPostId(Integer postId);

    List<PostLikes> findByPostId(Integer postId);
}
