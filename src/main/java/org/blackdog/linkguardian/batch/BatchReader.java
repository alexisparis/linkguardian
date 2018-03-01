package org.blackdog.linkguardian.batch;

import java.util.concurrent.CopyOnWriteArrayList;
import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.repository.BookmarkBatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class BatchReader extends AbstractPagingItemReader<BookmarkBatch> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchReader.class);

    @Autowired
    BookmarkBatchRepository repository;

    @Override
    protected void doReadPage() {

        LOGGER.info("@@ call " + this.getClass().getSimpleName() + " doReadPage");
        Pageable pageable = new PageRequest(getPage(), getPageSize());

        Page<BookmarkBatch> pageResults = repository.findFinishedBatchNotYetMarkedImported(pageable);

        if (results == null)
        {
            results = new CopyOnWriteArrayList<>();
        }
        else
        {
            results.clear();
        }

        if (!pageResults.getContent().isEmpty())
        {
            results.addAll(pageResults.getContent());
        }
    }

    @Override
    protected void doJumpToPage(int i) {
        // n/a
    }
}
