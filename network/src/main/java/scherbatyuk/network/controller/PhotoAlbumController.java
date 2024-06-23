/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

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
import scherbatyuk.network.service.*;

import java.util.List;

/**
 *
 */
@Controller
public class PhotoAlbumController {

    @Autowired
    private PhotoAlbumService photoAlbumService;
    @Autowired
    private UserService userService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private MessageService messageService;

    /**
     * Method for displaying photo settings.
     * @param model to add attributes for rendering the view.
     * @return The view name ("photoSetting") to render after processing.
     */
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

        List<User> friends = friendsService.getFriends(user.getId());

        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "photoSetting";
    }

    /**
     * Method for creating a new photo album.
     * @param albumName The name of the new album.
     * @return The view name ("photoSetting") to render after processing.
     */
    @PostMapping("/createAlbum")
    public String createAlbum(@RequestParam("albumName") String albumName) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail);

        photoAlbumService.createAlbum(albumName, currentUser);

        return "photoSetting";
    }

    /**
     * Method for deleting a photo album.
     * @param id The ID of the album to delete.
     * @param model to add attributes for rendering the view.
     * @return The redirect view name ("/photoSetting") after deleting the album.
     */
    @GetMapping("/delete/{id}")
    public String deleteAlbum(@PathVariable Integer id, Model model) {

        PhotoAlbum photoAlbum = photoAlbumService.findById(id);
        photoAlbumService.deleteById(photoAlbum.getId());

        return "redirect:/photoSetting";
    }

    /**
     * Method for viewing photos in an album.
     * @param id The ID of the album to view.
     * @param model to add attributes for rendering the view.
     * @return The view name ("albums") to render after processing.
     */
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

        List<User> friends = friendsService.getFriends(user.getId());

        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "albums";
    }

    /**
     * Method for uploading photos to an album.
     * @param images The array of Multipart files representing the images to upload.
     * @param description The description for the uploaded photos.
     * @param id The ID of the album to which the photos are uploaded.
     * @return The redirect view name ("/album/{id}") after uploading the photos.
     */
    @PostMapping("/uploadPhotos")
    public String uploadPhotos(@RequestParam("image") MultipartFile[] images,
                               @RequestParam("description") String description,
                               @RequestParam("id") Integer id) {

        for (MultipartFile image : images) {
            photoService.uploadPhoto(image, description, id);
        }
        return "redirect:/album/" + id;
    }

    /**
     * Method for deleting a photo.
     * @param photoId The ID of the photo to delete.
     * @return The redirect view name ("/albums") after deleting the photo.
     */
    @GetMapping("/deletePhoto/{id}")
    public String deletePhoto(@PathVariable("id") Integer photoId) {

        photoService.deletePhoto(photoId);

        return "redirect:/albums";
    }

    /**
     * Method for displaying photo galleries of a user.
     * @param id The ID of the user whose photo galleries are being viewed.
     * @param model to add attributes for rendering the view.
     * @return The view name ("photoGallery") to render after processing.
     */
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

        List<User> friends = friendsService.getFriends(user.getId());

        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "photoGallery";
    }

    /**
     * Method for displaying details of a specific photo gallery.
     * @param id The ID of the photo album whose details are being viewed.
     * @param model to add attributes for rendering the view.
     * @return The view name ("photoGalleryDetails") to render after processing.
     */
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

        List<User> friends = friendsService.getFriends(user.getId());

        int countRequests = friendsService.countIncomingFriendRequests(user.getId());
        model.addAttribute("countRequests", countRequests);
        int countMessages = messageService.countIncomingFriendMessage(user.getId());
        model.addAttribute("countMessages", countMessages);

        return "photoGalleryDetails";
    }

    /**
     * Method for displaying photo galleries of a friend.
     * @param id The ID of the friend whose photo galleries are being viewed.
     * @param model to add attributes for rendering the view.
     * @return The view name ("photoGallery") to render after processing.
     */
    @GetMapping("/photoGalleryFriend/{id}")
    public String photoGalleryFriend(@PathVariable Integer id, Model model){

        List<PhotoAlbum> albums = photoAlbumService.findByUserId(id);
        model.addAttribute("albums", albums);

        return "photoGallery";
    }
}
