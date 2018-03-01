package org.blackdog.linkguardian.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.blackdog.linkguardian.config.Constants;
import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.domain.transfer.CountPerUser;
import org.blackdog.linkguardian.repository.BookmarkBatchItemRepository;
import org.blackdog.linkguardian.repository.BookmarkBatchRepository;
import org.blackdog.linkguardian.repository.LinkRepository;
import org.blackdog.linkguardian.repository.ToxicLinkRepository;
import org.blackdog.linkguardian.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AdministrativeTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrativeTasks.class);

    @Inject
    private LinkRepository linkRepository;

    @Autowired
    private ToxicLinkRepository toxicLinkRepository;

    @Autowired
    private BookmarkBatchRepository bookmarkBatchRepository;

    @Autowired
    private BookmarkBatchItemRepository bookmarkBatchItemRepository;

    @Inject
    private MailService mailService;

    // near midnight
//    @Scheduled(cron = "0 59 23 * * *")
    // every 2 hours
    @Scheduled(cron = "0 0 */2 * * *")
    @Profile(Constants.SPRING_PROFILE_PRODUCTION)
    public void sendStatusMailSinceYesterday() {
        java.time.ZonedDateTime dt = java.time.ZonedDateTime.now();
        dt = dt.minusDays(1);

        List<CountPerUser> linkPerUserYesterday =
            this.linkRepository.findLinkCountPerUserAfter(dt);

        List<ToxicLink> toxicLinks = this.toxicLinkRepository.findToxicLinksAfter(dt);
        Map<String, List<ToxicLink>> toxicLinksPerMail =
            toxicLinks
                .stream()
                .collect(
                    Collectors.groupingBy(w -> w.getEmail()));

        // bookmark batch
        LOGGER.info("before bookmarkBatchRepository.findNotImportedBatchCountPerUser");
        List<CountPerUser> notImportedBatchCountPerUser =
            bookmarkBatchRepository.findNotImportedBatchCountPerUser();
        LOGGER.info("after bookmarkBatchRepository.findNotImportedBatchCountPerUser");

        // bookmark batch item
        LOGGER.info("before bookmarkBatchItemRepository.findNotImportedBatchItemCountPerUser");
        List<CountPerUser> notImportedBatchItemCountPerUser =
            bookmarkBatchItemRepository.findNotImportedBatchItemCountPerUser();
        LOGGER.info("after bookmarkBatchItemRepository.findNotImportedBatchItemCountPerUser");

        this.mailService.sendStatisticsEmail(linkPerUserYesterday, toxicLinksPerMail,
            notImportedBatchCountPerUser, notImportedBatchItemCountPerUser);
    }

//    @EventListener(ContextRefreshedEvent.class)
    public void sendAllKindOfMail() throws Exception {

//        bookmarkBatchItemRepository.findNotImportedBatchItemCountPerUser();
////        if (true) return;
//
//        User user = new User();
//        user.setEmail("alexis.rt.paris@gmail.com");
//        user.setLangKey("en");
//        user.setFirstName("John");
//        user.setLastName("DOE");
//        user.setLogin("jdoe");
//        this.mailService.sendActivationEmail(user, "https://www.linkguardian.io");
//        if (true) return;
//        this.mailService.sendPasswordResetMail(user, "https://www.linkguardian.io");
//        this.mailService.sendCreationEmail(user, "https://www.linkguardian.io");
//        this.mailService.sendTemporaryDisabledAccountMail(user);
//
//        user.setLangKey("fr");
//        this.mailService.sendActivationEmail(user, "https://www.linkguardian.io");
//        this.mailService.sendPasswordResetMail(user, "https://www.linkguardian.io");
//        this.mailService.sendCreationEmail(user, "https://www.linkguardian.io");
//        this.mailService.sendTemporaryDisabledAccountMail(user);

        List<CountPerUser> linkPerUserYesterday = new ArrayList<>();
        linkPerUserYesterday.add(new CountPerUser("toto@linkguardian.io", "toto", 3L));
        linkPerUserYesterday.add(new CountPerUser("titi@linkguardian.io", "titi", 2L));

        List<ToxicLink> toxicLinks = new ArrayList<>();
        ToxicLink toxicLink1 = new ToxicLink();
        toxicLink1.setUrl("www.tata.toto");
        toxicLink1.setError("error 1");
        toxicLink1.setEmail("toto@linkguardian.io");
        toxicLinks.add(toxicLink1);
        ToxicLink toxicLink2 = new ToxicLink();
        toxicLink2.setUrl("www.tata.tutu");
        toxicLink2.setError("error 2");
        toxicLink2.setEmail("titi@linkguardian.io");
        toxicLinks.add(toxicLink2);
        Map<String, List<ToxicLink>> toxicLinksPerMail =
            toxicLinks
                .stream()
                .collect(
                    Collectors.groupingBy(w -> w.getEmail()));

        List<CountPerUser> batchs = new ArrayList<>();
        batchs.add(new CountPerUser("batch1@linkguardian.io", "toto", 3L));
        batchs.add(new CountPerUser("batch2@linkguardian.io", "titi", 2L));

        List<CountPerUser> batchItems = new ArrayList<>();
        batchItems.add(new CountPerUser("batch_item1@linkguardian.io", "toto", 3L));
        batchItems.add(new CountPerUser("batch_item2@linkguardian.io", "titi", 2L));

        this.mailService.sendStatisticsEmail(linkPerUserYesterday, toxicLinksPerMail, batchs, batchItems);

//        ActivityReport report = new ActivityReport();
//        report.reportActivity("a", new ActivityDuringTime(ActivityType.LINK_CREATION, 3L, Duration.ofSeconds(23)));
//        report.reportActivity("a", new ActivityDuringTime(ActivityType.TOXIC_LINK_CREATION, 3L, Duration.ofMinutes(23)));
//        report.reportActivity("b", new ActivityDuringTime(ActivityType.BOOKMARK_BATCH_CREATION, 3L, Duration.ofSeconds(23)));
//
//        Map<String, TemporalAmount> map = new HashMap<>();
//        map.put("d", Duration.ofHours(2L));
//        map.put("e", Duration.ofHours(24L));
//        this.mailService.sendBlockReportEmail(report, map);
    }
}
