package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.PostLikesRepository;
import scherbatyuk.network.domain.PostLikes;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;

import java.util.List;

@Service
public class PostLikesService {

    @Autowired
    private PostLikesRepository postLikesRepository;

    public PostLikes findByPostAndUser(Integer postId, Integer userId) {
        return postLikesRepository.findByPostIdAndUserId(postId, userId);
    }

    public void save(PostLikes newLike) {
        postLikesRepository.save(newLike);
    }

    public List<PostLikes> getLikesByPost(Integer postId) {
        return postLikesRepository.findByPostId(postId);
    }

    public void deleteById(Integer postLikeId) {
        postLikesRepository.deleteById(postLikeId);
    }
}
