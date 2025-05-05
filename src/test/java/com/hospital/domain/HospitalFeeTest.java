package com.hospital.domain;

import static com.hospital.domain.AppointmentTestSamples.*;
import static com.hospital.domain.HospitalFeeTestSamples.*;
import static com.hospital.domain.PatientTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hospital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HospitalFeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HospitalFee.class);
        HospitalFee hospitalFee1 = getHospitalFeeSample1();
        HospitalFee hospitalFee2 = new HospitalFee();
        assertThat(hospitalFee1).isNotEqualTo(hospitalFee2);

        hospitalFee2.setId(hospitalFee1.getId());
        assertThat(hospitalFee1).isEqualTo(hospitalFee2);

        hospitalFee2 = getHospitalFeeSample2();
        assertThat(hospitalFee1).isNotEqualTo(hospitalFee2);
    }

    @Test
    void appointmentTest() {
        HospitalFee hospitalFee = getHospitalFeeRandomSampleGenerator();
        Appointment appointmentBack = getAppointmentRandomSampleGenerator();

        hospitalFee.setAppointment(appointmentBack);
        assertThat(hospitalFee.getAppointment()).isEqualTo(appointmentBack);

        hospitalFee.appointment(null);
        assertThat(hospitalFee.getAppointment()).isNull();
    }

    @Test
    void patientTest() {
        HospitalFee hospitalFee = getHospitalFeeRandomSampleGenerator();
        Patient patientBack = getPatientRandomSampleGenerator();

        hospitalFee.setPatient(patientBack);
        assertThat(hospitalFee.getPatient()).isEqualTo(patientBack);

        hospitalFee.patient(null);
        assertThat(hospitalFee.getPatient()).isNull();
    }
}
