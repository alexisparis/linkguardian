package org.blackdog.linkguardian.service.exception;

import org.blackdog.linkguardian.domain.Link;

public class LinkException extends Exception {

    private Link link;

    public LinkException(String message, Link link) {
        this(message, link, null);
    }

    public LinkException(String message, Link link, Exception cause) {
        super(message, cause);
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}
