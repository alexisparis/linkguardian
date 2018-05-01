package org.blackdog.linkguardian.web.rest;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.blackdog.linkguardian.web.rest.vm.LoggerVM;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for view and managing Log Level at runtime.
 *
 * used to perform rendering test for favicon.
 */
//@RestController
@RequestMapping("/api")
public class FavIconResource {

    @GetMapping(value="/favicon", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getFavicon() throws IOException {
        InputStream in = getClass()
            .getResourceAsStream("/images/unknown_favicon.png");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }
}
