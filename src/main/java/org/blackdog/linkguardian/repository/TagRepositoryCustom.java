package org.blackdog.linkguardian.repository;

import java.util.List;

/**
 * Created by alexisparis on 23/03/16.
 */
public interface TagRepositoryCustom {

    List<String> getTags(Number userId, String filter);
}
