package org.blackdog.linkguardian.batch;

import java.util.List;
import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchStatus;
import org.blackdog.linkguardian.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BatchWriter implements ItemWriter<BookmarkBatch> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchWriter.class);

    @Autowired
    private MailService mailService;

    @Override
    public void write(List<? extends BookmarkBatch> batchs) throws Exception {
        for (BookmarkBatch batch : batchs) {
            batch.setStatus(BookmarkBatchStatus.IMPORTED);

            mailService.sendBookmarkBatchFinishedEmail(batch.getUser());
        }
    }

}
