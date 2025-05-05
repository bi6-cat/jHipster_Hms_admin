package com.hospital.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.hospital.domain.VitalSign} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VitalSignDTO implements Serializable {

    private Long id;

    private LocalDate measurementDate;

    private String bloodPressure;

    private Integer heartRate;

    private Integer respiratoryRate;

    private Double temperature;

    private Integer oxygenSaturation;

    private Integer bloodSugar;

    private PatientDTO patient;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(Integer oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public Integer getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(Integer bloodSugar) {
        this.bloodSugar = bloodSugar;
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
        if (!(o instanceof VitalSignDTO)) {
            return false;
        }

        VitalSignDTO vitalSignDTO = (VitalSignDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vitalSignDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VitalSignDTO{" +
            "id=" + getId() +
            ", measurementDate='" + getMeasurementDate() + "'" +
            ", bloodPressure='" + getBloodPressure() + "'" +
            ", heartRate=" + getHeartRate() +
            ", respiratoryRate=" + getRespiratoryRate() +
            ", temperature=" + getTemperature() +
            ", oxygenSaturation=" + getOxygenSaturation() +
            ", bloodSugar=" + getBloodSugar() +
            ", patient=" + getPatient() +
            "}";
    }
}
