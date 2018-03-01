package org.blackdog.linkguardian.service.template.impl;

import javafx.util.Pair;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
import org.blackdog.linkguardian.service.LinkService;
import org.blackdog.linkguardian.service.TargetDeterminationError;
import org.blackdog.linkguardian.service.exception.LinkException;
import org.blackdog.linkguardian.service.template.LinkTargetProcessorTemplateMethod;

/**
 * LinkTargetProcessorTemplateMethod used for the link generation based on a bookmark item
 */
public class BookmarkItemLinkTargetProcessorTemplateMethod extends
    LinkTargetProcessorTemplateMethod<Pair<BookmarkBatchItemStatus, String>> {

    public BookmarkItemLinkTargetProcessorTemplateMethod(LinkService linkService) {
        super(linkService);
    }

    @Override
    protected Pair<BookmarkBatchItemStatus, String> onLinkAlreadyExist(CallContext ctx, Link link) {
        return new Pair<>(BookmarkBatchItemStatus.CREATED, "already exists");
    }

    @Override
    protected Pair<BookmarkBatchItemStatus, String> onLinkCreated(CallContext context,
        Link newLink) {
        if (context.getTarget().isClientError() || context.getTarget().isServerError()) {
            return new Pair<>(BookmarkBatchItemStatus.CREATED, "ok but inaccessible");
        } else {
            return new Pair<>(BookmarkBatchItemStatus.CREATED, "ok");
        }
    }

    @Override
    protected Pair<BookmarkBatchItemStatus, String> onUnknownHostException(CallContext context) {
        return new Pair<>(BookmarkBatchItemStatus.FAILED, "unknownHost");
    }

    @Override
    protected Pair<BookmarkBatchItemStatus, String> onLinkException(CallContext context, LinkException e) {
        return new Pair<>(BookmarkBatchItemStatus.FAILED, e.getMessage());
    }

    @Override
    protected Pair<BookmarkBatchItemStatus, String> onTargetDeterminationError(CallContext context, TargetDeterminationError error) {
        return new Pair<>(BookmarkBatchItemStatus.FAILED, error.name());
    }
}
