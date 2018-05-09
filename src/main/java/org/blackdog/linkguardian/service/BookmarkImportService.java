package org.blackdog.linkguardian.service;

import com.google.common.base.Strings;
import java.util.HashSet;
import java.util.Set;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.BookmarkedUrl;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
import org.springframework.stereotype.Service;

@Service
public class BookmarkImportService {

    public void purgePath(BookmarkedUrl bookmark) {
        if (bookmark != null) {
            String[] path = bookmark.getPath();
            if (path != null) {
                Set<String> pathSet = new HashSet<>();
                for(int i = 0; i < path.length; i++) {
                    pathSet.add(path[i]);
                }

                pathSet.remove("Bookmarks Bar");

                bookmark.setPath(pathSet.toArray(new String[pathSet.size()]));
            }
        }
    }

    /**
     * convert a {@link BookmarkedUrl} into a {@link BookmarkBatchItem}
     * @param url a {@link BookmarkedUrl}
     * @return a {@link BookmarkBatchItem}
     */
    public BookmarkBatchItem convert(BookmarkedUrl url) {
        BookmarkBatchItem item = null;

        if (url != null) {
            item = new BookmarkBatchItem();

            item.setUrl(url.getUrl());
            item.setStatus(BookmarkBatchItemStatus.NOT_IMPORTED);
            String[] tags = url.getPath();
            if (tags != null && tags.length > 0) {

                StringBuilder builder = new StringBuilder();

                for(int i = 0; i < tags.length; i++) {
                    String currentTag = tags[i];
                    if (currentTag != null) {
                        currentTag = currentTag.trim();
                    }
                    currentTag = Strings.emptyToNull(currentTag);
                    if (currentTag != null) {
                        if (builder.length() > 0) {
                            builder.append(",");
                        }
                        builder.append(currentTag);
                    }
                }

                item.setTags(builder.toString());
            }
        }

        return item;
    }
}
