package com.hospital.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.hospital.domain.Disease} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DiseaseDTO implements Serializable {

    private Long id;

    private String diseaseName;

    private LocalDate diagnosisDate;

    private PatientDTO patient;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
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
        if (!(o instanceof DiseaseDTO)) {
            return false;
        }

        DiseaseDTO diseaseDTO = (DiseaseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, diseaseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DiseaseDTO{" +
            "id=" + getId() +
            ", diseaseName='" + getDiseaseName() + "'" +
            ", diagnosisDate='" + getDiagnosisDate() + "'" +
            ", patient=" + getPatient() +
            "}";
    }
}
