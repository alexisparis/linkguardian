package org.blackdog.linkguardian.service.template.impl;

import javafx.util.Pair;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
import org.blackdog.linkguardian.service.LinkResponse;
import org.blackdog.linkguardian.service.LinkService;
import org.blackdog.linkguardian.service.TargetDeterminationError;
import org.blackdog.linkguardian.service.exception.LinkException;
import org.blackdog.linkguardian.service.template.LinkTargetProcessorTemplateMethod;
import org.springframework.http.ResponseEntity;

/**
 * LinkTargetProcessorTemplateMethod used for the link generation based on a bookmark item
 */
public class BookmarkItemLinkTargetProcessorTemplateMethod extends
    LinkTargetProcessorTemplateMethod<Pair<BookmarkBatchItemStatus, String>> {

    boolean addManuallyWhenError = true;

    public BookmarkItemLinkTargetProcessorTemplateMethod(LinkService linkService) {
        super(linkService);
    }

    @Override
    protected void createToxicLink(User user, String url, String error) {
        if (!addManuallyWhenError) {
            super.createToxicLink(user, url, error);
        }
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

    private Pair<BookmarkBatchItemStatus, String> onError(CallContext context, BookmarkBatchItemStatus status, String message) {
        ResponseEntity<LinkResponse> response = null;
        if (addManuallyWhenError) {
            response = this.linkService.manuallyAddUrl(context.getUser(), context.getUrl(), context.getAlternativeDescription(), context.getTags());
            if (response != null) {
                context.setCreatedLink(response.getBody().getLink());
            }
        }
        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            return new Pair<>(BookmarkBatchItemStatus.MANUAL, message);
        } else {
            return new Pair<>(status, message);
        }
    }

    @Override
    protected Pair<BookmarkBatchItemStatus, String> onUnknownHostException(CallContext context) {
        return onError(context, BookmarkBatchItemStatus.FAILED, "unknownHost");
    }

    @Override
    protected Pair<BookmarkBatchItemStatus, String> onLinkException(CallContext context, LinkException e) {
        return onError(context, BookmarkBatchItemStatus.FAILED, e.getMessage());
    }

    @Override
    protected Pair<BookmarkBatchItemStatus, String> onTargetDeterminationError(CallContext context, TargetDeterminationError error) {
        return onError(context, BookmarkBatchItemStatus.FAILED, error.name());
    }
}
