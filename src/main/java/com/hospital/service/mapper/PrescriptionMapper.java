package com.hospital.service.mapper;

import com.hospital.domain.Appointment;
import com.hospital.domain.Doctor;
import com.hospital.domain.Patient;
import com.hospital.domain.Prescription;
import com.hospital.service.dto.AppointmentDTO;
import com.hospital.service.dto.DoctorDTO;
import com.hospital.service.dto.PatientDTO;
import com.hospital.service.dto.PrescriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Prescription} and its DTO {@link PrescriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PrescriptionMapper extends EntityMapper<PrescriptionDTO, Prescription> {
    @Mapping(target = "appointment", source = "appointment", qualifiedByName = "appointmentId")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorName")
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientName")
    PrescriptionDTO toDto(Prescription s);

    @Named("appointmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppointmentDTO toDtoAppointmentId(Appointment appointment);

    @Named("doctorName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DoctorDTO toDtoDoctorName(Doctor doctor);

    @Named("patientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PatientDTO toDtoPatientName(Patient patient);
}
