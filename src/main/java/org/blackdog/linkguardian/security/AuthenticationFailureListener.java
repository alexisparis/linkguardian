package org.blackdog.linkguardian.security;

import javax.inject.Inject;
import org.blackdog.linkguardian.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener
    implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private LoginAttemptService loginAttemptService;

    public AuthenticationFailureListener(LoginAttemptService service) {
        this.loginAttemptService = service;
    }

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        WebAuthenticationDetails auth = null;

        if (e.getAuthentication() != null && e.getAuthentication().getDetails() instanceof WebAuthenticationDetails) {
            auth = (WebAuthenticationDetails) e.getAuthentication().getDetails();
        }

        if (auth == null) {
            if (SecurityContextHolder.getContext() != null &&
                SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof WebAuthenticationDetails) {
                auth = (WebAuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
            }
        }

        if (auth != null) {
            loginAttemptService.loginFailed(auth.getRemoteAddress());
        }
    }
}

