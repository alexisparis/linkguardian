package org.blackdog.linkguardian.batch;

import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BatchProcessor implements ItemProcessor<BookmarkBatch, BookmarkBatch> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchProcessor.class);

    @Override
    public BookmarkBatch process(BookmarkBatch data) throws Exception {
        return data;
    }
}
