package org.blackdog.linkguardian.batch;

import java.util.concurrent.CopyOnWriteArrayList;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.repository.BookmarkBatchItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * https://gist.github.com/ghusta/abdd0864bd8a3bb398d241e20ac241bd
 */
@Component
public class BatchItemReader extends AbstractPagingItemReader<BookmarkBatchItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchItemReader.class);

    @Autowired
    BookmarkBatchItemRepository repository;

    @Override
    protected void doReadPage() {

        LOGGER.info("@@ call " + this.getClass().getSimpleName() + " doReadPage");
        Pageable pageable = new PageRequest(getPage(), getPageSize());

        Page<BookmarkBatchItem> pageResults = repository.findNotImportedOrderByIdAsc(pageable);

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
