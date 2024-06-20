package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import scherbatyuk.network.domain.PhotoAlbum;
import scherbatyuk.network.domain.User;

import java.util.List;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Integer>, CrudRepository <PhotoAlbum, Integer> {

    List<PhotoAlbum> findByUserId(Integer userId);

}
