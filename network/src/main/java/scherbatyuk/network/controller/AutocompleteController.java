/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scherbatyuk.network.service.PostNewsService;
import scherbatyuk.network.service.UserService;

import java.util.List;

/**
 * Handles autocomplete requests (#autocomplete) based on user input.
 */
@RestController
@RequestMapping("/autocomplete")
public class AutocompleteController {

    @Autowired
    private PostNewsService postNewsService;

    @Autowired
    private UserService userService;

    /**
     * Handles GET requests to the path "/autocomplete".
     * Returns a list of strings matching the entered term.
     * @param term search term (can be hashtag or username)
     * @return a list of lines that are autocompleted
     */
    @GetMapping
    public List<String> autocomplete(@RequestParam("term") String term) {

        if (term.startsWith("#")) {
            return postNewsService.findHashtagsByTerm(term);
        } else {
            return userService.findUsernamesByTerm(term);
        }
    }
}

