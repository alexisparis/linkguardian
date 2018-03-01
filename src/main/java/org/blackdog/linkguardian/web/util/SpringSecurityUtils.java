package org.blackdog.linkguardian.web.util;

import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityUtils {

    @Autowired
    private UserRepository userRepository;

    /**
     * return the connected user
     * @return a {@link User} or null
     */
    public User getUser() {
        User user = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String name = auth.getName(); //get logged in username

            if (name != null) {
                user = this.userRepository.findOneByLogin(name).get();
            }
        }

        return user;
    }

}
