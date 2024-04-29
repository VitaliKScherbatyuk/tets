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
import scherbatyuk.network.service.PostLikesService;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.UserService;

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
     * видалення посту за його id
     * @param postId
     * @return
     */
    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable("id") Integer postId) {
        postNewsService.deletePost(postId);
        return "redirect:/addPost";
    }


}
