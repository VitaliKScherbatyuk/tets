package scherbatyuk.network;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scherbatyuk.network.service.VisitCountService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class NetworkOnlineApplicationTests {

	@Autowired
	private VisitCountService visitCountService;


//	@Test
//	void contextLoads() {
//		runRepostServiceTests();
//		runSearchServiceTests();
//		runSupportServiceTest();
//		runUserServiceTest();
//		System.err.println("Запрацював contextLoads");
//		runVisitCountServiceTest();
//	}

//	@Test
//	void runRepostServiceTests() {
//		repostServiceTest.testRepostNewsPost();
//		repostServiceTest.testGetRepostsByUser();
//		repostServiceTest.testHasUserAlreadyReposted();
//		repostServiceTest.testDeleteRepostById();
//	}

//	@Test
//	void runSearchServiceTests() {
//		searchServiceTest.testSearchUsers();
//		searchServiceTest.testSearchPost();
//	}

//	@Test
//	void runSupportServiceTest() {
//		supportServiceTest.testSaveLetter();
//		supportServiceTest.testSaveAnswerToLetter();
//		supportServiceTest.testCountSupportLetters();
//	}

//	@Test
//	void runUserServiceTest() {
//		userServiceTest.testSaveUser();
//		userServiceTest.testFindByEmail();
//		userServiceTest.testGetAllUsers();
//		userServiceTest.testDeleteUser();
//		userServiceTest.testUpdateUserProfile();
//	}

//	@Test
//	void runVisitCountServiceTest() {
//		System.err.println("Запрацював testGetVisitCount");
//		visitCountServiceTest.testGetVisitCount();
//	}
}
