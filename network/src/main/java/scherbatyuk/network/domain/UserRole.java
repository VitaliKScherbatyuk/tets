/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 24.12.2023
 */

package scherbatyuk.network.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * use authentication based on the admin and user values
 */
public enum UserRole implements GrantedAuthority {
    User, Admin;

    @Override
    public String getAuthority() {
        return name();
    }
}
