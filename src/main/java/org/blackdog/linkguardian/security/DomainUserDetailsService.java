package org.blackdog.linkguardian.security;

import javax.servlet.http.HttpServletRequest;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.repository.UserRepository;
import org.blackdog.linkguardian.security.exception.BrutForceLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.blackdog.linkguardian.service.LoginAttemptService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    private LoginAttemptService loginAttemptService;

    private HttpServletRequest request;

    public DomainUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService, HttpServletRequest request) {

        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.request = request;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);

        String ip = getClientIP();
        log.debug("using ip : " + ip);
        if (loginAttemptService.isBlocked(ip)) {
            log.warn("ip " + ip + " blocked => login " + login + " blocked");
            throw new BrutForceLoginException();
        }

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        Optional<User> userByEmailFromDatabase = userRepository.findOneWithAuthoritiesByEmail(lowercaseLogin);
        return userByEmailFromDatabase.map(user -> createSpringSecurityUser(lowercaseLogin, user)).orElseGet(() -> {
            Optional<User> userByLoginFromDatabase = userRepository.findOneWithAuthoritiesByLogin(lowercaseLogin);
            return userByLoginFromDatabase.map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
                    "database"));
        });
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.getActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
            .collect(Collectors.toList());
        log.info("for user " + lowercaseLogin + " consider authorities : " + grantedAuthorities);
        return new org.springframework.security.core.userdetails.User(user.getLogin(),
            user.getPassword(),
            grantedAuthorities);
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
