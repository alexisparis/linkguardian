package org.blackdog.linkguardian.service.exception;

import org.blackdog.linkguardian.domain.Link;

public class UrlTooLongException extends LinkException {

    public UrlTooLongException(String url, Link link) {
        super("url too long : " + url, link);
    }
}
