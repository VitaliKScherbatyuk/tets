package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import scherbatyuk.network.domain.Photo;
import scherbatyuk.network.domain.User;

import java.util.List;

public interface PhotoRepository extends JpaRepository <Photo, Integer> {
    List<Photo> findByAlbumId(Integer albumId);

}
