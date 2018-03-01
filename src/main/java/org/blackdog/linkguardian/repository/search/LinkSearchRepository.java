package org.blackdog.linkguardian.repository.search;

import org.blackdog.linkguardian.domain.Link;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Link entity.
 */
public interface LinkSearchRepository extends ElasticsearchRepository<Link, Long> {
}
