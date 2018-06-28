package org.blackdog.linkguardian.repository;

import java.util.List;
import java.util.Set;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
import org.blackdog.linkguardian.domain.transfer.CountPerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the BookmarkBatchItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookmarkBatchItemRepository extends JpaRepository<BookmarkBatchItem, Long>, JpaSpecificationExecutor<BookmarkBatchItem> {

    @Query("select item from BookmarkBatchItem item " +
        " where item.status = 'NOT_IMPORTED' order by item.id asc")
    Page<BookmarkBatchItem> findNotImportedOrderByIdAsc(Pageable pageable);

    Page<BookmarkBatchItem> findByStatusOrderByIdAsc(BookmarkBatchItemStatus status, Pageable pageable);

    @Query("select item from BookmarkBatchItem item where item.bookmarkBatch.id =:id")
    Set<BookmarkBatchItem> findItemsOfBookmarkBatch(@Param("id") Long id);

    @Query(value = "select new org.blackdog.linkguardian.domain.transfer.CountPerUser(item.bookmarkBatch.user.login,"
        + " item.bookmarkBatch.user.email, count(item)) from BookmarkBatchItem item "
        + " WHERE item.status = 'NOT_IMPORTED' group by item.bookmarkBatch.user.login, item.bookmarkBatch.user.email")
    List<CountPerUser> findNotImportedBatchItemCountPerUser();

}
