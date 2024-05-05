package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scherbatyuk.network.domain.Comment;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.CommentService;
import scherbatyuk.network.service.FriendsService;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private PostNewsService postNewsService;

    /**
     * this method retrieves the post and all its comments from the database
     * and passes them to the “comment” view for display
     * @param id
     * @param model
     * @return
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

        return "comment";
    }

    /**
     * method is used to add a new comment to a post
     * @param postId
     * @param commentText
     * @return
     */
    @PostMapping("/addComment")
    public String addComment(@RequestParam("postId") Integer postId,
                             @RequestParam("commentText") String commentText) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        PostNews post = postNewsService.findById(postId);
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .commentText(commentText)
                .addCommentTime(LocalDateTime.now())
                .build();
        commentService.addComment(comment);
        return "redirect:/comment/" + postId;
    }
}

