package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.PostLikesRepository;
import scherbatyuk.network.domain.PostLikes;

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

}
