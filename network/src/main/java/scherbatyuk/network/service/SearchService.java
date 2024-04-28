package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> searchUsers(String searchTerm) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE " +
                                "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                                "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                                "LOWER(u.age) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                                "LOWER(u.country) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                                "LOWER(u.city) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                                "LOWER(u.hobby) LIKE LOWER(CONCAT('%', :searchTerm, '%'))"
                        , User.class)
                .setParameter("searchTerm", searchTerm)
                .getResultList();
    }

    public List<PostNews> searchPost(String searchTerm) {
        return entityManager.createQuery(
                        "SELECT p FROM PostNews p WHERE " +
//                                "LOWER(p.postNews) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
//                                "LOWER(p.encodedImage) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                                "LOWER(p.hashTag) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
//                                +"LOWER(p.user.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))"
                        , PostNews.class)
                .setParameter("searchTerm", searchTerm)
                .getResultList();
    }


}
