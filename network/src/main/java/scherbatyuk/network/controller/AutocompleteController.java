package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/autocomplete")
public class AutocompleteController {

    @Autowired
    private PostNewsService postNewsService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<String> autocomplete(@RequestParam("term") String term) {
        System.err.println(term);  // Для відладки
        if (term.startsWith("#")) {
            return postNewsService.findHashtagsByTerm(term);
        } else {
            return userService.findUsernamesByTerm(term);
        }
    }
}

