package scherbatyuk.network;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import scherbatyuk.network.domain.PostNews;
import scherbatyuk.network.domain.Repost;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.RepostService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RepostServiceTest {

    @Autowired
    private RepostService repostService;

    @Test
    public void testRepostNewsPost() {
        // Create a user and a news post
        User user = new User();
        PostNews post = new PostNews();
        Repost repost = repostService.repostPost(user, post);

        assertNotNull(repost);
        assertEquals(user, repost.getUser());
        assertEquals(post, repost.getPost());
    }

    @Test
    public void testGetRepostsByUser() {
        // Create a user and verify repost retrieval
        User user = new User();
        List<Repost> userReposts = repostService.getRepostsByUser(user);

        assertNotNull(userReposts);
        // Add assertions based on expected behavior
    }

    @Test
    public void testHasUserAlreadyReposted() {
        // Create a user and a news post
        User user = new User();
        PostNews post = new PostNews();
        boolean hasReposted = repostService.hasUserAlreadyReposted(user, post);

        assertFalse(hasReposted);
        // Simulate user reposting and retest
    }

    @Test
    public void testDeleteRepostById() {
        // Create a user and a news post, repost it and then delete by ID
        User user = new User();
        PostNews post = new PostNews();
        Repost repost = repostService.repostPost(user, post);

        repostService.deleteById(repost.getId());

        // Verify deletion
    }
}
