package org.blackdog.linkguardian.repository;

import java.time.ZonedDateTime;
import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.domain.transfer.CountPerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the BookmarkBatch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookmarkBatchRepository extends JpaRepository<BookmarkBatch, Long>, JpaSpecificationExecutor<BookmarkBatch> {

    @Query("select bookmark_batch from BookmarkBatch bookmark_batch where bookmark_batch.user.login = ?#{principal.username}")
    List<BookmarkBatch> findByUserIsCurrentUser();


    @Query(value = "select batch from BookmarkBatch batch WHERE batch.id NOT IN ( "
        + "select DISTINCT(item.bookmarkBatch.id) from BookmarkBatchItem item "
        + "where item.status = 'NOT_IMPORTED') "
        + "AND batch.status = 'NOT_IMPORTED'")
    Page<BookmarkBatch> findFinishedBatchNotYetMarkedImported(Pageable page);

    @Query(value = "select new org.blackdog.linkguardian.domain.transfer.CountPerUser(batch.user.login, batch.user.email, count(batch)) from BookmarkBatch batch "
        + " WHERE batch.status = 'NOT_IMPORTED' group by batch.user.login, batch.user.email")
    List<CountPerUser> findNotImportedBatchCountPerUser();

    @Transactional
    @Query(value = "select new org.blackdog.linkguardian.domain.transfer.CountPerUser(batch.user.login, batch.user.email, count(batch))"
        + " from BookmarkBatch batch"
        + " WHERE"
        + " batch.creationDate > :date group by batch.user.login, batch.user.email having count(batch) >= :havingCountVal")
    List<CountPerUser> countByUserCreationDateIsAfterHavingCount(@Param("date") ZonedDateTime date, @Param("havingCountVal") Long havingCountVal);

}
