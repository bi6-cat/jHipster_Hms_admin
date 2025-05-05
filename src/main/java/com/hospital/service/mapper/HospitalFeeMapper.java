package com.hospital.service.mapper;

import com.hospital.domain.Appointment;
import com.hospital.domain.HospitalFee;
import com.hospital.domain.Patient;
import com.hospital.service.dto.AppointmentDTO;
import com.hospital.service.dto.HospitalFeeDTO;
import com.hospital.service.dto.PatientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link HospitalFee} and its DTO {@link HospitalFeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface HospitalFeeMapper extends EntityMapper<HospitalFeeDTO, HospitalFee> {
    @Mapping(target = "appointment", source = "appointment", qualifiedByName = "appointmentId")
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientName")
    HospitalFeeDTO toDto(HospitalFee s);

    @Named("appointmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppointmentDTO toDtoAppointmentId(Appointment appointment);

    @Named("patientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PatientDTO toDtoPatientName(Patient patient);
}
