/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.SupportRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.Support;
import scherbatyuk.network.domain.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for managing support letters.
 */
@Service
public class SupportService {

    @Autowired
    private SupportRepository supportRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Saves a support letter from a user.
     * @param user The user submitting the support letter
     * @param firstName First name of the user submitting the letter
     * @param comment Content of the support letter
     */
    public void saveLetter(User user, String firstName, String comment) {

        userRepository.save(user);

        Support support = Support.builder()
                .user(user)
                .firstName(firstName)
                .comment(comment)
                .email(user.getEmail())
                .commentCreate(LocalDate.now())
                .answerLetter(false)
                .build();

        supportRepository.save(support);
    }

    /**
     * Counts the number of unanswered support letters.
     * @return Number of unanswered support letters
     */
    public int countSupportLetters(){
        return supportRepository.countByAnswerLetter(false);
    }

    /**
     * Counts the number of unanswered support letters.
     * @return Number of unanswered support letters
     */
    public List<Support> getAllLetters() {
        return supportRepository.findAll();
    }

    /**
     * Finds a support letter by its ID.
     * @param id ID of the support letter to find
     * @return Support letter if found, otherwise null
     */
    public Support findById(Integer id) {
        return supportRepository.findById(id).orElse(null);
    }

    /**
     * Saves an answer to a support letter.
     * @param letter Support letter to answer
     * @param answer Answer to the support letter
     */
    public void saveLetter(Support letter, String answer) {

        letter.setAnswerLetter(true);
        letter.setAnswer(answer);
        letter.setAnswerCreate(LocalDate.now());
        supportRepository.save(letter);
    }
}
