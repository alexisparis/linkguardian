package org.blackdog.linkguardian.repository.search.dummy;

import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.repository.search.ToxicLinkSearchRepository;
import org.springframework.stereotype.Component;

@Component
public class ToxicLinkSearchRepositoryImpl extends AbstractSearchRepositoryImpl<ToxicLink> implements ToxicLinkSearchRepository {

    @Override
    public Class<ToxicLink> getEntityClass() {
        return ToxicLink.class;
    }
}
