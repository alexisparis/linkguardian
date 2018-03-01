package org.blackdog.linkguardian.batch;

import java.time.ZonedDateTime;
import java.util.Set;
import javafx.util.Pair;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
import org.blackdog.linkguardian.service.LinkBuilder;
import org.blackdog.linkguardian.service.LinkService;
import org.blackdog.linkguardian.service.LinkTarget;
import org.blackdog.linkguardian.service.template.LinkTargetProcessorTemplateMethod;
import org.blackdog.linkguardian.service.template.impl.BookmarkItemLinkTargetProcessorTemplateMethod;
import org.blackdog.linkguardian.service.util.TagsNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BatchItemProcessor implements ItemProcessor<BookmarkBatchItem, Pair<Link, BookmarkBatchItem>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchItemProcessor.class);

    @Autowired
    private LinkService linkService;

    @Autowired
    private LinkBuilder builder;

    @Autowired
    private TagsNormalizer tagsNormalizer;

    @Override
    public Pair<Link, BookmarkBatchItem> process(BookmarkBatchItem data) throws Exception {

        LOGGER.info("@@ call " + this.getClass().getSimpleName() + " process");
        // determine target
        LinkTarget target = null;
        target = linkService.determineTarget(data.getUrl());

        // instantiate the target processor
        LinkTargetProcessorTemplateMethod<Pair<BookmarkBatchItemStatus, String>> templateMethod =
            new BookmarkItemLinkTargetProcessorTemplateMethod(this.linkService);

        // build context of processing
        // normalized after
        Set<String> tags = data.getTags() == null ? null :  this.tagsNormalizer.split(data.getTags(), ",", false);
        LOGGER.info("considering tags : " + tags);
        LinkTargetProcessorTemplateMethod.CallContext ctx =
            LinkTargetProcessorTemplateMethod.CallContext
                .newInstance(target, data.getBookmarkBatch().getUser(), data.getUrl(),
                    tags);
        Pair<BookmarkBatchItemStatus, String> result = templateMethod.process(ctx);

        // update bookmark item with the process result
        data.setStatus(result.getKey());
        data.setErrorMsgCode(result.getValue());
        if (ctx.getCreatedLink() != null) {
            data.setLinkCreationDate(ZonedDateTime.now());
        }

        if (ctx.getCreatedLink() == null)
        {
            data.setStatus(BookmarkBatchItemStatus.FAILED);
        }
        return new Pair<>(ctx.getCreatedLink(), data);
    }
}
