package org.blackdog.linkguardian.repository;

import org.blackdog.linkguardian.domain.*;
import org.blackdog.linkguardian.domain.transfer.CountPerTag;
import org.blackdog.linkguardian.domain.transfer.ReadStatus;
import org.blackdog.linkguardian.domain.transfer.SortDirection;
import org.blackdog.linkguardian.domain.transfer.SortType;
import org.blackdog.linkguardian.repository.exception.TooMuchTagException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by alexisparis on 23/03/16.
 */
public interface LinkRepositoryCustom {

    Page<Link> customFindLinksOfUser(String userLogin, String tag,
        ReadStatus status, SortType sortType,
        SortDirection direction, Pageable pageable) throws TooMuchTagException;

    List<CountPerTag> getCountPerTags(String userLogin);
}
