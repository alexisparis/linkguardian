package org.blackdog.linkguardian.web.rest;

import com.codahale.metrics.annotation.Timed;
import java.util.HashSet;
import java.util.Set;
import org.blackdog.linkguardian.service.MailService;
import org.blackdog.linkguardian.service.UserService;
import org.blackdog.linkguardian.service.dto.GenericResponse;
import org.blackdog.linkguardian.service.dto.UserDTO;
import org.blackdog.linkguardian.service.exception.MailNotSentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private UserService userService;

    @Value("${admin.mail}")
    private String adminMail;

    public MailResource(MailService mailService, UserService userService) {
        this.mailService = mailService;
        this.userService = userService;
    }

    @PostMapping("/mail")
    @Timed
    public ResponseEntity sendMail(String title, String message, String to) {

        System.out.println("title : '" + title + "', message : '" + message + "', to : '" + to + "'");

        MailRecipient mailRecipient = MailRecipient.valueOf(to);
        if(mailRecipient == null) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE);
        }
        int countOfMailSent = 0;

        if(mailRecipient.equals(MailRecipient.ADMIN)) {
            try {
                mailService.sendAnnouncementEmail(adminMail, "admin", title, message);
                countOfMailSent++;
            } catch (MailNotSentException e) {
                log.error("error while trying to send mail", e);
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.withMessage("0 sent, 1 not send"));
            }
        } else if (mailRecipient.equals(MailRecipient.ALL)) {

            // on all active users
            int pageSize = 100;
            int pageIndex = 0;

            Set<String> mailsWithError = new HashSet<>();

            while (true) {
                Page<UserDTO> allManagedUsers = this.userService.getAllManagedUsers(new PageRequest(pageIndex, pageSize));

                if ( allManagedUsers == null || allManagedUsers.getNumberOfElements() == 0 ) {
                    break;
                }

                for(int i = 0; i < allManagedUsers.getContent().size(); i++) {
                    UserDTO current = allManagedUsers.getContent().get(i);
                    if (current.getEmail() != null) {

                        try {
                            mailService.sendAnnouncementEmail(current.getEmail(), current.getLogin(), title, message);
                            countOfMailSent++;
                        } catch (MailNotSentException e) {
                            mailsWithError.add(current.getEmail());
                            log.error("error while trying to send mail to " + current.getEmail(), e);
                        }
                    }
                }

                pageIndex++;
            }

            if (!mailsWithError.isEmpty()) {
                StringBuilder messageContent = new StringBuilder();

                messageContent.append("<p>mails sent :");
                messageContent.append(countOfMailSent);
                messageContent.append("<br/>");
                messageContent.append("<p>mails not sent :");
                messageContent.append(mailsWithError.size());
                messageContent.append("</p>");

                messageContent.append("<p>error while trying to send message with title :<br/>");
                messageContent.append(title);
                messageContent.append("<br/>");
                messageContent.append("message : <br/>");
                messageContent.append(message);
                messageContent.append("<br/>");
                messageContent.append("to : ");
                messageContent.append("</p>");

                messageContent.append("<p>");
                messageContent.append("<ul>");
                mailsWithError
                    .forEach(s -> {
                        messageContent.append("<li>");
                        messageContent.append(s);
                        messageContent.append("</li>");

                    });
                messageContent.append("</ul>");
                messageContent.append("</p>");

                mailService.sendGenericAdminEmail("announcement mail sending error", messageContent.toString());

                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.withMessage(countOfMailSent + " sent, " + mailsWithError.size() + " not send"));
            }
        }

        return ResponseEntity.ok().body(GenericResponse.withMessage(countOfMailSent + " sent"));
    }

    enum MailRecipient {
        ADMIN,
        ALL;
    }
}
