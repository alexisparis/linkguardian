package org.blackdog.linkguardian.web.rest.vm;

import org.blackdog.linkguardian.domain.BookmarkBatch;

public class BookmarkBatchResponse {

    private final BookmarkBatch batch;

    private final String messageCode;

    public BookmarkBatchResponse(BookmarkBatch batch, String messageCode) {
        this.batch = batch;
        this.messageCode = messageCode;
    }

    public BookmarkBatch getBookmarkBatch() {
        return batch;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public static BookmarkBatchResponse of(BookmarkBatch batch, String msgCode) {
        return new BookmarkBatchResponse(batch, msgCode);
    }

    public static BookmarkBatchResponse of(String msgCode, BookmarkBatch batch) {
        return new BookmarkBatchResponse(batch, msgCode);
    }

    public static BookmarkBatchResponse of(String msgCode) {
        return new BookmarkBatchResponse(null, msgCode);
    }

    public static BookmarkBatchResponse of(BookmarkBatch batch) {
        return new BookmarkBatchResponse(batch, null);
    }

    public static BookmarkBatchResponse empty() {
        return new BookmarkBatchResponse(null, null);
    }
}
