package com.hospital.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Disease.
 */
@Entity
@Table(name = "disease")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Disease implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "disease_name", columnDefinition = "NVARCHAR(255)")
    private String diseaseName;

    @Column(name = "diagnosis_date")
    private LocalDate diagnosisDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Disease id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiseaseName() {
        return this.diseaseName;
    }

    public Disease diseaseName(String diseaseName) {
        this.setDiseaseName(diseaseName);
        return this;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public LocalDate getDiagnosisDate() {
        return this.diagnosisDate;
    }

    public Disease diagnosisDate(LocalDate diagnosisDate) {
        this.setDiagnosisDate(diagnosisDate);
        return this;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Disease patient(Patient patient) {
        this.setPatient(patient);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Disease)) {
            return false;
        }
        return getId() != null && getId().equals(((Disease) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Disease{" +
            "id=" + getId() +
            ", diseaseName='" + getDiseaseName() + "'" +
            ", diagnosisDate='" + getDiagnosisDate() + "'" +
            "}";
    }
}
