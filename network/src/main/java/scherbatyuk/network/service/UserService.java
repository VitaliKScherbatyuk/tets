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
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.domain.UserRole;

import javax.transaction.Transactional;
import java.time.LocalDate;
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
    public void save(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.User);
        user.setCreateData(LocalDate.now());
        userRepository.save(user);
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



}
