package org.blackdog.linkguardian.repository.search.dummy;

import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.repository.search.LinkSearchRepository;
import org.springframework.stereotype.Component;

@Component
public class LinkSearchRepositoryImpl extends AbstractSearchRepositoryImpl<Link> implements LinkSearchRepository {

    @Override
    public Class<Link> getEntityClass() {
        return Link.class;
    }
}
