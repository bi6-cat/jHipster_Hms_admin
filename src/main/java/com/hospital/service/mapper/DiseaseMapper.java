package com.hospital.service.mapper;

import com.hospital.domain.Disease;
import com.hospital.domain.Patient;
import com.hospital.service.dto.DiseaseDTO;
import com.hospital.service.dto.PatientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Disease} and its DTO {@link DiseaseDTO}.
 */
@Mapper(componentModel = "spring")
public interface DiseaseMapper extends EntityMapper<DiseaseDTO, Disease> {
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientName")
    DiseaseDTO toDto(Disease s);

    @Named("patientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PatientDTO toDtoPatientName(Patient patient);
}
