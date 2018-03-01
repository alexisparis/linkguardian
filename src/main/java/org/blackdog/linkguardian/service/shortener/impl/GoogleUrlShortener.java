package org.blackdog.linkguardian.service.shortener.impl;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;
import org.blackdog.linkguardian.service.shortener.UrlShortener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by alexisparis on 17/03/16.
 */
@Service
public class GoogleUrlShortener implements UrlShortener {

    private final Logger log = LoggerFactory.getLogger(GoogleUrlShortener.class);

    static Urlshortener newUrlshortener() {

        ClientParametersAuthentication credential = new ClientParametersAuthentication(
            "717205487286-l69l83qrngicomfd4i83j2r88531pari.apps.googleusercontent.com",
            "vQ5c8_RYJd18wr3diGTpgYGca"
        );

        HttpTransport transport = null;

        transport = new NetHttpTransport();
        return new Urlshortener.Builder(transport, new JacksonFactory(), null).build();
    }

    @Override
    public String shorten(String longUrl) {

        String result = null;

        if (longUrl != null) {
            Urlshortener shortener = newUrlshortener();
            try {

                Url url = new Url();
                url.setLongUrl(longUrl);

                Url shortUrl = shortener.url().insert(url).setKey("AIzaSyDhzSrTh75y7ixjnct7l3ygyjlgRiXxxRc").execute();

                if (shortUrl != null) {
                    result = shortUrl.getId();
                }
            } catch (IOException e) {
                log.error("error while shortening " + longUrl, e);
            }
        }

        log.info("shortening " + longUrl + " returns " + result);

        return result;
    }


}
