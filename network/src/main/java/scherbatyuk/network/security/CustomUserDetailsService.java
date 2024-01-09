/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 24.12.2023
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
     * designed to load information about a user by their username, which in this case is an email
     * @param email
     * @return
     * @throws UsernameNotFoundException
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
