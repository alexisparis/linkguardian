package org.blackdog.linkguardian.repository.search;

import org.blackdog.linkguardian.domain.ToxicLink;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ToxicLink entity.
 */
public interface ToxicLinkSearchRepository extends ElasticsearchRepository<ToxicLink, Long> {
}
