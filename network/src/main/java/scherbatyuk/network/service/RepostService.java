/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.PostNewsRepository;
import scherbatyuk.network.dao.RepostRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.Repost;
import scherbatyuk.network.domain.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing reposts of news posts.
 */
@Service
public class RepostService {

    private final RepostRepository repostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostNewsRepository postNewsRepository;

    @Autowired
    public RepostService(RepostRepository repostRepository) {
        this.repostRepository = repostRepository;
    }

    /**
     * Reposts a news post.
     * @param user The user reposting the news post
     * @param post The news post being reposted
     * @return The created Repost object
     */
    public Repost repostPost(User user, PostNews post) {

        userRepository.save(user);
        postNewsRepository.save(post);

        Repost repost = Repost.builder()
                .user(user)
                .post(post)
                .timestamp(LocalDateTime.now())
                .build();

        return repostRepository.save(repost);
    }

    /**
     * Retrieves all reposts by a specific user.
     * @param user The user whose reposts are to be retrieved
     * @return List of reposts by the user
     */
    public List<Repost> getRepostsByUser(User user) {
        userRepository.save(user);
        return repostRepository.getRepostsByUser(user);
    }

    /**
     * Checks if a user has already reposted a specific news post.
     * @param user The user to check for reposting
     * @param post The news post to check
     * @return true if the user has reposted the post, false otherwise
     */
    public boolean hasUserAlreadyReposted(User user, PostNews post) {
        userRepository.save(user);
        postNewsRepository.save(post);
        return repostRepository.findByUserAndPost(user, post).isPresent();
    }

    /**
     * Deletes a repost by its ID.
     * @param id The ID of the repost to delete
     */
    public void deleteById(Integer id) {
        repostRepository.deleteById(id);
    }
}
