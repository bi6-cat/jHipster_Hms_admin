package com.hospital.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.hospital.domain.Prescription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PrescriptionDTO implements Serializable {

    private Long id;

    private String medicineName;

    private String form;

    private Integer dosageMg;

    private String instruction;

    private Integer durationDays;

    private String note;

    private AppointmentDTO appointment;

    private DoctorDTO doctor;

    private PatientDTO patient;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Integer getDosageMg() {
        return dosageMg;
    }

    public void setDosageMg(Integer dosageMg) {
        this.dosageMg = dosageMg;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AppointmentDTO getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentDTO appointment) {
        this.appointment = appointment;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrescriptionDTO)) {
            return false;
        }

        PrescriptionDTO prescriptionDTO = (PrescriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, prescriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrescriptionDTO{" +
            "id=" + getId() +
            ", medicineName='" + getMedicineName() + "'" +
            ", form='" + getForm() + "'" +
            ", dosageMg=" + getDosageMg() +
            ", instruction='" + getInstruction() + "'" +
            ", durationDays=" + getDurationDays() +
            ", note='" + getNote() + "'" +
            ", appointment=" + getAppointment() +
            ", doctor=" + getDoctor() +
            ", patient=" + getPatient() +
            "}";
    }
}
