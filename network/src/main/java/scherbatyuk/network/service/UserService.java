/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scherbatyuk.network.dao.*;
import scherbatyuk.network.domain.*;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

/**
 * Service class that provides methods for interacting with the User entity in the database.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PostNewsService postNewsService;
    @Autowired
    private PhotoAlbumService photoAlbumService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RepostService repostService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FriendsRepository friendsRepository;
    @Autowired
    private PostLikesService postLikesService;

    /**
     * Saves a user into the database.
     * @param user The user to save
     */
    public void save(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(user.getEmail().equals("vitaliktuhata@gmail.com")){
            user.setRole(UserRole.Admin);
        }else {
            user.setRole(UserRole.User);
        }
        user.setCreateData(LocalDate.now());

        String defaultImageUrl = "https://media.istockphoto.com/id/1288129966/uk/%D0%B2%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%BD%D1%96-%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%BD%D1%8F/%D0%B2%D1%96%D0%B4%D1%81%D1%83%D1%82%D0%BD%D1%96%D0%B9-%D1%82%D1%80%D0%B8%D0%BC%D0%B0%D1%87-%D0%BF%D1%96%D0%BA%D1%82%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%B8-%D0%BA%D0%B0%D0%BC%D0%B5%D1%80%D0%B8-%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%BD%D1%8F.jpg?s=612x612&w=0&k=20&c=-NzuLKw2KSkcPZlWWwaxJ6idMKyHGwsSeb_U3iDZTCo=";
        String imageData = downloadImage(defaultImageUrl);

        user.setImageData(imageData);
        userRepository.save(user);
    }

    /**
     * Downloads an image from a URL and converts it to a Base64-encoded string.
     * @param imageUrl The URL of the image to download
     * @return Base64-encoded string representing the image data
     */
    private String downloadImage(String imageUrl) {

        try {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageDataBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageDataBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Finds a user by their email.
     * @param email The email of the user to find
     * @return The User object if found, otherwise null
     */
    public User findByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }


    /**
     * Retrieves all users from the database.
     * @return List of all users
     */
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    /**
     * Updates profiles for a list of users.
     * @param list The list of users to update
     */
    public void updateProfile(List<User> list) {

        for (User userUpdate : list) {

            try {
                userRepository.save(userUpdate);
                System.out.println("User updated successfully: " + userUpdate.getId());
            } catch (Exception e) {
                System.err.println("Error updating user: " + userUpdate.getId());
            }
        }
    }

    /**
     * Finds a user by their ID.
     * @param id The ID of the user to find
     * @return The User object if found, otherwise null
     */
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Retrieves a user by their ID.
     * @param userId The ID of the user to retrieve
     * @return The User object
     * @throws EntityNotFoundException If no user exists with the given ID
     */
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    /**
     * Uploads an image for a user and updates their last activity.
     * @param image The image to upload
     * @param userEmail The email of the user
     */
    public void uploadImage(MultipartFile image, String userEmail) {

        User user = findByEmail(userEmail);
        if (user != null) {
            String encodedImage = photoService.encodeImage(image);
            user.setImageData(encodedImage);

            user.setLastActivityOnUpdate();

            userRepository.save(user);
        }
    }

    /**
     * Finds usernames matching a search term.
     * @param term The search term to match usernames
     * @return List of usernames matching the search term
     */
    public List<String> findUsernamesByTerm(String term) {
        return entityManager.createQuery(
                        "SELECT u.name FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :term, '%'))", String.class)
                .setParameter("term", term)
                .getResultList();
    }

    /**
     * Deletes a user from the database, including all associated data such as posts, comments, likes,
     * messages, albums, and friends.
     * @param user The user to delete
     */
    @Transactional
    public void deleteUser(User user) {

        friendsRepository.saveAll(user.getFriendsList());

        List<PostNews> posts = postNewsService.getPostsByUser(user);
        if (!posts.isEmpty()) {
            posts.forEach(post -> {

                List<Comment> comments = commentService.getCommentByPost(post.getId());
                if (!comments.isEmpty()) {
                    comments.forEach(comment -> commentService.deleteById(comment.getId()));
                }

                List<PostLikes> postLikesList = postLikesService.getLikesByPost(post.getId());
                if (!postLikesList.isEmpty()) {
                    postLikesList.forEach(postLikes -> postLikesService.deleteById(postLikes.getId()));
                }

                postNewsService.deletePost(post.getId());
            });
        }

        List<Repost> reposts = repostService.getRepostsByUser(user);
        if (!reposts.isEmpty()) {
            reposts.forEach(repost -> repostService.deleteById(repost.getId()));
        }

        List<PhotoAlbum> photoAlbums = photoAlbumService.findByUserId(user.getId());
        if (!photoAlbums.isEmpty()) {
            photoAlbums.forEach(photoAlbum -> {
                List<Photo> photos = photoService.getAllPhotosByAlbumId(photoAlbum.getId());

                if (!photos.isEmpty()) {
                    photos.forEach(photo -> photoService.deletePhoto(photo.getId()));
                }

                photoAlbumService.deleteById(photoAlbum.getId());
            });
        }

        List<Message> messages = messageService.getMessagesForUser(user.getId());
        if (!messages.isEmpty()) {
            messages.forEach(message -> {

                if (!message.getReplies().isEmpty()) {
                    message.getReplies().forEach(reply -> messageService.deleteById(reply.getId()));
                }

                messageService.deleteById(message.getId());
            });
        }

        List<User> friends = friendsService.getFriends(user.getId());
        if (!friends.isEmpty()) {
            friends.forEach(friend -> {
                friendsService.deleteFriend(friend, user);
            });
        }

        List<Message> messagesToUser = messageService.getMessagesToUser(user.getId());
        if (!messagesToUser.isEmpty()) {
            messagesToUser.forEach(message -> {

                if (!message.getReplies().isEmpty()) {
                    message.getReplies().forEach(reply -> messageService.deleteReplyById(reply.getId()));
                }

                messageService.deleteById(message.getId());
            });
        }

        userRepository.delete(user);
    }

    /**
     * Retrieves the total count of users in the database.
     * @return Total count of users
     */
    public int getCountAllUser() {
        return userRepository.countAllUsers();
    }
}
