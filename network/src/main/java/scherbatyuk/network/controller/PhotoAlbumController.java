package scherbatyuk.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scherbatyuk.network.domain.PhotoAlbum;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.service.PhotoAlbumService;
import scherbatyuk.network.service.PhotoService;
import scherbatyuk.network.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class PhotoAlbumController {

    @Autowired
    private PhotoAlbumService photoAlbumService;
    @Autowired
    private UserService userService;
    @Autowired
    private PhotoService photoService;


    @GetMapping("/photoSetting")
    public String photoSetting(Model model){
        List<PhotoAlbum> albums = photoAlbumService.getAllAlbums();
        model.addAttribute("albums", albums);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        model.addAttribute("user", user);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();
        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        return "photoSetting";
    }

    @PostMapping("/createAlbum")
    public String createAlbum(@RequestParam("albumName") String albumName) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        photoAlbumService.createAlbum(albumName, currentUser);
        return "photoSetting";
    }

    @GetMapping("/delete/{id}")
    public String deleteAlbum(@PathVariable Integer id, Model model) {
        PhotoAlbum photoAlbum = photoAlbumService.findById(id);
        photoAlbumService.deleteById(photoAlbum.getId());
        return "redirect:/photoSetting";
    }

    @GetMapping("/album/{id}")
    public String viewAlbums(@PathVariable Integer id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("photos", photoService.getAllPhotosByAlbumId(id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        model.addAttribute("user", user);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);
        return "albums";
    }


    @PostMapping("/uploadPhotos")
    public String uploadPhotos(@RequestParam("image") MultipartFile[] images,
                               @RequestParam("description") String description,
                               @RequestParam("id") Integer id) {

        for (MultipartFile image : images) {
            photoService.uploadPhoto(image, description, id);
        }
        return "redirect:/album/" + id;
    }

    @GetMapping("/deletePhoto/{id}")
    public String deletePhoto(@PathVariable("id") Integer photoId) {
        photoService.deletePhoto(photoId);
        return "redirect:/albums";
    }

    @GetMapping("/photoGallery/{id}")
    public String photoGallery(@PathVariable Integer id, Model model){
        List<PhotoAlbum> albums = photoAlbumService.findByUserId(id);
        model.addAttribute("albums", albums);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        model.addAttribute("user", user);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);
        return "photoGallery";
    }

    @GetMapping("/photoGalleryDetails/{id}")
    public String photoGalleryDetails(@PathVariable Integer id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("photos", photoService.getAllPhotosByAlbumId(id)); // Assuming you have a method to retrieve photos by album id

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        model.addAttribute("user", user);

        int age = user.getAge();
        String country = user.getCountry();
        String hobby = user.getHobby();
        String imageData = user.getImageData();

        model.addAttribute("age", age);
        model.addAttribute("country", country);
        model.addAttribute("hobby", hobby);
        model.addAttribute("imageData", imageData);

        return "photoGalleryDetails";
    }

    @GetMapping("/photoGalleryFriend/{id}")
    public String photoGalleryFriend(@PathVariable Integer id, Model model){
        List<PhotoAlbum> albums = photoAlbumService.findByUserId(id);
        model.addAttribute("albums", albums);

        return "photoGallery";
    }
}
