/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import scherbatyuk.network.domain.PhotoAlbum;

import java.util.List;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Integer>, CrudRepository <PhotoAlbum, Integer> {

    List<PhotoAlbum> findByUserId(Integer userId);

}
