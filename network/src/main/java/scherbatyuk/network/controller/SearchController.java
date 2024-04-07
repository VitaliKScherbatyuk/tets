package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        List<User> users = searchService.searchUsers(searchTerm);
        model.addAttribute("users", users);
        return "searchResults";
    }
}
