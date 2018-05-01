package org.blackdog.linkguardian.repository.search.dummy;

import org.blackdog.linkguardian.domain.Tag;
import org.blackdog.linkguardian.repository.search.TagSearchRepository;
import org.springframework.stereotype.Component;

@Component
public class TagSearchRepositoryImpl extends AbstractSearchRepositoryImpl<Tag> implements TagSearchRepository {

    @Override
    public Class<Tag> getEntityClass() {
        return Tag.class;
    }
}
