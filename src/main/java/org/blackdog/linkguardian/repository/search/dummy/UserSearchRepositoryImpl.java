package org.blackdog.linkguardian.repository.search.dummy;

import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.repository.search.UserSearchRepository;
import org.springframework.stereotype.Component;

@Component
public class UserSearchRepositoryImpl extends AbstractSearchRepositoryImpl<User> implements UserSearchRepository {

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }
}
