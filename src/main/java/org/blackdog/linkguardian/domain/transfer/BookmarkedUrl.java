package org.blackdog.linkguardian.domain.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * represent an item in a bookmarks file
 */
public class BookmarkedUrl {

    @JsonProperty
    private String url;

    @JsonProperty
    private String title;

    @JsonProperty
    private String[] path;

    public BookmarkedUrl() {
    }

    public BookmarkedUrl(String[] path, String title, String url) {
        this.path = path;
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "BookmarkedUrl{" + "title='" + title + '\'' + ", path='" + path + '\'' + ", url='"
            + url + '\'' + '}';
    }
}
