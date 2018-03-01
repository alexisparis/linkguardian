package org.blackdog.linkguardian.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;



import io.github.jhipster.service.filter.ZonedDateTimeFilter;


/**
 * Criteria class for the ToxicLink entity. This class is used in ToxicLinkResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /toxic-links?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ToxicLinkCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter email;

    private ZonedDateTimeFilter creation_date;

    private StringFilter error;

    public ToxicLinkCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getEmail() {
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public ZonedDateTimeFilter getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(ZonedDateTimeFilter creation_date) {
        this.creation_date = creation_date;
    }

    public StringFilter getError() {
        return error;
    }

    public void setError(StringFilter error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ToxicLinkCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (email != null ? "email=" + email + ", " : "") +
                (creation_date != null ? "creation_date=" + creation_date + ", " : "") +
                (error != null ? "error=" + error + ", " : "") +
            "}";
    }

}
