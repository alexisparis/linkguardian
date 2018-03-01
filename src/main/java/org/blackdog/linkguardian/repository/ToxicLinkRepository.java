package org.blackdog.linkguardian.repository;

import java.time.ZonedDateTime;
import java.util.List;
import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.domain.transfer.CountPerUser;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;


/**
 * Spring Data JPA repository for the ToxicLink entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ToxicLinkRepository extends JpaRepository<ToxicLink, Long>, JpaSpecificationExecutor<ToxicLink> {

    @Transactional
    @Query(value = "select link from ToxicLink link "
        + " WHERE link.creationDate > :date")
    List<ToxicLink> findToxicLinksAfter(@Param("date") java.time.ZonedDateTime date);

    Long countByEmailAndCreationDateIsAfter(String mail, ZonedDateTime date);

    @Transactional
    @Query(value = "select new org.blackdog.linkguardian.domain.transfer.CountPerUser(user.login, link.email, count(link))"
        + " from ToxicLink link, User user "
        + " WHERE"
        + " link.email = user.email AND"
        + " link.creationDate > :date group by link.email, user.login having count(link) >= :havingCountVal")
    List<CountPerUser> countByEmailCreationDateIsAfterHavingCount(@Param("date") ZonedDateTime date, @Param("havingCountVal") Long havingCountVal);
}
