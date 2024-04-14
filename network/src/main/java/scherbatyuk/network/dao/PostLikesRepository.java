package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.PostLikes;

import java.util.List;

public interface PostLikesRepository extends JpaRepository <PostLikes, Integer> {


    PostLikes findByPostIdAndUserId(Integer postId, Integer userId);

}
