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

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    public List<Photo> getAllPhotosByAlbumId(Integer albumId) {
        return photoRepository.findByAlbumId(albumId);
    }

    public void uploadPhoto(MultipartFile image, String description, Integer id) {
        Optional<PhotoAlbum> albumOptional = photoAlbumRepository.findById(id);
        if (albumOptional.isPresent()) {
            PhotoAlbum album = albumOptional.get();
            Photo photo = new Photo();
            photo.setAddPhotoToAlbum(LocalDate.now());
            photo.setDescription(description);
            photo.setAlbum(album);
            // Кодуємо зображення у формат base64
            String encodedImage = encodeImage(image);
            photo.setEncodedImage(encodedImage);

            photoRepository.save(photo);
        } else {
            // Обробити випадок, коли альбом не знайдено
        }
    }


    protected String encodeImage(MultipartFile image) {
        String base64Image = "";
        try {
            byte[] imageBytes = image.getBytes();
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error encoding image: " + e.getMessage());
            return null; // поверніть null або встановіть base64Image в якесь значення за замовчуванням
        }
        return base64Image;
    }

    public void deletePhoto(Integer photoId) {
        photoRepository.deleteById(photoId);
    }
}
