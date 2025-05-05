package com.hospital.domain;

import static com.hospital.domain.PatientTestSamples.*;
import static com.hospital.domain.VitalSignTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hospital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VitalSignTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VitalSign.class);
        VitalSign vitalSign1 = getVitalSignSample1();
        VitalSign vitalSign2 = new VitalSign();
        assertThat(vitalSign1).isNotEqualTo(vitalSign2);

        vitalSign2.setId(vitalSign1.getId());
        assertThat(vitalSign1).isEqualTo(vitalSign2);

        vitalSign2 = getVitalSignSample2();
        assertThat(vitalSign1).isNotEqualTo(vitalSign2);
    }

    @Test
    void patientTest() {
        VitalSign vitalSign = getVitalSignRandomSampleGenerator();
        Patient patientBack = getPatientRandomSampleGenerator();

        vitalSign.setPatient(patientBack);
        assertThat(vitalSign.getPatient()).isEqualTo(patientBack);

        vitalSign.patient(null);
        assertThat(vitalSign.getPatient()).isNull();
    }
}
