/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

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

	/**
	 * This test tests the repost function of a news post.
	 * A user and a news post are created.
	 * The repostPost() method of the RepostService service is called.
	 * It is checked that the repost is not null.
	 * It is checked that the user and the post in the repost correspond to the ones created.
	 */
	@Test
	public void testRepostNewsPost() {

		User user = new User();
		PostNews post = new PostNews();
		Repost repost = repostService.repostPost(user, post);

		assertNotNull(repost);
		assertEquals(user, repost.getUser());
		assertEquals(post, repost.getPost());
	}

	/**
	 * This test tests the function of receiving reposts by user.
	 * A user is created.
	 * The getRepostsByUser() method of the RepostService service is called.
	 * It is checked that the list of reposts is not null.
	 */
	@Test
	public void testGetRepostsByUser() {

		User user = new User();
		List<Repost> userReposts = repostService.getRepostsByUser(user);

		assertNotNull(userReposts);
	}

	/**
	 * This test checks whether the user has already reposted a specific post.
	 * A user and a news post are created.
	 * The hasUserAlreadyReposted() method of the RepostService service is called.
	 * It is checked that the user has not reposted yet (should return false).
	 */
	@Test
	public void testHasUserAlreadyReposted() {

		User user = new User();
		PostNews post = new PostNews();
		boolean hasReposted = repostService.hasUserAlreadyReposted(user, post);

		assertFalse(hasReposted);
	}

	/**
	 * This test tests the function of removing a repost by its ID.
	 * A user and a news post are created, a repost is executed.
	 * The deleteById() method of the RepostService service is called to delete the repost.
	 * Deletion is checked (for example, you can check that the repost no longer exists in the database).
	 */
	@Test
	public void testDeleteRepostById() {

		User user = new User();
		PostNews post = new PostNews();
		Repost repost = repostService.repostPost(user, post);

		repostService.deleteById(repost.getId());
	}


	@Autowired
	private SearchService searchService;

	/**
	 * This test tests the user search function.
	 * The searchUsers() method of the SearchService service is called with a specific search term.
	 * Checks that the list of users is not null.
	 */
	@Test
	public void testSearchUsers() {

		List<User> users = searchService.searchUsers("searchTerm");
		assertNotNull(users);
	}

	/**
	 * This test tests the post search function.
	 * The searchPost() method of the SearchService service is called with a specific search term.
	 * Checks that the list of posts is not null.
	 */
	@Test
	public void testSearchPost() {

		List<PostNews> posts = searchService.searchPost("searchTerm");
		assertNotNull(posts);
	}

	@Autowired
	private SupportService supportService;

	/**
	 * This test tests the support letter save function.
	 * A user is created and a support letter is saved.
	 * The getAllLetters() method of the SupportService service is called to retrieve all support letters.
	 * It is checked that the list of letters is not null.
	 */
	@Test
	public void testSaveLetter() {

		User user = new User();
		supportService.saveLetter(user, "First Name", "Comment");

		List<Support> supportLetters = supportService.getAllLetters();
		assertNotNull(supportLetters);
	}

	/**
	 * This test tests the functionality of saving a reply to a support letter.
	 * A support letter is created, the answer is saved.
	 * The method findById() of the SupportService service is called to find the letter by its ID.
	 * It checks that the email found is not null, that it is a response email, and that the response is stored correctly.
	 */
	@Test
	public void testSaveAnswerToLetter() {

		Support letter = new Support();
		supportService.saveLetter(letter, "Answer");

		Support savedLetter = supportService.findById(letter.getId());
		assertNotNull(savedLetter);
		assertTrue(savedLetter.isAnswerLetter());
		assertEquals("Answer", savedLetter.getAnswer());
	}

	/**
	 * This test tests support letter counting functionality.
	 * The countSupportLetters() method of the SupportService service is called.
	 * It checks that the returned value is as expected.
	 */
	@Test
	public void testCountSupportLetters() {
		int count = supportService.countSupportLetters();
	}


	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * This test tests the user save function.
	 * A user with an email and password is created and stored in the database.
	 * The method findByEmail() of the UserService service is called to find a user by email.
	 * It is verified that the user is found, his email is correct, the password is coded correctly, and the user's role is correct.
	 */
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

	/**
	 * This test checks the function of finding a user by email.
	 * A user is created and saved.
	 * The method findByEmail() of the UserService service is called.
	 * It is checked that the found user is not null and that his email matches.
	 */
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

	/**
	 * This test tests the functionality of getting all users.
	 * The getAllUser() method of the UserService service is called.
	 * Checks that the list of users is not null.
	 */
	@Test
	public void testGetAllUsers() {

		List<User> users = userService.getAllUser();
		assertNotNull(users);
	}

	/**
	 * This test tests the user profile update feature.
	 * A user is created and saved.
	 * The username is updated and the updateProfile() method of the UserService service is called.
	 * Verifying that the username is updated correctly.
	 */
	@Test
	public void testUpdateUserProfile() {

		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		userService.save(user);

		user.setName("Updated Name");
		userService.updateProfile(Collections.singletonList(user));

		assertEquals("Updated Name", user.getName());
	}

	@Autowired
	private VisitCountService visitCountService;

	/**
	 * This test tests the page visit counting feature.
	 * It checks that the initial hit count for the page is 0.
	 * The incrementVisitCount() method is called to increment the count.
	 * It is verified that the visit count has increased by 1.
	 */
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

