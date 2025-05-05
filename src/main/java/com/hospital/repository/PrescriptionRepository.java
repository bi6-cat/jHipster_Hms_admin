package com.hospital.repository;

import com.hospital.domain.Prescription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Prescription entity.
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    default Optional<Prescription> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Prescription> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Prescription> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select prescription from Prescription prescription left join fetch prescription.doctor left join fetch prescription.patient",
        countQuery = "select count(prescription) from Prescription prescription"
    )
    Page<Prescription> findAllWithToOneRelationships(Pageable pageable);

    @Query("select prescription from Prescription prescription left join fetch prescription.doctor left join fetch prescription.patient")
    List<Prescription> findAllWithToOneRelationships();

    @Query(
        "select prescription from Prescription prescription left join fetch prescription.doctor left join fetch prescription.patient where prescription.id =:id"
    )
    Optional<Prescription> findOneWithToOneRelationships(@Param("id") Long id);
}
