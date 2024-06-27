package scherbatyuk.network;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.SearchService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void testSearchUsers() {
        List<User> users = searchService.searchUsers("searchTerm");

        assertNotNull(users);
        // Add assertions based on expected behavior
    }

    @Test
    public void testSearchPost() {
        List<PostNews> posts = searchService.searchPost("searchTerm");

        assertNotNull(posts);
        // Add assertions based on expected behavior
    }
}

