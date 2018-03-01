package org.blackdog.linkguardian.repository;

import org.blackdog.linkguardian.domain.Tag;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Tag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag>, TagRepositoryCustom {

    Tag findByLabel(String label);

}
