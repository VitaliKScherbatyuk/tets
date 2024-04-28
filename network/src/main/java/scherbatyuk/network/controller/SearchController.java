package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.SearchService;
import scherbatyuk.network.service.UserService;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/search")
    public String search(@RequestParam String searchTerm, Model model) {

        if (searchTerm.startsWith("#")) {
            searchTerm = searchTerm.substring(1);
            List<PostNews> posts = searchService.searchPost(searchTerm);
            model.addAttribute("posts", posts);
        } else {
            List<User> users = searchService.searchUsers(searchTerm);
            model.addAttribute("users", users);
        }

        return "searchResults";
    }
}
