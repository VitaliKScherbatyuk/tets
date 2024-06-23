/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.UserRepository;
import scherbatyuk.network.domain.User;

import java.util.Collections;
import java.util.Optional;

/**
 * implementation of the UserDetailsService interface in Spring Security and is
 * responsible for loading information about the user during authentication
 */
@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Designed to load information about a user by their username, which in this case is an email.
     * @param email of the user to be loaded
     * @return  UserDetails object containing user information
     * @throws UsernameNotFoundException if no user is found with the provided email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            User user =userOptional.get();
            return new CustomUserDetails(user, Collections.singletonList(user.getRole().toString()));
        }
        throw new UsernameNotFoundException("No user present with email:" + email);
    }
}
