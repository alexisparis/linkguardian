package org.blackdog.linkguardian.task;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.blackdog.linkguardian.config.Constants;
import org.blackdog.linkguardian.domain.transfer.CountPerUser;
import org.blackdog.linkguardian.report.AbstractActivity;
import org.blackdog.linkguardian.report.ActivityDuringTime;
import org.blackdog.linkguardian.report.ActivityReport;
import org.blackdog.linkguardian.report.ActivityType;
import org.blackdog.linkguardian.repository.BookmarkBatchRepository;
import org.blackdog.linkguardian.repository.LinkRepository;
import org.blackdog.linkguardian.repository.ToxicLinkRepository;
import org.blackdog.linkguardian.repository.UserRepository;
import org.blackdog.linkguardian.service.MailService;
import org.blackdog.linkguardian.service.UserBlocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile(Constants.SPRING_PROFILE_PRODUCTION)
public class AbuseCheckerTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbuseCheckerTasks.class);

    @Inject
    private UserBlocker userBlocker;

    @Inject
    private UserRepository userRepository;

    @Inject
    private LinkRepository linkRepository;

    @Autowired
    private ToxicLinkRepository toxicLinkRepository;

    @Autowired
    private BookmarkBatchRepository bookmarkBatchRepository;

    @Inject
    private MailService mailService;

    @Scheduled(cron = "0 * * * * *")
    public void detectAbuses() {

        ActivityReport report = new ActivityReport();

        ZonedDateTime now = ZonedDateTime.now();
        TemporalAmount duration = null;
        List<CountPerUser> countPerUsers = null;

        // no more than 30 toxic links created over the last minute
        duration = Duration.ofSeconds(30);
        countPerUsers = toxicLinkRepository.countByEmailCreationDateIsAfterHavingCount(
            now.minus(duration),
            30L);
        feedReport(report, countPerUsers, ActivityType.TOXIC_LINK_CREATION, duration);

        // no more than 30 links created over the last minute
        duration = Duration.ofMinutes(1);
        countPerUsers = linkRepository.countByUserCreationDateIsAfterHavingCount(
            now.minus(duration),
            30L);
        feedReport(report, countPerUsers, ActivityType.LINK_CREATION, duration);

        // no more than 10 bookmark batch over last 10 minutes
        duration = Duration.ofMinutes(10);
        countPerUsers = bookmarkBatchRepository.countByUserCreationDateIsAfterHavingCount(
            now.minus(duration),
            10L);
        feedReport(report, countPerUsers, ActivityType.BOOKMARK_BATCH_CREATION, duration);

        LOGGER.info("block report : ");
        // determine the blocked duration per user
        Map<String, TemporalAmount> blockDurationPerUser = new HashMap<>();

        for (String login : report.logins()) {
            LOGGER.info("   login : " + login);
            Iterable<AbstractActivity> activities = report.activityForUser(login);
            if (activities != null) {
                for (AbstractActivity activity : activities) {
                    LOGGER.info("       => " + activity);
                }
            }
            TemporalAmount blockDuration = determineBlockDuration(activities);
            blockDurationPerUser.put(login, blockDuration);
        }

        blockDurationPerUser.forEach((login, blockDuration) -> {
            if (blockDuration != null) {
                userBlocker.blockUserUntil(login, now.plus(blockDuration));
            }
        });

        if (!blockDurationPerUser.isEmpty()) {
            // send mail
            this.mailService.sendBlockReportEmail(report, blockDurationPerUser);
        } else {
            LOGGER.info("no user to block temporary");
        }
    }

    private TemporalAmount determineBlockDuration(Iterable<AbstractActivity> activities) {
        TemporalAmount amount = null;

        if (activities != null) {
            int value = 0;

            Iterator<AbstractActivity> iterator = activities.iterator();
            while(iterator.hasNext()) {
                AbstractActivity current = iterator.next();
                switch(current.getActivityType()) {
                    case TOXIC_LINK_CREATION:
                        value += 20;
                        break;
                    case BOOKMARK_BATCH_CREATION:
                        value += 10;
                        break;
                    case LINK_CREATION:
                        value += 5;
                        break;
                }
            }

            if (value > 0) {
                amount = Duration.ofSeconds(value * 5);
            }
        }

        return amount;
    }

    private void feedReport(ActivityReport report, List<CountPerUser> countPerUsers, ActivityType type, TemporalAmount duration) {
        if (countPerUsers != null) {
            countPerUsers.forEach(countPerUser -> {
                report.reportActivity(countPerUser.getUserLogin(), new ActivityDuringTime(type, countPerUser.getCount(), duration));
            });
        }
    }
}
