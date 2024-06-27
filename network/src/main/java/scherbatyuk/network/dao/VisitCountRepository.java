/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.VisitCount;

import java.util.Optional;

public interface VisitCountRepository extends JpaRepository<VisitCount, Integer> {

    Optional<VisitCount> findByPageName(String pageName);

}
