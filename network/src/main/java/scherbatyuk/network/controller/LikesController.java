/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Processing requests related to post likes and display of post ratings in the social network.
 */
@Controller
public class LikesController {

    Logger logger = LoggerFactory.getLogger(LikesController.class);

    @Autowired
    private PostLikesService postLikesService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostNewsService postNewsService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private MessageService messageService;

    /**
     * Updates the number of likes for a specific post based on the provided payload.
     * @param payload A map containing postId and likes values sent in the request body.
     * @return ResponseEntity indicating the success of updating likes with HTTP status OK (200).
     */
    @PostMapping("/addLikeToPost")
    @Transactional
    public ResponseEntity<Void> updateLikes(@RequestBody Map<String, Integer> payload) {

        Integer postId = payload.get("postId");
        Integer likes = payload.get("likes");
        PostNews post = postNewsService.findById(postId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        try {
            post.setLikeInPost(likes);
            postNewsService.save(post);
        } catch (Exception e){
            logger.error("LikesController -> updateLikes: Error add likes from UserId: " + user.getId()+ " to postId: " + postId, e);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Retrieves the number of likes for a specific post and adds it to the model.
     * @param postId The ID of the post to retrieve likes for.
     * @param model The model to add attributes for rendering the view.
     * @return The view name ("home") to render after processing.
     */
    @PostMapping("/likeable")
    public String countLike(@RequestParam Integer postId, Model model) {

        PostNews post = postNewsService.findById(postId);
        int likePost = post.getLikeInPost();
        model.addAttribute("likeInPost", likePost);

        return "home";
    }

    /**
     * Displays a page with posts sorted by popularity based on the number of likes.
     * @param model to add attributes for rendering the view.
     * @return view name ("rating") to render after processing.
     */
    @GetMapping("/rating")
    public String ratingPost(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        List<User> friends = friendsService.getFriends(user.getId());
        List<PostNews> posts = postNewsService.getPostsByUsers(friends);

        Optional<PostNews> topPost = posts.stream()
                .sorted(Comparator.comparingInt(PostNews::getLikeInPost).reversed())
                .findFirst();

        topPost.ifPresent(post -> model.addAttribute("topPost", post));

        List<PostNews> myPosts = postNewsService.getPostsByUser(user);
        Optional<PostNews> myTopPost = myPosts.stream()
                .sorted(Comparator.comparingInt(PostNews::getLikeInPost).reversed())
                .findFirst();

        myTopPost.ifPresent(post -> model.addAttribute("myTopPost", post));

        model.addAttribute("user", user);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "rating";
    }

}

