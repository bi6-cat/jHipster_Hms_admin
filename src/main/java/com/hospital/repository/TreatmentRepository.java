package com.hospital.repository;

import com.hospital.domain.Treatment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Treatment entity.
 */
@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    default Optional<Treatment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Treatment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Treatment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select treatment from Treatment treatment left join fetch treatment.patient left join fetch treatment.doctor left join fetch treatment.disease",
        countQuery = "select count(treatment) from Treatment treatment"
    )
    Page<Treatment> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select treatment from Treatment treatment left join fetch treatment.patient left join fetch treatment.doctor left join fetch treatment.disease"
    )
    List<Treatment> findAllWithToOneRelationships();

    @Query(
        "select treatment from Treatment treatment left join fetch treatment.patient left join fetch treatment.doctor left join fetch treatment.disease where treatment.id =:id"
    )
    Optional<Treatment> findOneWithToOneRelationships(@Param("id") Long id);
}
