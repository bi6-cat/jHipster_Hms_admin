package com.hospital.domain;

import static com.hospital.domain.DiseaseTestSamples.*;
import static com.hospital.domain.PatientTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hospital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiseaseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Disease.class);
        Disease disease1 = getDiseaseSample1();
        Disease disease2 = new Disease();
        assertThat(disease1).isNotEqualTo(disease2);

        disease2.setId(disease1.getId());
        assertThat(disease1).isEqualTo(disease2);

        disease2 = getDiseaseSample2();
        assertThat(disease1).isNotEqualTo(disease2);
    }

    @Test
    void patientTest() {
        Disease disease = getDiseaseRandomSampleGenerator();
        Patient patientBack = getPatientRandomSampleGenerator();

        disease.setPatient(patientBack);
        assertThat(disease.getPatient()).isEqualTo(patientBack);

        disease.patient(null);
        assertThat(disease.getPatient()).isNull();
    }
}
