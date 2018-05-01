package org.blackdog.linkguardian.repository.search.dummy;

import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.repository.search.BookmarkBatchItemSearchRepository;
import org.springframework.stereotype.Component;

@Component
public class BookmarkBatchItemSearchRepositoryImpl extends AbstractSearchRepositoryImpl<BookmarkBatchItem> implements BookmarkBatchItemSearchRepository {

    @Override
    public Class<BookmarkBatchItem> getEntityClass() {
        return BookmarkBatchItem.class;
    }
}
