/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.Support;

import java.util.List;

public interface SupportRepository extends JpaRepository<Support, Integer> {

    int countByAnswerLetter(boolean answerLetter);

    List<Support> findAll();

    List<Support> findByEmail(String email);
}
