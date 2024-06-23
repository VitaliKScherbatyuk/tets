/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scherbatyuk.network.dao.PhotoAlbumRepository;
import scherbatyuk.network.dao.PhotoRepository;
import scherbatyuk.network.domain.Photo;
import scherbatyuk.network.domain.PhotoAlbum;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing photos.
 */
@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    /**
     * Retrieves all photos belonging to a specific album.
     * @param albumId The ID of the album
     * @return List of photos in the album
     */
    public List<Photo> getAllPhotosByAlbumId(Integer albumId) {
        return photoRepository.findByAlbumId(albumId);
    }

    /**
     * Uploads a photo to a specified album.
     * @param image The image file to upload
     * @param description Description of the photo
     * @param id The ID of the album to upload the photo to
     */
    public void uploadPhoto(MultipartFile image, String description, Integer id) {

        Optional<PhotoAlbum> albumOptional = photoAlbumRepository.findById(id);

        if (albumOptional.isPresent()) {
            PhotoAlbum album = albumOptional.get();
            Photo photo = new Photo();
            photo.setAddPhotoToAlbum(LocalDate.now());
            photo.setDescription(description);
            photo.setAlbum(album);

            String encodedImage = encodeImage(image);
            photo.setEncodedImage(encodedImage);

            photoRepository.save(photo);
        } else {
            throw new IllegalArgumentException("Album not found with id: " + id);
        }
    }

    /**
     * Encodes a multipart file (image) to base64 string.
     * @param image The image file to encode
     * @return Base64-encoded string representation of the image
     */
    protected String encodeImage(MultipartFile image) {

        String base64Image = "";

        try {
            byte[] imageBytes = image.getBytes();
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error encoding image: " + e.getMessage());
            return null;
        }
        return base64Image;
    }

    /**
     * Deletes a photo by its ID.
     * @param photoId The ID of the photo to delete
     */
    public void deletePhoto(Integer photoId) {
        photoRepository.deleteById(photoId);
    }
}
