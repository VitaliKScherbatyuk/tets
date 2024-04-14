package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scherbatyuk.network.domain.PostLikes;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.PostLikesService;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.UserService;

import java.util.Map;

@Controller
public class LikesController {

    @Autowired
    private PostLikesService postLikesService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostNewsService postNewsService;

    @PostMapping("/addLikeToPost")
    @Transactional
    public ResponseEntity<Void> updateLikes(@RequestBody Map<String, Integer> payload) {
        Integer postId = payload.get("postId");
        Integer likes = payload.get("likes");
        PostNews post = postNewsService.findById(postId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        // Перевірка наявності запису PostLikes з заданими postId та userId
        PostLikes existingLike = postLikesService.findByPostAndUser(postId, user.getId());

        if (existingLike == null) {

            PostLikes newLike = PostLikes.builder()
                    .user(user)
                    .post(post)
                    .likePost(1)
                    .build();

            // Збереження нового лайку
            postLikesService.save(newLike);
            post.setLikeInPost(post.getLikeInPost() + 1);
            postNewsService.save(post);
        } else {
            if (existingLike.getLikePost() == 1) {
                existingLike.setLikePost(0);
                post.setLikeInPost(post.getLikeInPost() - 1);

            } else if (existingLike.getLikePost() == 0) {
                existingLike.setLikePost(1);
                post.setLikeInPost(post.getLikeInPost() + 1);
            }
            postNewsService.save(post);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * відображення кількості лайків під постом
     * @param postId
     * @param model
     * @return
     */
    @PostMapping("/likeable")
    public String countLike(@RequestParam Integer postId, Model model){
        PostNews post = postNewsService.findById(postId);
        int likePost = post.getLikeInPost();
        model.addAttribute("likeInPost", likePost);


        return "home";
    }

}

