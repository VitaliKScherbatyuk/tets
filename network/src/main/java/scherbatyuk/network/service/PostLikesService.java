/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.PostLikesRepository;
import scherbatyuk.network.domain.PostLikes;

import java.util.List;

/**
 * Service class for managing post likes.
 */
@Service
public class PostLikesService {

    @Autowired
    private PostLikesRepository postLikesRepository;

    /**
     * Saves a new post like.
     * @param newLike The PostLikes entity to save
     */
    public void save(PostLikes newLike) {
        postLikesRepository.save(newLike);
    }

    /**
     * Retrieves all likes for a specific post.
     * @param postId The ID of the post
     * @return List of likes for the post
     */
    public List<PostLikes> getLikesByPost(Integer postId) {
        return postLikesRepository.findByPostId(postId);
    }

    /**
     * Deletes a post like by its ID.
     * @param postLikeId The ID of the post like to delete
     */
    public void deleteById(Integer postLikeId) {
        postLikesRepository.deleteById(postLikeId);
    }
}
