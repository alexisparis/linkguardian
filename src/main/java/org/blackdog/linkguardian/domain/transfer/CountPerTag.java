package org.blackdog.linkguardian.domain.transfer;

/**
 * Created by alexisparis on 22/04/16.
 */
public class CountPerTag {

    private final long tagId;

    private final String tag;

    private final int count;

    public CountPerTag(long tagId, String tag, int count) {
        this.tagId = tagId;
        this.tag = tag;
        this.count = count;
    }

    public long getTagId() {
        return tagId;
    }

    public String getTag() {
        return tag;
    }

    public int getCount() {
        return count;
    }
}
