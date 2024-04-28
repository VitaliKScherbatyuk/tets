package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.Repost;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.RepostService;
import scherbatyuk.network.service.UserService;

import java.util.Map;


@RestController
public class RepostController {

    private final RepostService repostService;

    @Autowired
    public RepostController(RepostService repostService) {
        this.repostService = repostService;
    }

    @Autowired
    private UserService userService;
    @Autowired
    private PostNewsService postNewsService;

    @PostMapping("/createRepost")
    public ResponseEntity<?> repostPost(@RequestBody Map<String, Integer> postIdMap) {
        Integer postId = postIdMap.get("postId");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);

        PostNews post = postNewsService.findById(postId);

        if (user == null || post == null) {
            return new ResponseEntity<>("Користувач або пост не знайдено", HttpStatus.NOT_FOUND);
        }

        Repost repost = repostService.repostPost(user, post);

        // Повернути успішну відповідь з інформацією про створений репост
        return new ResponseEntity<>(repost, HttpStatus.CREATED);
    }

}