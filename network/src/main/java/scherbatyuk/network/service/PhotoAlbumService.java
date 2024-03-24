package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.PhotoAlbumRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.Message;
import scherbatyuk.network.domain.PhotoAlbum;
import scherbatyuk.network.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoAlbumService {

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;
    @Autowired
    private UserRepository userRepository;

    public List<PhotoAlbum> getAllAlbums() {
        return photoAlbumRepository.findAll();
    }

    public void createAlbum(String albumName, User user) {

        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.setNameAlbum(albumName);
        photoAlbum.setCreateAlbum(LocalDate.now());
        photoAlbum.setUser(user);
        photoAlbumRepository.save(photoAlbum);
    }

    public PhotoAlbum findById(int id){
        return photoAlbumRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id){
        photoAlbumRepository.deleteById(id);
    }
}
