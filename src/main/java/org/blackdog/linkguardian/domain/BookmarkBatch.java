package org.blackdog.linkguardian.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchStatus;

/**
 * A BookmarkBatch.
 */
@Entity
@Table(name = "bookmark_batch", indexes = {
    @Index(name = "bookmark_batch_idx_1", columnList = "status")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "bookmarkbatch")
public class BookmarkBatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="bookmark_batch_id_seq", sequenceName="bookmark_batch_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="bookmark_batch_id_seq")
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    @JsonProperty("creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "status_date")
    @JsonProperty("status_date")
    private ZonedDateTime statusDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookmarkBatchStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonManagedReference
    private User user;

    @OneToMany(
        mappedBy = "bookmarkBatch",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<BookmarkBatchItem> items = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public BookmarkBatch creation_date(ZonedDateTime creation_date) {
        this.creationDate = creation_date;
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ZonedDateTime getStatusDate() {
        return statusDate;
    }

    public BookmarkBatch status_date(ZonedDateTime status_date) {
        this.statusDate = status_date;
        return this;
    }

    public void setStatusDate(ZonedDateTime statusDate) {
        this.statusDate = statusDate;
    }

    public BookmarkBatchStatus getStatus() {
        return status;
    }

    public BookmarkBatch status(BookmarkBatchStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(BookmarkBatchStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public BookmarkBatch user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<BookmarkBatchItem> getItems() {
        return items;
    }

    public BookmarkBatch items(Set<BookmarkBatchItem> bookmarkBatchItems) {
        this.items = bookmarkBatchItems;
        return this;
    }

    public BookmarkBatch addItems(BookmarkBatchItem bookmarkBatchItem) {
        this.items.add(bookmarkBatchItem);
        bookmarkBatchItem.setBookmarkBatch(this);
        return this;
    }

    public BookmarkBatch removeItems(BookmarkBatchItem bookmarkBatchItem) {
        this.items.remove(bookmarkBatchItem);
        bookmarkBatchItem.setBookmarkBatch(null);
        return this;
    }

    public void setItems(Set<BookmarkBatchItem> bookmarkBatchItems) {
        this.items = bookmarkBatchItems;
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
        BookmarkBatch bookmarkBatch = (BookmarkBatch) o;
        if (bookmarkBatch.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bookmarkBatch.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BookmarkBatch{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", statusDate='" + getStatusDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
