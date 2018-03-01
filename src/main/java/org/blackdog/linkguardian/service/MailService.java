package org.blackdog.linkguardian.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.blackdog.linkguardian.config.Constants;
import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.domain.User;

import io.github.jhipster.config.JHipsterProperties;

import org.apache.commons.lang3.CharEncoding;
import org.blackdog.linkguardian.domain.transfer.CountPerUser;
import org.blackdog.linkguardian.report.ActivityReport;
import org.blackdog.linkguardian.service.exception.MailNotSentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    @Value("${admin.mail}")
    private String adminMail;

    @Inject
    private Environment env;

    public MailService(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender,
            MessageSource messageSource, SpringTemplateEngine templateEngine) {

        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    /**
     * return true if it is running production environment
     * @return
     */
    private boolean isProdEnvironment() {
        return env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION);
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    public void sendTransactionalEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) throws MailNotSentException {
        try {
            HttpResponse<JsonNode>
                request = Unirest.post("https://api.mailgun.net/v3/" + "mailgun.linkguardian.io" + "/messages")
                .basicAuth("api", "key-009e7aa8126455cbfb194966591f1e51")
                .queryString("from", jHipsterProperties.getMail().getFrom())
                .queryString("to", to)
                .queryString("subject", subject)
                //.queryString("text", "Testing out some Mailgun awesomeness!")
                .queryString("html", content)
                //.field("attachment", new File("/temp/folder/test.txt"))
                .asJson();
            log.debug("Send transactional e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={} returns " +
                    request.getStatus() + " " + request.getBody(),
                isMultipart, isHtml, to, subject, content);
            if (request.getStatus() < 200 || request.getStatus() >= 400) {
                throw new MailNotSentException();
            }
        } catch (UnirestException e) {
            throw new MailNotSentException(e);
        }
    }

    public void sendTransactionalEmailFromTemplate(User user, String templateName, String titleKey) throws MailNotSentException {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendTransactionalEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);

    }

    @Async
    public void sendActivationEmail(User user) throws MailNotSentException {
        log.debug("Sending activation email to '{}'", user.getEmail());

        if (Objects.equals("linkguardian@blackdog-project.org", user.getEmail())) {
            sendEmailFromTemplate(user, "activationEmail", "email.activation.title");
        } else {
            sendTransactionalEmailFromTemplate(user, "activationEmail", "email.activation.title");
        }

        // warn admin
        sendGenericAdminEmail("new activation mail sent", "user mail activation sent to " + user.getEmail());
        // todo
    }

    @Async
    public void sendCreationEmail(User user) throws MailNotSentException {
        log.debug("Sending creation email to '{}'", user.getEmail());

        sendTransactionalEmailFromTemplate(user, "creationEmail", "email.activation.title");

        // warn admin
        sendGenericAdminEmail("account creation mail sent", "account creation mail sent to " + user.getEmail());
    }

    @Async
    public void sendPasswordResetMail(User user) throws MailNotSentException {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendTransactionalEmailFromTemplate(user, "passwordResetEmail", "email.reset.title");
    }

    @Async
    public void sendSocialRegistrationValidationEmail(User user, String provider) {
        log.debug("Sending social registration validation email to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        context.setVariable("provider", StringUtils.capitalize(provider));
        String content = templateEngine.process("socialRegistrationValidationEmail", context);
        String subject = messageSource.getMessage("email.social.registration.title", null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendGenericAdminEmail(String subject, String message) {
        Context context = new Context();
        context.setVariable("message", message);
        String content = templateEngine.process("genericAdmin", context);
        sendEmail(adminMail, (isProdEnvironment() ? "[PROD] " : "") + "[ADMIN] " + subject, content, false, true);
    }

    @Async
    public void sendBookmarkBatchFinishedEmail(User user) {
        log.debug("Sending bookmark batch finished e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);

        String content = templateEngine.process("batchFinishedEmail", context);
        String subject = messageSource.getMessage("email.batch.finished.title", null, locale);
        try {
            sendTransactionalEmail(user.getEmail(), subject, content, false, true);
        } catch (MailNotSentException e) {
            log.error("could not send bookmark batch finished mail to " + user.getEmail(), e);
        }

        // warn admin
        sendGenericAdminEmail("new bookmark batch finished mail sent", "new bookmark batch finished mail sent to " + user.getEmail());
    }

    @Async
    public void sendTemporaryDisabledAccountMail(User user) {
        sendGenericAdminEmail("account temporary disabled mail sent", "account temporary disabled mail sent to " + user.getEmail());
    }

    @Async
    public void sendStatisticsEmail(Collection<CountPerUser> linkCreatedCount, Map<String, List<ToxicLink>> toxicLinks,
        List<CountPerUser> batchNotCompletedCount, List<CountPerUser> batchItemNotCompletedCount) {

        log.debug("Sending stats e-mail to '{}'", adminMail);
        Locale locale = Locale.ENGLISH;
        Context context = new Context(locale);
        context.setVariable("linkCreatedCount", linkCreatedCount);
        context.setVariable("message", linkCreatedCount.isEmpty() ? "No new link added" : "New links added : ");
        context.setVariable("toxicLinksPerMail", toxicLinks);

        context.setVariable("batchNotCompletedCount", batchNotCompletedCount);
        context.setVariable("batchItemNotCompletedCount", batchItemNotCompletedCount);

        String content = templateEngine.process("statisticsEmail", context);
        String subject = (isProdEnvironment() ? "[PROD] " : "") + "[ADMIN] Statistics at " + LocalDateTime
            .now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        sendEmail(adminMail,
            subject,
            content, false, true);
    }

    @Async
    public void sendBlockReportEmail(ActivityReport report, Map<String, TemporalAmount> blockDurationPerUser) {

        log.debug("Sending blocking report e-mail to '{}'", adminMail);
        Locale locale = Locale.ENGLISH;
        Context context = new Context(locale);
        context.setVariable("report", report);
        context.setVariable("blockDurationPerUser", blockDurationPerUser);

        String content = templateEngine.process("blockReportEmail", context);
        String subject = (isProdEnvironment() ? "[PROD] " : "") + "[ADMIN] Block Report at " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        sendEmail(adminMail,
            subject,
            content, false, true);
    }
}
