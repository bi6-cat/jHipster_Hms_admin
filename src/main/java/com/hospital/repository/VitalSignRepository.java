package com.hospital.repository;

import com.hospital.domain.VitalSign;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VitalSign entity.
 */
@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    default Optional<VitalSign> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<VitalSign> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<VitalSign> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select vitalSign from VitalSign vitalSign left join fetch vitalSign.patient",
        countQuery = "select count(vitalSign) from VitalSign vitalSign"
    )
    Page<VitalSign> findAllWithToOneRelationships(Pageable pageable);

    @Query("select vitalSign from VitalSign vitalSign left join fetch vitalSign.patient")
    List<VitalSign> findAllWithToOneRelationships();

    @Query("select vitalSign from VitalSign vitalSign left join fetch vitalSign.patient where vitalSign.id =:id")
    Optional<VitalSign> findOneWithToOneRelationships(@Param("id") Long id);
}
