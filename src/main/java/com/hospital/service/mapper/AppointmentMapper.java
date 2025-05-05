package com.hospital.service.mapper;

import com.hospital.domain.Appointment;
import com.hospital.domain.Doctor;
import com.hospital.domain.Patient;
import com.hospital.service.dto.AppointmentDTO;
import com.hospital.service.dto.DoctorDTO;
import com.hospital.service.dto.PatientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Appointment} and its DTO {@link AppointmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper extends EntityMapper<AppointmentDTO, Appointment> {
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientName")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorName")
    AppointmentDTO toDto(Appointment s);

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
}
