package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.CommentRepository;
import scherbatyuk.network.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;


    public Comment addComment(Comment comment) {
        comment.setAddCommentTime(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentByPost(Integer postId) {
        // Викликаємо метод репозиторію для отримання коментарів за ідентифікатором посту
        return commentRepository.findByPostId(postId);
    }
}
