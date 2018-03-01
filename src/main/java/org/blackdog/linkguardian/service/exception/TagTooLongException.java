package org.blackdog.linkguardian.service.exception;

import org.blackdog.linkguardian.domain.Link;

public class TagTooLongException extends LinkException {

    public TagTooLongException(String tag, Link link) {
        super("tag too long : " + tag, link);
    }
}
