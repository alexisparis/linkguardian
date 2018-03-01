package org.blackdog.linkguardian.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A ToxicLink.
 */
@Entity
@Table(name = "toxic_link", indexes = {
    @Index(name = "toxic_link_idx_1", columnList = "creation_date,email")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "toxiclink")
public class ToxicLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="toxic_link_id_seq", sequenceName="toxic_link_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="toxic_link_id_seq")
    private Long id;

    @NotNull
    @Size(max = 254)
    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @NotNull
    @Lob
    @Column(name = "url", nullable = false)
    private String url;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    @JsonProperty("creation_date")
    private ZonedDateTime creationDate;

    @Size(max = 30)
    @Column(name = "error", length = 30)
    private String error;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public ToxicLink email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public ToxicLink url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public ToxicLink creation_date(ZonedDateTime creation_date) {
        this.creationDate = creation_date;
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getError() {
        return error;
    }

    public ToxicLink error(String error) {
        this.error = error;
        return this;
    }

    public void setError(String error) {
        this.error = error;
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
        ToxicLink toxicLink = (ToxicLink) o;
        if (toxicLink.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), toxicLink.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ToxicLink{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", url='" + getUrl() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", error='" + getError() + "'" +
            "}";
    }
}
