package org.blackdog.linkguardian.service.dto;

import java.io.Serializable;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;



import io.github.jhipster.service.filter.ZonedDateTimeFilter;


/**
 * Criteria class for the BookmarkBatch entity. This class is used in BookmarkBatchResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /bookmark-batches?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BookmarkBatchCriteria implements Serializable {
    /**
     * Class for filtering BookmarkBatchStatus
     */
    public static class BookmarkBatchStatusFilter extends Filter<BookmarkBatchStatus> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private ZonedDateTimeFilter creation_date;

    private ZonedDateTimeFilter status_date;

    private BookmarkBatchStatusFilter status;

    private LongFilter userId;

    private LongFilter itemsId;

    public BookmarkBatchCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(ZonedDateTimeFilter creation_date) {
        this.creation_date = creation_date;
    }

    public ZonedDateTimeFilter getStatus_date() {
        return status_date;
    }

    public void setStatus_date(ZonedDateTimeFilter status_date) {
        this.status_date = status_date;
    }

    public BookmarkBatchStatusFilter getStatus() {
        return status;
    }

    public void setStatus(BookmarkBatchStatusFilter status) {
        this.status = status;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getItemsId() {
        return itemsId;
    }

    public void setItemsId(LongFilter itemsId) {
        this.itemsId = itemsId;
    }

    @Override
    public String toString() {
        return "BookmarkBatchCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (creation_date != null ? "creation_date=" + creation_date + ", " : "") +
                (status_date != null ? "status_date=" + status_date + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (itemsId != null ? "itemsId=" + itemsId + ", " : "") +
            "}";
    }

}
