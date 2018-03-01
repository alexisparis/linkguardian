package org.blackdog.linkguardian.repository.search;

import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the BookmarkBatch entity.
 */
public interface BookmarkBatchSearchRepository extends ElasticsearchRepository<BookmarkBatch, Long> {
}
