package scherbatyuk.network;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import scherbatyuk.network.domain.*;
import scherbatyuk.network.service.*;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NetworkOnlineApplicationTests {

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

	@Autowired
	private SupportService supportService;

	@Test
	public void testSaveLetter() {
		// Create a user and save a support letter
		User user = new User();
		supportService.saveLetter(user, "First Name", "Comment");

		List<Support> supportLetters = supportService.getAllLetters();
		assertNotNull(supportLetters);
		// Add assertions based on expected behavior
	}

	@Test
	public void testSaveAnswerToLetter() {
		// Create a support letter, save it, then save an answer
		Support letter = new Support();
		supportService.saveLetter(letter, "Answer");

		Support savedLetter = supportService.findById(letter.getId());
		assertNotNull(savedLetter);
		assertTrue(savedLetter.isAnswerLetter());
		assertEquals("Answer", savedLetter.getAnswer());
	}

	@Test
	public void testCountSupportLetters() {
		int count = supportService.countSupportLetters();
		// Add assertions based on expected behavior
	}


	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void testSaveUser() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		userService.save(user);

		User savedUser = userService.findByEmail("test@example.com");
		assertNotNull(savedUser);
		assertEquals("test@example.com", savedUser.getEmail());
		assertTrue(passwordEncoder.matches("password", savedUser.getPassword()));
		assertEquals(UserRole.User, savedUser.getRole());
	}

	@Test
	public void testFindByEmail() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		userService.save(user);

		User foundUser = userService.findByEmail("test@example.com");
		assertNotNull(foundUser);
		assertEquals("test@example.com", foundUser.getEmail());
	}

	@Test
	public void testGetAllUsers() {
		List<User> users = userService.getAllUser();
		assertNotNull(users);
		// Add assertions based on your expectations for the list of users
	}

	@Test
	public void testUpdateUserProfile() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		userService.save(user);

		user.setName("Updated Name"); // Встановіть нове ім'я
		userService.updateProfile(Collections.singletonList(user));

		assertEquals("Updated Name", user.getName());
	}

	@Autowired
	private VisitCountService visitCountService;

	@Test
	public void testGetVisitCount() {

		int count = 0;
		String pageName = "login";
		count = visitCountService.getVisitCount(pageName);
		assertEquals(0, count);

		visitCountService.incrementVisitCount(pageName);
		count = visitCountService.getVisitCount(pageName);
		assertEquals(1, count);

	}
}

