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
import scherbatyuk.network.service.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class LikesController {

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

    @PostMapping("/addLikeToPost")
    @Transactional
    public ResponseEntity<Void> updateLikes(@RequestBody Map<String, Integer> payload) {
        Integer postId = payload.get("postId");
        Integer likes = payload.get("likes");
        PostNews post = postNewsService.findById(postId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        post.setLikeInPost(likes);
        postNewsService.save(post);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * відображення кількості лайків під постом
     *
     * @param postId
     * @param model
     * @return
     */
    @PostMapping("/likeable")
    public String countLike(@RequestParam Integer postId, Model model) {
        PostNews post = postNewsService.findById(postId);
        int likePost = post.getLikeInPost();
        model.addAttribute("likeInPost", likePost);


        return "home";
    }

    /**
     * відображення сторінки з рейтингами rating (не реалізовано)
     *
     * @return
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

        List<PostNews> myPosts = postNewsService.getPostsByUser(user); // Отримати всі пости користувача
        Optional<PostNews> myTopPost = myPosts.stream()
                .sorted(Comparator.comparingInt(PostNews::getLikeInPost).reversed())
                .findFirst();

        myTopPost.ifPresent(post -> model.addAttribute("myTopPost", post)); // Передати пост користувача в шаблон

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

