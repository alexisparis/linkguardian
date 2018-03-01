package org.blackdog.linkguardian.service;

import java.time.ZonedDateTime;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBlocker {

    @Autowired
    private UserRepository userRepository;

    @Async
    @Transactional
    public void blockUserUntil(String login, ZonedDateTime date) {

        // load user
        User user = this.userRepository.findOneByLogin(login).get();
        user.setLockEndDate(date);
        // update it
        this.userRepository.save(user);
    }
}
