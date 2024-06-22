package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.SupportRepository;
import scherbatyuk.network.domain.Support;
import scherbatyuk.network.domain.User;

import java.time.LocalDate;

@Service
public class SupportService {

    @Autowired
    private SupportRepository supportRepository;

    public void saveLetter(User user, String firstName, String comment) {
        Support support = Support.builder()
                .user(user)
                .firstName(firstName)
                .comment(comment)
                .commentCreate(LocalDate.now())
                .answerLetter(false)
                .build();

        supportRepository.save(support);
    }

    public int countSupportLetters(){
        return supportRepository.countByAnswerLetter(false);
    }

}
