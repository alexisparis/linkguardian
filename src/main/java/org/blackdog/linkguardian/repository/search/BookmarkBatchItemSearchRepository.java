package org.blackdog.linkguardian.repository.search;

import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the BookmarkBatchItem entity.
 */
public interface BookmarkBatchItemSearchRepository extends ElasticsearchRepository<BookmarkBatchItem, Long> {
}
