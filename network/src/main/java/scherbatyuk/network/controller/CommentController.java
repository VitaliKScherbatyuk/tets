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
import scherbatyuk.network.domain.Comment;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Is responsible for processing requests related to comments on posts on the page.
 */
@Controller
public class CommentController {

    Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private PostNewsService postNewsService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private EmailService emailService;

    /**
     * Gets the post and all its comments from the database and passes them to the "comment" view for display.
     * @param id post identifier
     * @param model for transferring data to type
     * @return the name of the view to display the comments
     */
    @GetMapping("/comment/{id}")
    public String commentPage(@PathVariable Integer id, Model model) {

        PostNews post = postNewsService.findById(id);
        List<Comment> comments = commentService.getCommentByPost(post.getId());
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
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

        return "comment";
    }

    /**
     * Method for adding a new comment to a post.
     * @param postId the ID of the post to which the comment is added
     * @param commentText comment text
     * @return redirection to the comments page for the corresponding post
     */
    @PostMapping("/addComment")
    public String addComment(@RequestParam("postId") Integer postId,
                             @RequestParam("commentText") String commentText) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        PostNews post = postNewsService.findById(postId);

        try {
            Comment comment = Comment.builder()
                    .user(user)
                    .post(post)
                    .commentText(commentText)
                    .addCommentTime(LocalDateTime.now())
                    .build();
            commentService.addComment(comment);
        }catch (Exception e){
            logger.error("CommentController -> addComment: Error create comment from UserId: " + user.getId()+ " for postId: " + postId, e);
        }

        try {
            emailService.sendMessageAndComment(post.getUser().getEmail(), "You have a comment for your post");
        }catch (Exception e){
            logger.error("CommentController -> addComment: Error to send comment from email userId: " + user.getId()+ " for postId: " + postId, e);
        }

        return "redirect:/comment/" + postId;
    }
}

