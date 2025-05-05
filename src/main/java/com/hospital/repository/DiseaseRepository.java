package com.hospital.repository;

import com.hospital.domain.Disease;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Disease entity.
 */
@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    default Optional<Disease> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Disease> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Disease> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select disease from Disease disease left join fetch disease.patient",
        countQuery = "select count(disease) from Disease disease"
    )
    Page<Disease> findAllWithToOneRelationships(Pageable pageable);

    @Query("select disease from Disease disease left join fetch disease.patient")
    List<Disease> findAllWithToOneRelationships();

    @Query("select disease from Disease disease left join fetch disease.patient where disease.id =:id")
    Optional<Disease> findOneWithToOneRelationships(@Param("id") Long id);
}
