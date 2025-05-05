package com.hospital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Prescription.
 */
@Entity
@Table(name = "prescription")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "medicine_name")
    private String medicineName;

    @Column(name = "form")
    private String form;

    @Column(name = "dosage_mg")
    private Integer dosageMg;

    @Column(name = "instruction")
    private String instruction;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "patient", "doctor" }, allowSetters = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Prescription id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return this.medicineName;
    }

    public Prescription medicineName(String medicineName) {
        this.setMedicineName(medicineName);
        return this;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getForm() {
        return this.form;
    }

    public Prescription form(String form) {
        this.setForm(form);
        return this;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Integer getDosageMg() {
        return this.dosageMg;
    }

    public Prescription dosageMg(Integer dosageMg) {
        this.setDosageMg(dosageMg);
        return this;
    }

    public void setDosageMg(Integer dosageMg) {
        this.dosageMg = dosageMg;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public Prescription instruction(String instruction) {
        this.setInstruction(instruction);
        return this;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Integer getDurationDays() {
        return this.durationDays;
    }

    public Prescription durationDays(Integer durationDays) {
        this.setDurationDays(durationDays);
        return this;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getNote() {
        return this.note;
    }

    public Prescription note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Appointment getAppointment() {
        return this.appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Prescription appointment(Appointment appointment) {
        this.setAppointment(appointment);
        return this;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Prescription doctor(Doctor doctor) {
        this.setDoctor(doctor);
        return this;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Prescription patient(Patient patient) {
        this.setPatient(patient);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Prescription)) {
            return false;
        }
        return getId() != null && getId().equals(((Prescription) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Prescription{" +
            "id=" + getId() +
            ", medicineName='" + getMedicineName() + "'" +
            ", form='" + getForm() + "'" +
            ", dosageMg=" + getDosageMg() +
            ", instruction='" + getInstruction() + "'" +
            ", durationDays=" + getDurationDays() +
            ", note='" + getNote() + "'" +
            "}";
    }
}
