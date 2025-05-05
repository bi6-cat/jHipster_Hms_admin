package com.hospital.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.hospital.domain.Appointment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppointmentDTO implements Serializable {

    private Long id;

    private Instant appointmentDate;

    private Instant startTime;

    private Instant endTime;

    private String reason;

    private String status;

    private String phone;

    private String location;

    private String appointmentType;

    private PatientDTO patient;

    private DoctorDTO doctor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Instant appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppointmentDTO)) {
            return false;
        }

        AppointmentDTO appointmentDTO = (AppointmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appointmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppointmentDTO{" +
            "id=" + getId() +
            ", appointmentDate='" + getAppointmentDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", phone='" + getPhone() + "'" +
            ", location='" + getLocation() + "'" +
            ", appointmentType='" + getAppointmentType() + "'" +
            ", patient=" + getPatient() +
            ", doctor=" + getDoctor() +
            "}";
    }
}
