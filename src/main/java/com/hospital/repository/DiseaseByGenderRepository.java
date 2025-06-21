package com.hospital.repository;

import com.hospital.domain.DiseaseByGender;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiseaseByGenderRepository extends JpaRepository<DiseaseByGender, Long> {
    @Query("SELECT d FROM DiseaseByGender d ORDER BY d.diseaseName, d.gender")
    List<DiseaseByGender> findAllOrderByDiseaseNameAndGender();
}
