package scherbatyuk.network;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import scherbatyuk.network.domain.Support;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.SupportService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SupportServiceTest {

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
}

