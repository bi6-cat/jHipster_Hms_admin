package com.hospital.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A VitalSign.
 */
@Entity
@Table(name = "vital_sign")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VitalSign implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "measurement_date")
    private LocalDate measurementDate;

    @Column(name = "blood_pressure")
    private String bloodPressure;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "oxygen_saturation")
    private Integer oxygenSaturation;

    @Column(name = "blood_sugar")
    private Integer bloodSugar;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VitalSign id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getMeasurementDate() {
        return this.measurementDate;
    }

    public VitalSign measurementDate(LocalDate measurementDate) {
        this.setMeasurementDate(measurementDate);
        return this;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getBloodPressure() {
        return this.bloodPressure;
    }

    public VitalSign bloodPressure(String bloodPressure) {
        this.setBloodPressure(bloodPressure);
        return this;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getHeartRate() {
        return this.heartRate;
    }

    public VitalSign heartRate(Integer heartRate) {
        this.setHeartRate(heartRate);
        return this;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getRespiratoryRate() {
        return this.respiratoryRate;
    }

    public VitalSign respiratoryRate(Integer respiratoryRate) {
        this.setRespiratoryRate(respiratoryRate);
        return this;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public Double getTemperature() {
        return this.temperature;
    }

    public VitalSign temperature(Double temperature) {
        this.setTemperature(temperature);
        return this;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getOxygenSaturation() {
        return this.oxygenSaturation;
    }

    public VitalSign oxygenSaturation(Integer oxygenSaturation) {
        this.setOxygenSaturation(oxygenSaturation);
        return this;
    }

    public void setOxygenSaturation(Integer oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public Integer getBloodSugar() {
        return this.bloodSugar;
    }

    public VitalSign bloodSugar(Integer bloodSugar) {
        this.setBloodSugar(bloodSugar);
        return this;
    }

    public void setBloodSugar(Integer bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public VitalSign patient(Patient patient) {
        this.setPatient(patient);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VitalSign)) {
            return false;
        }
        return getId() != null && getId().equals(((VitalSign) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VitalSign{" +
            "id=" + getId() +
            ", measurementDate='" + getMeasurementDate() + "'" +
            ", bloodPressure='" + getBloodPressure() + "'" +
            ", heartRate=" + getHeartRate() +
            ", respiratoryRate=" + getRespiratoryRate() +
            ", temperature=" + getTemperature() +
            ", oxygenSaturation=" + getOxygenSaturation() +
            ", bloodSugar=" + getBloodSugar() +
            "}";
    }
}
