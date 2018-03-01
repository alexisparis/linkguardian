package org.blackdog.linkguardian.service.dto;

import java.io.Serializable;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;



import io.github.jhipster.service.filter.ZonedDateTimeFilter;


/**
 * Criteria class for the BookmarkBatchItem entity. This class is used in BookmarkBatchItemResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /bookmark-batch-items?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BookmarkBatchItemCriteria implements Serializable {
    /**
     * Class for filtering BookmarkBatchItemStatus
     */
    public static class BookmarkBatchItemStatusFilter extends Filter<BookmarkBatchItemStatus> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter url;

    private StringFilter tags;

    private ZonedDateTimeFilter link_creation_date;

    private BookmarkBatchItemStatusFilter status;

    private StringFilter error_msg_code;

    public BookmarkBatchItemCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getUrl() {
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getTags() {
        return tags;
    }

    public void setTags(StringFilter tags) {
        this.tags = tags;
    }

    public ZonedDateTimeFilter getLink_creation_date() {
        return link_creation_date;
    }

    public void setLink_creation_date(ZonedDateTimeFilter link_creation_date) {
        this.link_creation_date = link_creation_date;
    }

    public BookmarkBatchItemStatusFilter getStatus() {
        return status;
    }

    public void setStatus(BookmarkBatchItemStatusFilter status) {
        this.status = status;
    }

    public StringFilter getError_msg_code() {
        return error_msg_code;
    }

    public void setError_msg_code(StringFilter error_msg_code) {
        this.error_msg_code = error_msg_code;
    }

    @Override
    public String toString() {
        return "BookmarkBatchItemCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (url != null ? "url=" + url + ", " : "") +
                (tags != null ? "tags=" + tags + ", " : "") +
                (link_creation_date != null ? "link_creation_date=" + link_creation_date + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (error_msg_code != null ? "error_msg_code=" + error_msg_code + ", " : "") +
            "}";
    }

}
