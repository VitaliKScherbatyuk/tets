package scherbatyuk.network;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import scherbatyuk.network.domain.User;
import scherbatyuk.network.domain.UserRole;
import scherbatyuk.network.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        userService.save(user);

        User savedUser = userService.findByEmail("test@example.com");
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(passwordEncoder.matches("password", savedUser.getPassword()));
        assertEquals(UserRole.User, savedUser.getRole());
    }

    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        userService.save(user);

        User foundUser = userService.findByEmail("test@example.com");
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        userService.save(user);

        userService.deleteUser(user);

        User deletedUser = userService.findByEmail("test@example.com");
        assertNull(deletedUser);
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUser();
        assertNotNull(users);
        // Add assertions based on your expectations for the list of users
    }

    @Test
    public void testUpdateUserProfile() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        userService.save(user);

        user.setName("Updated Name");
        userService.updateProfile(Collections.singletonList(user));

        User updatedUser = userService.findByEmail("test@example.com");
        assertEquals("Updated Name", updatedUser.getName());
    }
}

