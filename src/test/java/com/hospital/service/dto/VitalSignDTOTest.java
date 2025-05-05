package com.hospital.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hospital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VitalSignDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VitalSignDTO.class);
        VitalSignDTO vitalSignDTO1 = new VitalSignDTO();
        vitalSignDTO1.setId(1L);
        VitalSignDTO vitalSignDTO2 = new VitalSignDTO();
        assertThat(vitalSignDTO1).isNotEqualTo(vitalSignDTO2);
        vitalSignDTO2.setId(vitalSignDTO1.getId());
        assertThat(vitalSignDTO1).isEqualTo(vitalSignDTO2);
        vitalSignDTO2.setId(2L);
        assertThat(vitalSignDTO1).isNotEqualTo(vitalSignDTO2);
        vitalSignDTO1.setId(null);
        assertThat(vitalSignDTO1).isNotEqualTo(vitalSignDTO2);
    }
}
