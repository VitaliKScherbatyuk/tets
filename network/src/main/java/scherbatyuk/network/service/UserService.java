/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 24.12.2023
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
import java.util.Optional;

/**
 * service class that provides methods for interacting with the User entity in the database.
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

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.User);
        user.setCreateData(LocalDate.now());

        // Завантаження дефолтного зображення з URL
        String defaultImageUrl = "https://media.istockphoto.com/id/1288129966/uk/%D0%B2%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%BD%D1%96-%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%BD%D1%8F/%D0%B2%D1%96%D0%B4%D1%81%D1%83%D1%82%D0%BD%D1%96%D0%B9-%D1%82%D1%80%D0%B8%D0%BC%D0%B0%D1%87-%D0%BF%D1%96%D0%BA%D1%82%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%B8-%D0%BA%D0%B0%D0%BC%D0%B5%D1%80%D0%B8-%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%BD%D1%8F.jpg?s=612x612&w=0&k=20&c=-NzuLKw2KSkcPZlWWwaxJ6idMKyHGwsSeb_U3iDZTCo=";
        String imageData = downloadImage(defaultImageUrl);

        user.setImageData(imageData);
        userRepository.save(user);
    }

    private String downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);

            // Перетворення BufferedImage в рядок Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageDataBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageDataBytes);
        } catch (IOException e) {
            // Обробка помилок завантаження зображення
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Accepts the user's email and returns the User object found by the specified email.
     *
     * @param email
     * @return
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }

    /**
     * Returns a list of all users that are in the database.
     *
     * @return
     */
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

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

    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public void uploadImage(MultipartFile image, String userEmail) {

        User user = findByEmail(userEmail);
        if (user != null) {
            String encodedImage = photoService.encodeImage(image);
            user.setImageData(encodedImage);

            // Оновіть час активності користувача
            user.setLastActivityOnUpdate();

            userRepository.save(user);
        }
    }

    public List<String> findUsernamesByTerm(String term) {
        return entityManager.createQuery(
                        "SELECT u.name FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :term, '%'))", String.class)
                .setParameter("term", term)
                .getResultList();
    }

    @Transactional
    public void deleteUser(User user) {

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

}
