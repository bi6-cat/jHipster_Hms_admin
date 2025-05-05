package com.hospital.service.mapper;

import com.hospital.domain.Disease;
import com.hospital.domain.Doctor;
import com.hospital.domain.Patient;
import com.hospital.domain.Treatment;
import com.hospital.service.dto.DiseaseDTO;
import com.hospital.service.dto.DoctorDTO;
import com.hospital.service.dto.PatientDTO;
import com.hospital.service.dto.TreatmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Treatment} and its DTO {@link TreatmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TreatmentMapper extends EntityMapper<TreatmentDTO, Treatment> {
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientName")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorName")
    @Mapping(target = "disease", source = "disease", qualifiedByName = "diseaseDiseaseName")
    TreatmentDTO toDto(Treatment s);

    @Named("patientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PatientDTO toDtoPatientName(Patient patient);

    @Named("doctorName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DoctorDTO toDtoDoctorName(Doctor doctor);

    @Named("diseaseDiseaseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "diseaseName", source = "diseaseName")
    DiseaseDTO toDtoDiseaseDiseaseName(Disease disease);
}
