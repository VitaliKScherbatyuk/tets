/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import scherbatyuk.network.dao.PostLikesRepository;
import scherbatyuk.network.dao.PostNewsRepository;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing news posts.
 */
@Service
public class PostNewsService {
    @Autowired
    private PostNewsRepository newsRepository;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private PostLikesRepository postLikesRepository;
    @Autowired
    private EntityManager entityManager;

    /**
     * Creates a new news post.
     * @param image     The image file for the post (optional)
     * @param postNews  The text content of the news post
     * @param hashTag   The hashtag associated with the news post
     * @param user      The user creating the news post
     * @return The created PostNews object
     */
    public PostNews createPost(MultipartFile image, String postNews, String hashTag, User user) {

        PostNews post = new PostNews();
        post.setAddPostNews(LocalDateTime.now());
        post.setPostNews(postNews);
        post.setUser(user);
        post.setHashTag(hashTag);
        post.setLikeInPost(0);
        String encodedImage = photoService.encodeImage(image);
        post.setEncodedImage(encodedImage);

        newsRepository.save(post);
        return post;
    }

    /**
     * Retrieves posts from multiple users.
     * @param users List of users whose posts are to be retrieved
     * @return List of news posts from the specified users
     */
    public List<PostNews> getPostsByUsers(List<User> users) {

        List<PostNews> posts = new ArrayList<>();
        for (User user : users) {
            List<PostNews> userPosts = newsRepository.findByUserOrderByAddPostNewsDesc(user);
            posts.addAll(userPosts);
        }
        return posts;
    }

    /**
     * Retrieves posts by a specific user.
     * @param user The user whose posts are to be retrieved
     * @return List of news posts by the user
     */
    public List<PostNews> getPostsByUser(User user) {
        return newsRepository.findByUser(user);
    }

    /**
     * Finds a news post by its ID.
     * @param postId The ID of the news post to find
     * @return The found PostNews object, or null if not found
     */
    public PostNews findById(Integer postId) {
        return newsRepository.findById(postId).orElse(null);
    }

    /**
     * Saves a news post.
     * @param post The news post to save
     */
    public void save(PostNews post) {
        newsRepository.save(post);
    }

    /**
     * Deletes a news post by its ID. Also deletes associated likes.
     * @param postId The ID of the news post to delete
     */
    @Transactional
    public void deletePost(Integer postId) {

        postLikesRepository.deleteByPostId(postId);
        newsRepository.deleteById(postId);
    }

    /**
     * Finds hashtags containing a given term.
     * @param term The term to search for within hashtags
     * @return List of hashtags containing the term
     */
    public List<String> findHashtagsByTerm(String term) {
        return entityManager.createQuery(
                        "SELECT DISTINCT p.hashTag FROM PostNews p WHERE LOWER(p.hashTag) LIKE LOWER(CONCAT('%', :term, '%'))", String.class)
                .setParameter("term", term)
                .getResultList();
    }
}
