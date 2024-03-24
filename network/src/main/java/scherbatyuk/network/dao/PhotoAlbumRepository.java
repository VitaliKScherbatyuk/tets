package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import scherbatyuk.network.domain.PhotoAlbum;

import java.util.List;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Integer>, CrudRepository <PhotoAlbum, Integer> {

}
