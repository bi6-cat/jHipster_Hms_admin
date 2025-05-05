package com.hospital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A HospitalFee.
 */
@Entity
@Table(name = "hospital_fee")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HospitalFee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "description")
    private String description;

    @Column(name = "amount", precision = 21, scale = 2)
    private BigDecimal amount;

    @Column(name = "fee_date")
    private LocalDate feeDate;

    @Column(name = "phone")
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "patient", "doctor" }, allowSetters = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HospitalFee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public HospitalFee serviceType(String serviceType) {
        this.setServiceType(serviceType);
        return this;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDescription() {
        return this.description;
    }

    public HospitalFee description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public HospitalFee amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getFeeDate() {
        return this.feeDate;
    }

    public HospitalFee feeDate(LocalDate feeDate) {
        this.setFeeDate(feeDate);
        return this;
    }

    public void setFeeDate(LocalDate feeDate) {
        this.feeDate = feeDate;
    }

    public String getPhone() {
        return this.phone;
    }

    public HospitalFee phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Appointment getAppointment() {
        return this.appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public HospitalFee appointment(Appointment appointment) {
        this.setAppointment(appointment);
        return this;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public HospitalFee patient(Patient patient) {
        this.setPatient(patient);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HospitalFee)) {
            return false;
        }
        return getId() != null && getId().equals(((HospitalFee) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HospitalFee{" +
            "id=" + getId() +
            ", serviceType='" + getServiceType() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", feeDate='" + getFeeDate() + "'" +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}
