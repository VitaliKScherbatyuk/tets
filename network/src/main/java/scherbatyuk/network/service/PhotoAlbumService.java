/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.PhotoAlbumRepository;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.PhotoAlbum;
import scherbatyuk.network.domain.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for managing photo albums.
 */
@Service
public class PhotoAlbumService {

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all photo albums.
     * @return List of all photo albums
     */
    public List<PhotoAlbum> getAllAlbums() {
        return photoAlbumRepository.findAll();
    }

    /**
     * Creates a new photo album for a user.
     * @param albumName The name of the album
     * @param user The name of the album
     */
    public void createAlbum(String albumName, User user) {

        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.setNameAlbum(albumName);
        photoAlbum.setCreateAlbum(LocalDate.now());
        photoAlbum.setUser(user);

        photoAlbumRepository.save(photoAlbum);
    }

    /**
     * Finds a photo album by its ID.
     * @param id Finds a photo album by its ID.
     * @return The found photo album or null if not found
     */
    public PhotoAlbum findById(int id){
        return photoAlbumRepository.findById(id).orElse(null);
    }

    /**
     * Deletes a photo album by its ID.
     * @param id The ID of the photo album to delete
     */
    public void deleteById(Integer id){
        photoAlbumRepository.deleteById(id);
    }

    /**
     * Finds all photo albums belonging to a specific user.
     * @param userId The ID of the user
     * @return List of photo albums belonging to the user
     */
    public List<PhotoAlbum> findByUserId(Integer userId) {
        return photoAlbumRepository.findByUserId(userId);
    }
}
