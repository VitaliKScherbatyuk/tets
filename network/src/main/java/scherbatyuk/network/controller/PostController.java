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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scherbatyuk.network.domain.PostLikes;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.*;

import java.util.Comparator;
import java.util.List;

/**
 * Allows users to perform operations with posts (creation, deletion, display)
 */
@Controller
public class PostController {

    Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostNewsService postNewsService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostLikesService postLikesService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private MessageService messageService;

    /**
     * Method for displaying the add post page.
     * @param model to add attributes for rendering the view.
     * @return The view name ("addPost") to render after processing.
     */
    @GetMapping("/addPost")
    private String addPost(Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        List<PostNews> posts = postNewsService.getPostsByUser(user);
        posts.sort(Comparator.comparing(PostNews::getAddPostNews).reversed());

        model.addAttribute("posts", posts);

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

        return "addPost";
    }

    /**
     * Method for saving a new post.
     * @param images The array of Multipart files representing images to upload with the post.
     * @param postNews The text content of the post.
     * @param hashTag The hashtag associated with the post.
     * @return The redirect view name ("/home") after saving the post.
     */
    @PostMapping("/savePost")
    private String addPost(@RequestParam("image") MultipartFile[] images,
                           @RequestParam("postNews") String postNews,
                           @RequestParam("hashTag") String hashTag){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        try {
            for (MultipartFile image : images) {
                PostNews post = postNewsService.createPost(image, postNews, hashTag, user);

                PostLikes newLike = PostLikes.builder()
                        .user(user)
                        .post(post)
                        .likePost(0)
                        .build();

                postLikesService.save(newLike);
            }
        } catch (Exception e){
            logger.error("PostController -> addPost: Error add post for UserId: " + user.getId(), e);
        }

        return "redirect:/home";
    }

    /**
     * Method for deleting a post.
     * @param postId The ID of the post to delete.
     * @return The redirect view name ("/addPost") after deleting the post.
     */
    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable("id") Integer postId) {

        try {
            postNewsService.deletePost(postId);
        } catch (Exception e){
            logger.error("PostController -> deletePost: Error delete postId: " + postId, e);
        }

        return "redirect:/addPost";
    }

}
