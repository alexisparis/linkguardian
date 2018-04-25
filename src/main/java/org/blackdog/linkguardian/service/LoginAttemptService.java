package org.blackdog.linkguardian.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * from http://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 */
@Component
public class LoginAttemptService {
    private final int MAX_ATTEMPT = 40;
    private LoadingCache<String, Integer> attemptsCache;

    private MailService mailService;

    public LoginAttemptService(MailService mailService) {
        super();
        attemptsCache = CacheBuilder.newBuilder().
            expireAfterWrite(1, TimeUnit.HOURS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
        this.mailService = mailService;
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;

        if (attempts == MAX_ATTEMPT) {
            this.mailService.sendGenericAdminEmail("[login] " + key + " blocked", key + " has been blocked for login");
        }

        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
