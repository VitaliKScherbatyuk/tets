package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/comment/{id}")
    public String commentPage(@PathVariable Integer id, Model model) {
        PostNews post = postNewsService.findById(id);
        List<Comment> comments = commentService.getCommentByPost(post.getId());
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "comment";
    }

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
        return "redirect:/comment/" + postId; // Redirect back to the comment page after adding the comment
    }
}

