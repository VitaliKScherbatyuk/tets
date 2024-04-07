package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.UserService;

import java.time.LocalDate;

@Controller
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostNewsService postNewsService;

    @GetMapping("/addPost")
    private String addPost(){
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
}
