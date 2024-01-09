/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 24.12.2023
 */

package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.GeoLocationResponse;


public interface GeoLocationResponseRepository extends JpaRepository<GeoLocationResponse, Integer> {
}
