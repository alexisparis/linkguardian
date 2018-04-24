package org.blackdog.linkguardian.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.blackdog.linkguardian.service.MailService;
import org.blackdog.linkguardian.service.exception.MailNotSentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing sendin mails
 */
@RestController
@RequestMapping("/management")
public class MailResource {

    private final Logger log = LoggerFactory.getLogger(MailResource.class);

    private MailService mailService;

    @Value("${admin.mail}")
    private String adminMail;

    public MailResource(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/mail")
    @Timed
    public ResponseEntity sendMail(String title, String message, String to) {

        System.out.println("title : '" + title + "', message : '" + message + "', to : '" + to + "'");

        MailRecipient mailRecipient = MailRecipient.valueOf(to);
        if(mailRecipient == null) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE);
        }

        try {

            if(mailRecipient.equals(MailRecipient.ADMIN)) {
                mailService.sendAnnouncementEmail(adminMail, "admin", title, message);
            } else if (mailRecipient.equals(MailRecipient.ALL)) {

            }
        } catch (MailNotSentException e) {
            log.error("error while trying to send mail", e);
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok().build();
    }

    enum MailRecipient {
        ADMIN,
        ALL;
    }


}
