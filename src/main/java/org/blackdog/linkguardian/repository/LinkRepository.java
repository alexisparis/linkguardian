package org.blackdog.linkguardian.repository;

import java.time.ZonedDateTime;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.transfer.CountPerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Link entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LinkRepository extends JpaRepository<Link, Long>, JpaSpecificationExecutor<Link>, LinkRepositoryCustom {

    @Query("select link from Link link where link.user.login = ?#{principal.username}")
    List<Link> findByUserIsCurrentUser();

    @Query("select distinct link from Link link left join fetch link.tags")
    List<Link> findAllWithEagerRelationships();

    @Query("select link from Link link left join fetch link.tags where link.id =:id")
    Link findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select distinct link from Link link join link.tags" +
        " where link.user.login =:userLogin")
    Page<Link> findLinksOfUser2(@Param("userLogin") String userLogin, Pageable pageable);

    @Query("select distinct link from Link link " +
        " where link.user.login =:userLogin and link.originalUrl =:url")
    Link findLinksByUserAndUrl(@Param("userLogin") String userLogin, @Param("url") String url);

    @Query(value = "select new org.blackdog.linkguardian.domain.transfer.CountPerUser(link.user.login, link.user.email, count(link)) from Link link "
        + " WHERE link.creationDate > :date group by link.user.login, link.user.email")
    List<CountPerUser> findLinkCountPerUserAfter(@Param("date") java.time.ZonedDateTime date);

    @Transactional
    @Query(value = "select new org.blackdog.linkguardian.domain.transfer.CountPerUser(link.user.login, link.user.email, count(link))"
        + " from Link link"
        + " WHERE"
        + " link.creationDate > :date group by link.user.login, link.user.email having count(link) >= :havingCountVal")
    List<CountPerUser> countByUserCreationDateIsAfterHavingCount(@Param("date") ZonedDateTime date, @Param("havingCountVal") Long havingCountVal);

    @Query("select count(link) from Link link where link.user.login =:userLogin")
    Long countByLogin(@Param("userLogin") String userLogin);

}
