package org.blackdog.linkguardian.security.exception;

import org.springframework.security.core.AuthenticationException;

public class BrutForceLoginException extends AuthenticationException {

    public BrutForceLoginException() {
        super("blocked");
    }
}
