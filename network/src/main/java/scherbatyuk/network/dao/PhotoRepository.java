package scherbatyuk.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import scherbatyuk.network.domain.Photo;

import java.util.List;

public interface PhotoRepository extends JpaRepository <Photo, Integer> {
    List<Photo> findByAlbumId(Integer albumId);
}
