/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.PostLikes;

import java.util.List;

public interface PostLikesRepository extends JpaRepository <PostLikes, Integer> {

    PostLikes findByPostIdAndUserId(Integer postId, Integer userId);

    void deleteByPostId(Integer postId);

    List<PostLikes> findByPostId(Integer postId);
}
