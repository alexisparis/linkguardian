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
 * Criteria class for the Link entity. This class is used in LinkResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /links?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LinkCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private ZonedDateTimeFilter creation_date;

    private StringFilter description;

    private StringFilter domain;

    private BooleanFilter locked;

    private IntegerFilter note;

    private BooleanFilter read;

    private StringFilter title;

    private StringFilter url;

    private StringFilter original_url;

    private LongFilter userId;

    private LongFilter tagsId;

    public LinkCriteria() {
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

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getDomain() {
        return domain;
    }

    public void setDomain(StringFilter domain) {
        this.domain = domain;
    }

    public BooleanFilter getLocked() {
        return locked;
    }

    public void setLocked(BooleanFilter locked) {
        this.locked = locked;
    }

    public IntegerFilter getNote() {
        return note;
    }

    public void setNote(IntegerFilter note) {
        this.note = note;
    }

    public BooleanFilter getRead() {
        return read;
    }

    public void setRead(BooleanFilter read) {
        this.read = read;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getUrl() {
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getOriginal_url() {
        return original_url;
    }

    public void setOriginal_url(StringFilter original_url) {
        this.original_url = original_url;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getTagsId() {
        return tagsId;
    }

    public void setTagsId(LongFilter tagsId) {
        this.tagsId = tagsId;
    }

    @Override
    public String toString() {
        return "LinkCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (creation_date != null ? "creation_date=" + creation_date + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (domain != null ? "domain=" + domain + ", " : "") +
                (locked != null ? "locked=" + locked + ", " : "") +
                (note != null ? "note=" + note + ", " : "") +
                (read != null ? "read=" + read + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (url != null ? "url=" + url + ", " : "") +
                (original_url != null ? "original_url=" + original_url + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (tagsId != null ? "tagsId=" + tagsId + ", " : "") +
            "}";
    }

}
