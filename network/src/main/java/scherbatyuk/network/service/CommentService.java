/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.CommentRepository;
import scherbatyuk.network.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing comments.
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * Adds a new comment to the repository.
     * Sets the current time as the comment's creation time.
     * @param comment to be added
     * @return the saved Comment object
     */
    public Comment addComment(Comment comment) {
        comment.setAddCommentTime(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    /**
     * Retrieves a list of comments associated with a specific post.
     * @param postId the ID of the post for which comments are to be retrieved
     * @return a list of comments for the specified post
     */
    public List<Comment> getCommentByPost(Integer postId) {
        return commentRepository.findByPostId(postId);
    }

    /**
     * Deletes a comment from the repository by its ID.
     * @param id the ID of the comment to be deleted
     */
    public void deleteById(Integer id) {
        commentRepository.deleteById(id);
    }
}
