/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Service class for searching users and posts.
 */
@Service
public class SearchService {

    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Searches for users based on various fields.
     * @param searchTerm The term to search for in user fields
     * @return List of users matching the search term
     */
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

    /**
     * Searches for news posts based on hashtags.
     * @param searchTerm The hashtag term to search for in posts
     * @return List of news posts matching the hashtag search term
     */
    public List<PostNews> searchPost(String searchTerm) {

        return entityManager.createQuery(
                        "SELECT p FROM PostNews p WHERE " +
                                "LOWER(p.hashTag) LIKE LOWER(CONCAT('%', :searchTerm, '%'))", PostNews.class)
                .setParameter("searchTerm", searchTerm)
                .getResultList();
    }

}
