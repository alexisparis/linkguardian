package org.blackdog.linkguardian.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestUtils {

    public static <T> ResponseEntity<T> standardTemporaryBlockedResponse() {
        return new ResponseEntity<T>(HttpStatus.LOCKED);
    }

}
