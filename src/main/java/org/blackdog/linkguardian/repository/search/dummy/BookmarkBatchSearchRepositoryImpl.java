package org.blackdog.linkguardian.repository.search.dummy;

import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.repository.search.BookmarkBatchSearchRepository;
import org.springframework.stereotype.Component;

@Component
public class BookmarkBatchSearchRepositoryImpl extends AbstractSearchRepositoryImpl<BookmarkBatch> implements BookmarkBatchSearchRepository {

    @Override
    public Class<BookmarkBatch> getEntityClass() {
        return BookmarkBatch.class;
    }
}
