package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scherbatyuk.network.domain.Support;
import scherbatyuk.network.domain.User;

public interface SupportRepository extends JpaRepository<Support, Integer> {


    int countByAnswerLetter(boolean answerLetter);
}
