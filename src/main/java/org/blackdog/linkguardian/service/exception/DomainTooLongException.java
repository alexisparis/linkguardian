package org.blackdog.linkguardian.service.exception;

import org.blackdog.linkguardian.domain.Link;

public class DomainTooLongException extends LinkException {

    public DomainTooLongException(String domain, Link link) {
        super("domain too long : " + domain, link);
    }
}
