package org.blackdog.linkguardian.security;

import org.blackdog.linkguardian.service.LoginAttemptService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener
  implements ApplicationListener<AuthenticationSuccessEvent> {

    private LoginAttemptService loginAttemptService;

    public AuthenticationSuccessEventListener(LoginAttemptService service) {
        this.loginAttemptService = service;
    }

    public void onApplicationEvent(AuthenticationSuccessEvent e) {
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
            loginAttemptService.loginSucceeded(auth.getRemoteAddress());
        }
    }
}
