package org.blackdog.linkguardian.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.transfer.CountPerTag;
import org.blackdog.linkguardian.domain.transfer.ReadStatus;
import org.blackdog.linkguardian.domain.transfer.SortDirection;
import org.blackdog.linkguardian.domain.transfer.SortType;
import org.blackdog.linkguardian.repository.exception.TooMuchTagException;
import org.blackdog.linkguardian.service.util.TagsNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Created by alexisparis on 23/03/16.
 */
public class LinkRepositoryImpl implements LinkRepositoryCustom {

    private final Logger log = LoggerFactory.getLogger(LinkRepositoryImpl.class);

    @Inject
    private EntityManager manager;

    @Inject
    private TagsNormalizer tagsNormalizer;

    @Override
    public Page<Link> customFindLinksOfUser(String userLogin, String tag,
        ReadStatus status, SortType sortType,
        SortDirection direction, Pageable pageable) throws TooMuchTagException {

        log.info("call customFindLinksOfUser(" + userLogin + ", " + tag + ", " +
            status + ", " + sortType + ", " + direction + " with pageable " + pageable + ")");


        CriteriaBuilder criteriaBuilder = this.manager.getCriteriaBuilder();

        StringBuilder query = new StringBuilder();
        query.append("select distinct link from Link link ");
        query.append("left join link.tags tag ");
        query.append("where link.user.login =:userLogin");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userLogin", userLogin);

        // ############
        // consider tag
        // ############

        Set<String> tags = this.tagsNormalizer.split(tag, " ", true);
        tags = tags.stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                boolean result = false;

                if ( s != null ) {
                    s = s.trim();
                    if ( s.length() > 0 ) {
                        result = true;
                    }
                }

                return result;
            }
        }).collect(Collectors.toSet());

        if ( tags != null && ! tags.isEmpty() ) {
            if ( tags.size() == 1 ) {
                query.append(" AND tag.label like CONCAT(:tag,'%')");
                parameters.put("tag", tags.iterator().next());
            } else {
                throw new TooMuchTagException("provide no more than one tag");
            }
        }

        // ############
        // consider status
        // ############
        if ( status != null && ! ReadStatus.ALL.equals(status) ) {
            query.append(" AND link.read = " +
                (ReadStatus.READ.equals(status) ? "TRUE" : "FALSE"));
        }

        // ############
        // SORT
        // ############
        query.append(" ORDER BY link." +
            (SortType.CREATION_DATE.equals(sortType) ? "creationDate" : "note") +
            " " + direction.toString());

        // ############
        // create jpql query
        // ############
        log.info("executing request " + query.toString());
        Query jpqlQuery = this.manager.createQuery(query.toString());

        for(Map.Entry<String, Object> entry : parameters.entrySet()) {
            jpqlQuery = jpqlQuery.setParameter(entry.getKey(), entry.getValue());
        }

        List<Link> results = (List<Link>)jpqlQuery.
            setMaxResults(pageable.getPageSize()).
            setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).
            getResultList();

        return new PageImpl<Link>(results, pageable, results.size());
    }

    @Override
    public List<CountPerTag> getCountPerTags(String userLogin) {
        final List<CountPerTag> counts = new ArrayList<>();

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT tag.id, label, count(*) ");
        builder.append("FROM link inner join link_tag on link.id = link_tag.links_id " +
            "inner join tag on tag.id = link_tag.tags_id ");
        builder.append("inner join jhi_user on link.user_id = jhi_user.id ");
        builder.append("WHERE jhi_user.login = '" + userLogin + "' ");
        builder.append("group by tag.id ");
        builder.append("order by count(*) desc");

        Query query = this.manager.createNativeQuery(builder.toString());


        query.setMaxResults(150);
        query.getResultList().forEach(new Consumer() {
            @Override
            public void accept(Object o) {
                Object[] array = (Object[])o;
                counts.add(new CountPerTag(
                    ((Number)array[0]).longValue(),
                    ((String)array[1]),
                    ((Number)array[2]).intValue()));
            }
        });

        return counts;
    }
}
