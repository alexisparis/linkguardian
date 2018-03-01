package org.blackdog.linkguardian.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by alexisparis on 23/03/16.
 */
public class TagRepositoryImpl implements TagRepositoryCustom {

    private final Logger log = LoggerFactory.getLogger(TagRepositoryImpl.class);

    @Inject
    private EntityManager manager;

    @Override
    public List<String> getTags(Number userId, String filter) {
        int maxResults = 100;
        final List<String> results = new ArrayList<>(maxResults);

        StringBuilder builder = new StringBuilder();
        builder.append("select distinct Tag.label from Tag");
        builder.append(" join link_tag on Tag.id = link_tag.tags_id");
        builder.append(" join link on link_tag.links_id = link.id");
        builder.append(" where link.user_id = " + userId);
        if ( filter != null ) {
            builder.append(" and Tag.label like '" + filter + "%'");
        }
        builder.append(" order by Tag.label");

        log.info("using query : " + builder.toString());

        Query query = this.manager.createNativeQuery(builder.toString());

        query.setMaxResults(maxResults);
        query.getResultList().forEach(new Consumer() {
            @Override
            public void accept(Object o) {
                log.info("adding tag " + o);
                results.add((String)o);
            }
        });

        return results;
    }
}
