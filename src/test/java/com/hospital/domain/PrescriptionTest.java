package com.hospital.domain;

import static com.hospital.domain.AppointmentTestSamples.*;
import static com.hospital.domain.DoctorTestSamples.*;
import static com.hospital.domain.PatientTestSamples.*;
import static com.hospital.domain.PrescriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hospital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PrescriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Prescription.class);
        Prescription prescription1 = getPrescriptionSample1();
        Prescription prescription2 = new Prescription();
        assertThat(prescription1).isNotEqualTo(prescription2);

        prescription2.setId(prescription1.getId());
        assertThat(prescription1).isEqualTo(prescription2);

        prescription2 = getPrescriptionSample2();
        assertThat(prescription1).isNotEqualTo(prescription2);
    }

    @Test
    void appointmentTest() {
        Prescription prescription = getPrescriptionRandomSampleGenerator();
        Appointment appointmentBack = getAppointmentRandomSampleGenerator();

        prescription.setAppointment(appointmentBack);
        assertThat(prescription.getAppointment()).isEqualTo(appointmentBack);

        prescription.appointment(null);
        assertThat(prescription.getAppointment()).isNull();
    }

    @Test
    void doctorTest() {
        Prescription prescription = getPrescriptionRandomSampleGenerator();
        Doctor doctorBack = getDoctorRandomSampleGenerator();

        prescription.setDoctor(doctorBack);
        assertThat(prescription.getDoctor()).isEqualTo(doctorBack);

        prescription.doctor(null);
        assertThat(prescription.getDoctor()).isNull();
    }

    @Test
    void patientTest() {
        Prescription prescription = getPrescriptionRandomSampleGenerator();
        Patient patientBack = getPatientRandomSampleGenerator();

        prescription.setPatient(patientBack);
        assertThat(prescription.getPatient()).isEqualTo(patientBack);

        prescription.patient(null);
        assertThat(prescription.getPatient()).isNull();
    }
}
