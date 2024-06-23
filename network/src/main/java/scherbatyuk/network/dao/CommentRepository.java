/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByPostId(Integer postId);

}
