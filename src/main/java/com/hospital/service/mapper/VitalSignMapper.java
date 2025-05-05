package com.hospital.service.mapper;

import com.hospital.domain.Patient;
import com.hospital.domain.VitalSign;
import com.hospital.service.dto.PatientDTO;
import com.hospital.service.dto.VitalSignDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VitalSign} and its DTO {@link VitalSignDTO}.
 */
@Mapper(componentModel = "spring")
public interface VitalSignMapper extends EntityMapper<VitalSignDTO, VitalSign> {
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientName")
    VitalSignDTO toDto(VitalSign s);

    @Named("patientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PatientDTO toDtoPatientName(Patient patient);
}
