package com.hospital.repository;

import com.hospital.domain.HospitalFee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the HospitalFee entity.
 */
@Repository
public interface HospitalFeeRepository extends JpaRepository<HospitalFee, Long> {
    default Optional<HospitalFee> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<HospitalFee> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<HospitalFee> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select hospitalFee from HospitalFee hospitalFee left join fetch hospitalFee.patient",
        countQuery = "select count(hospitalFee) from HospitalFee hospitalFee"
    )
    Page<HospitalFee> findAllWithToOneRelationships(Pageable pageable);

    @Query("select hospitalFee from HospitalFee hospitalFee left join fetch hospitalFee.patient")
    List<HospitalFee> findAllWithToOneRelationships();

    @Query("select hospitalFee from HospitalFee hospitalFee left join fetch hospitalFee.patient where hospitalFee.id =:id")
    Optional<HospitalFee> findOneWithToOneRelationships(@Param("id") Long id);
}
