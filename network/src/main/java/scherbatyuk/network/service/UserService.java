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
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.domain.UserRole;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

/**
 * service class that provides methods for interacting with the User entity in the database.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Takes a User object and stores it in the database. The user's password is hashed
     * using PasswordEncoder before saving. Also sets the user role to UserRole.User.
     *
     * @param user
     */
//    public void save(User user) {
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setRole(UserRole.User);
//        user.setCreateData(LocalDate.now());
//        userRepository.save(user);
//    }

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
                e.printStackTrace();
                System.err.println("Error updating user: " + userUpdate.getId());
            }
        }
    }

}
