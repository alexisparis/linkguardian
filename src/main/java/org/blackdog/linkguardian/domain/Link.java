package org.blackdog.linkguardian.domain;

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

/**
 * A Link.
 */
@Entity
@Table(name = "link", indexes = {
    @Index(name = "link_idx_1", columnList = "title"),
    @Index(name = "link_idx_2", columnList = "url"),
    @Index(name = "link_idx_3", columnList = "original_url"),
    @Index(name = "link_idx_4", columnList = "creation_date"),
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "link")
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="link_id_seq", sequenceName="link_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="link_id_seq")
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    @JsonProperty("creation_date")
    private ZonedDateTime creationDate;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @NotNull
    @Size(max = 255)
    @Column(name = "domain", length = 255, nullable = false)
    private String domain;

    @Column(name = "locked")
    private Boolean locked;

    @Column(name = "note")
    private Integer note;

    @Column(name = "read")
    private Boolean read;

    @Size(max = 255)
    @Column(name = "title", length = 255)
    private String title;

    @NotNull
    @Size(max = 2400)
    @Column(name = "url", length = 2400, nullable = false)
    private String url;

    @NotNull
    @Size(max = 2400)
    @Column(name = "original_url", length = 2400, nullable = false)
    @JsonProperty("original_url")
    private String originalUrl;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "link_tag",
        joinColumns = @JoinColumn(name="links_id", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="tags_id", referencedColumnName="ID"))
    private Set<Tag> tags = new HashSet<>();

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

    public Link creation_date(ZonedDateTime creation_date) {
        this.creationDate = creation_date;
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public Link description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public Link domain(String domain) {
        this.domain = domain;
        return this;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean isLocked() {
        return locked;
    }

    public Link locked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Integer getNote() {
        return note;
    }

    public Link note(Integer note) {
        this.note = note;
        return this;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public Boolean isRead() {
        return read;
    }

    public Link read(Boolean read) {
        this.read = read;
        return this;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getTitle() {
        return title;
    }

    public Link title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public Link url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Link originalUrl(String original_url) {
        this.originalUrl = original_url;
        return this;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public User getUser() {
        return user;
    }

    public Link user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Link tags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
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
        Link link = (Link) o;
        if (link.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), link.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Link{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", domain='" + getDomain() + "'" +
            ", locked='" + isLocked() + "'" +
            ", note=" + getNote() +
            ", read='" + isRead() + "'" +
            ", title='" + getTitle() + "'" +
            ", url='" + getUrl() + "'" +
            ", originalUrl='" + getOriginalUrl() + "'" +
            "}";
    }
}
