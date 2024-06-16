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

    public List<PostNews> getPostsByUsers(List<User> users) {
        List<PostNews> posts = new ArrayList<>();
        for (User user : users) {
            List<PostNews> userPosts = newsRepository.findByUserOrderByAddPostNewsDesc(user);
            posts.addAll(userPosts);
        }
        return posts;
    }

    public List<PostNews> getPostsByUser(User user) {
        return newsRepository.findByUser(user);
    }

    public PostNews findById(Integer postId) {
        return newsRepository.findById(postId).orElse(null);
    }

    public void save(PostNews post) {
        newsRepository.save(post);
    }
    @Transactional
    public void deletePost(Integer postId) {
        postLikesRepository.deleteByPostId(postId);
        newsRepository.deleteById(postId);
    }

    public List<String> findHashtagsByTerm(String term) {
        return entityManager.createQuery(
                        "SELECT DISTINCT p.hashTag FROM PostNews p WHERE LOWER(p.hashTag) LIKE LOWER(CONCAT('%', :term, '%'))", String.class)
                .setParameter("term", term)
                .getResultList();
    }
}
