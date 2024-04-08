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
import org.springframework.web.multipart.MultipartFile;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostNewsService postNewsService;

    @GetMapping("/addPost")
    private String addPost(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        List<PostNews> posts = postNewsService.getPostsByUser(user); // отримати пости авторизованого користувача
        posts.sort(Comparator.comparing(PostNews::getAddPostNews).reversed()); // сортувати по даті

        model.addAttribute("posts", posts); // додати пости в модель
        return "addPost";
    }


    @PostMapping("/savePost")
    private String addPost(@RequestParam("image") MultipartFile[] images,
                           @RequestParam("postNews") String postNews,
                           @RequestParam("hashTag") String hashTag){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        for (MultipartFile image : images) {
           postNewsService.createPost(image, postNews, hashTag, user);
        }

        return "redirect:/home";
    }

    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable("id") Integer postId) {
        postNewsService.deletePost(postId);
        return "redirect:/addPost";
    }

    @GetMapping("/rating")
    public String ratingPost() {
        return "rating";
    }

    @PostMapping("/likeable")
    public String countLike(@RequestParam Integer postId, Model model){
        PostNews post = postNewsService.findById(postId);
        int likePost = post.getLikeInPost();
        model.addAttribute("likeInPost", likePost);
        return "home";
    }

    @PostMapping("/updateLikes")
    @Transactional
    public ResponseEntity<Void> updateLikes(@RequestBody Map<String, Integer> payload) {
        Integer postId = payload.get("postId");
        Integer likes = payload.get("likes");
        PostNews post = postNewsService.findById(postId);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        post.setLikeInPost(likes);
        postNewsService.save(post);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
