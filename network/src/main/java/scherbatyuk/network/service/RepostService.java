package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.RepostRepository;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.Repost;
import scherbatyuk.network.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepostService {

    private final RepostRepository repostRepository;

    @Autowired
    public RepostService(RepostRepository repostRepository) {
        this.repostRepository = repostRepository;
    }

    public Repost repostPost(User user, PostNews post) {
        Repost repost = Repost.builder()
                .user(user)
                .post(post)
                .timestamp(LocalDateTime.now())
                .build();
        return repostRepository.save(repost);
    }

    public List<Repost> getRepostsByUser(User user) {
        return repostRepository.getRepostsByUser(user);
    }

    public boolean hasUserAlreadyReposted(User user, PostNews post) {
        return repostRepository.findByUserAndPost(user, post).isPresent();
    }
}
