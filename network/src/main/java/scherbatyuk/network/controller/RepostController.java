/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.Repost;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.RepostService;
import scherbatyuk.network.service.UserService;

import java.util.Map;

/**
 * Controller for handling repost actions.
 */
@RestController
public class RepostController {

    private final RepostService repostService;

    @Autowired
    public RepostController(RepostService repostService) {
        this.repostService = repostService;
    }

    @Autowired
    private UserService userService;
    @Autowired
    private PostNewsService postNewsService;

    /**
     * Endpoint to create a repost of a post identified by postId.
     * @param postIdMap A map containing postId as Integer.
     * @return ResponseEntity with created Repost object or error message if user or post not found.
     */
    @PostMapping("/createRepost")
    public ResponseEntity<?> repostPost(@RequestBody Map<String, Integer> postIdMap) {
        Integer postId = postIdMap.get("postId");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        PostNews post = postNewsService.findById(postId);
        boolean flagPost = repostService.hasUserAlreadyReposted(user, post);
        Repost repost;
        if (flagPost) {
            repost = repostService.repostPost(user, post);
        } else {
            return new ResponseEntity<>("User or post not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(repost, HttpStatus.CREATED);
    }

}