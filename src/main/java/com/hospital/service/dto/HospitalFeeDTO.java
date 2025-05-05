package com.hospital.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.hospital.domain.HospitalFee} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HospitalFeeDTO implements Serializable {

    private Long id;

    private String serviceType;

    private String description;

    private BigDecimal amount;

    private LocalDate feeDate;

    private String phone;

    private AppointmentDTO appointment;

    private PatientDTO patient;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getFeeDate() {
        return feeDate;
    }

    public void setFeeDate(LocalDate feeDate) {
        this.feeDate = feeDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AppointmentDTO getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentDTO appointment) {
        this.appointment = appointment;
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
        if (!(o instanceof HospitalFeeDTO)) {
            return false;
        }

        HospitalFeeDTO hospitalFeeDTO = (HospitalFeeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, hospitalFeeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HospitalFeeDTO{" +
            "id=" + getId() +
            ", serviceType='" + getServiceType() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", feeDate='" + getFeeDate() + "'" +
            ", phone='" + getPhone() + "'" +
            ", appointment=" + getAppointment() +
            ", patient=" + getPatient() +
            "}";
    }
}
