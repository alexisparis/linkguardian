package org.blackdog.linkguardian.batch;

import java.util.List;
import javafx.util.Pair;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.repository.BookmarkBatchItemRepository;
import org.blackdog.linkguardian.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BatchItemWriter implements ItemWriter<Pair<Link, BookmarkBatchItem>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchItemWriter.class);

    @Autowired
    BookmarkBatchItemRepository bookmarkItemRepository;

    @Autowired
    LinkRepository linkRepository;

    @Override
    public void write(List<? extends Pair<Link, BookmarkBatchItem>> pairs) throws Exception {
//        for (Pair<Link, BookmarkBatchItem> pair : pairs) {
//

//            if (pair.getKey() != null) {
//                // save link
//                System.out.println("@@ call " + this.getClass().getSimpleName() + " write");
//                System.out.println("   saving " + pair.getKey());
//                linkRepository.save(pair.getKey());
//            }
//
            // and update bookmark item
//            BookmarkBatchItem bookmarkItem = pair.getValue();
//            System.out.println("   saving " + bookmarkItem);
//            bookmarkItemRepository.save(bookmarkItem);
//        }
    }

}
