package org.blackdog.linkguardian.service;

import org.blackdog.linkguardian.domain.Link;

/**
 * Created by alexisparis on 29/03/16.
 */
public class LinkResponse {

    private final Link link;

    private final String messageCode;

    public LinkResponse(Link link, String messageCode) {
        this.link = link;
        this.messageCode = messageCode;
    }

    public Link getLink() {
        return link;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public static LinkResponse of(Link link, String msgCode) {
        return new LinkResponse(link, msgCode);
    }

    public static LinkResponse of(String msgCode, Link link) {
        return new LinkResponse(link, msgCode);
    }

    public static LinkResponse of(String msgCode) {
        return new LinkResponse(null, msgCode);
    }

    public static LinkResponse of(Link link) {
        return new LinkResponse(link, null);
    }

    public static LinkResponse empty() {
        return new LinkResponse(null, null);
    }
}
