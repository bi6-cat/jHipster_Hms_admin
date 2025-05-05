package com.hospital.domain;

import static com.hospital.domain.DiseaseTestSamples.*;
import static com.hospital.domain.DoctorTestSamples.*;
import static com.hospital.domain.PatientTestSamples.*;
import static com.hospital.domain.TreatmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hospital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TreatmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Treatment.class);
        Treatment treatment1 = getTreatmentSample1();
        Treatment treatment2 = new Treatment();
        assertThat(treatment1).isNotEqualTo(treatment2);

        treatment2.setId(treatment1.getId());
        assertThat(treatment1).isEqualTo(treatment2);

        treatment2 = getTreatmentSample2();
        assertThat(treatment1).isNotEqualTo(treatment2);
    }

    @Test
    void patientTest() {
        Treatment treatment = getTreatmentRandomSampleGenerator();
        Patient patientBack = getPatientRandomSampleGenerator();

        treatment.setPatient(patientBack);
        assertThat(treatment.getPatient()).isEqualTo(patientBack);

        treatment.patient(null);
        assertThat(treatment.getPatient()).isNull();
    }

    @Test
    void doctorTest() {
        Treatment treatment = getTreatmentRandomSampleGenerator();
        Doctor doctorBack = getDoctorRandomSampleGenerator();

        treatment.setDoctor(doctorBack);
        assertThat(treatment.getDoctor()).isEqualTo(doctorBack);

        treatment.doctor(null);
        assertThat(treatment.getDoctor()).isNull();
    }

    @Test
    void diseaseTest() {
        Treatment treatment = getTreatmentRandomSampleGenerator();
        Disease diseaseBack = getDiseaseRandomSampleGenerator();

        treatment.setDisease(diseaseBack);
        assertThat(treatment.getDisease()).isEqualTo(diseaseBack);

        treatment.disease(null);
        assertThat(treatment.getDisease()).isNull();
    }
}
