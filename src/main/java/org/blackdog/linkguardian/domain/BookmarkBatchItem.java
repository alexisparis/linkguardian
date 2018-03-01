package org.blackdog.linkguardian.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;

/**
 * A BookmarkBatchItem.
 */
@Entity
@Table(name = "bookmark_batch_item", indexes = {
    @Index(name = "bookmark_batch_item_idx_1", columnList = "status")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "bookmarkbatchitem")
public class BookmarkBatchItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="bookmark_batch_item_id_seq", sequenceName="bookmark_batch_item_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="bookmark_batch_item_id_seq")
    private Long id;

    @NotNull
    @Size(max = 2400)
    @Column(name = "url", length = 2400, nullable = false)
    private String url;

    @Size(max = 2400)
    @Column(name = "tags", length = 2400)
    private String tags;

    @Column(name = "link_creation_date")
    @JsonProperty("link_creation_date")
    private ZonedDateTime linkCreationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookmarkBatchItemStatus status;

    @Size(max = 100)
    @Column(name = "error_msg_code", length = 100)
    @JsonProperty("error_msg_code")
    private String errorMsgCode;

    @ManyToOne
    @JsonBackReference
    private BookmarkBatch bookmarkBatch;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public BookmarkBatchItem url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTags() {
        return tags;
    }

    public BookmarkBatchItem tags(String tags) {
        this.tags = tags;
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public ZonedDateTime getLinkCreationDate() {
        return linkCreationDate;
    }

    public BookmarkBatchItem link_creation_date(ZonedDateTime link_creation_date) {
        this.linkCreationDate = link_creation_date;
        return this;
    }

    public void setLinkCreationDate(ZonedDateTime linkCreationDate) {
        this.linkCreationDate = linkCreationDate;
    }

    public BookmarkBatchItemStatus getStatus() {
        return status;
    }

    public BookmarkBatchItem status(BookmarkBatchItemStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(BookmarkBatchItemStatus status) {
        this.status = status;
    }

    public String getErrorMsgCode() {
        return errorMsgCode;
    }

    public BookmarkBatchItem error_msg_code(String error_msg_code) {
        this.errorMsgCode = error_msg_code;
        return this;
    }

    public void setErrorMsgCode(String errorMsgCode) {
        this.errorMsgCode = errorMsgCode;
    }

    public BookmarkBatch getBookmarkBatch() {
        return bookmarkBatch;
    }

    public BookmarkBatchItem bookmarkBatch(BookmarkBatch bookmarkBatch) {
        this.bookmarkBatch = bookmarkBatch;
        return this;
    }

    public void setBookmarkBatch(BookmarkBatch bookmarkBatch) {
        this.bookmarkBatch = bookmarkBatch;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookmarkBatchItem bookmarkBatchItem = (BookmarkBatchItem) o;
        if (bookmarkBatchItem.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bookmarkBatchItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BookmarkBatchItem{" +
            "id=" + getId() +
            ", url='" + getUrl() + "'" +
            ", tags='" + getTags() + "'" +
            ", linkCreationDate='" + getLinkCreationDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", errorMsgCode='" + getErrorMsgCode() + "'" +
            "}";
    }
}
