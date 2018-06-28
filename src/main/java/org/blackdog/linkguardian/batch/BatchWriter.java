package org.blackdog.linkguardian.batch;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchStatus;
import org.blackdog.linkguardian.repository.BookmarkBatchItemRepository;
import org.blackdog.linkguardian.repository.BookmarkBatchRepository;
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

    @Autowired
    BookmarkBatchRepository repository;

    @Autowired
    BookmarkBatchItemRepository itemRepository;

    @Override
    public void write(List<? extends BookmarkBatch> batchs) throws Exception {
        for (BookmarkBatch batch : batchs) {
            batch.setStatus(BookmarkBatchStatus.IMPORTED);

            Set<BookmarkBatchItem> items = itemRepository.findItemsOfBookmarkBatch(batch.getId());
            Map<BookmarkBatchItemStatus, Long> countPerStatus = items.stream().collect(Collectors.groupingBy(o -> o.getStatus(), Collectors.counting()));

            mailService.sendBookmarkBatchFinishedEmail(batch.getUser(), countPerStatus);
        }
    }

}
