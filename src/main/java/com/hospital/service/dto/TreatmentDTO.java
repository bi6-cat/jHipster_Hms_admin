package com.hospital.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.hospital.domain.Treatment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TreatmentDTO implements Serializable {

    private Long id;

    private String treatmentDescription;

    private LocalDate treatmentDate;

    private PatientDTO patient;

    private DoctorDTO doctor;

    private DiseaseDTO disease;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTreatmentDescription() {
        return treatmentDescription;
    }

    public void setTreatmentDescription(String treatmentDescription) {
        this.treatmentDescription = treatmentDescription;
    }

    public LocalDate getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(LocalDate treatmentDate) {
        this.treatmentDate = treatmentDate;
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

    public DiseaseDTO getDisease() {
        return disease;
    }

    public void setDisease(DiseaseDTO disease) {
        this.disease = disease;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TreatmentDTO)) {
            return false;
        }

        TreatmentDTO treatmentDTO = (TreatmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, treatmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TreatmentDTO{" +
            "id=" + getId() +
            ", treatmentDescription='" + getTreatmentDescription() + "'" +
            ", treatmentDate='" + getTreatmentDate() + "'" +
            ", patient=" + getPatient() +
            ", doctor=" + getDoctor() +
            ", disease=" + getDisease() +
            "}";
    }
}
