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
import scherbatyuk.network.domain.PostLikes;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {
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
     * Відображення сторінки addPost враховуючи сортування за датою створення
     * виводяться лише пости друзів
     * @param model
     * @return
     */
    @GetMapping("/addPost")
    private String addPost(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        List<PostNews> posts = postNewsService.getPostsByUser(user); // отримати пости авторизованого користувача
        posts.sort(Comparator.comparing(PostNews::getAddPostNews).reversed()); // сортувати по даті

        model.addAttribute("posts", posts); // додати пости в модель

        model.addAttribute("user", user);
        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        List<User> friends = friendsService.getFriends(user.getId());
        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "addPost";
    }

    /**
     * зберігання нового посту
     * @param images
     * @param postNews
     * @param hashTag
     * @return
     */
    @PostMapping("/savePost")
    private String addPost(@RequestParam("image") MultipartFile[] images,
                           @RequestParam("postNews") String postNews,
                           @RequestParam("hashTag") String hashTag){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        for (MultipartFile image : images) {
            PostNews post = postNewsService.createPost(image, postNews, hashTag, user);

            // Створення нового об'єкта PostLikes для кожного допису
            PostLikes newLike = PostLikes.builder()
                    .user(user)
                    .post(post)
                    .likePost(0) // Початкове значення лайків встановлено як 0
                    .build();

            // Збереження нового об'єкта PostLikes
            postLikesService.save(newLike);
        }

        return "redirect:/home";
    }

    /**
     * видалення посту за його id та лайки до цього посту
     * @param postId
     * @return
     */
    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable("id") Integer postId) {
        postNewsService.deletePost(postId);
        return "redirect:/addPost";
    }

}
