package com.hospital.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hospital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HospitalFeeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HospitalFeeDTO.class);
        HospitalFeeDTO hospitalFeeDTO1 = new HospitalFeeDTO();
        hospitalFeeDTO1.setId(1L);
        HospitalFeeDTO hospitalFeeDTO2 = new HospitalFeeDTO();
        assertThat(hospitalFeeDTO1).isNotEqualTo(hospitalFeeDTO2);
        hospitalFeeDTO2.setId(hospitalFeeDTO1.getId());
        assertThat(hospitalFeeDTO1).isEqualTo(hospitalFeeDTO2);
        hospitalFeeDTO2.setId(2L);
        assertThat(hospitalFeeDTO1).isNotEqualTo(hospitalFeeDTO2);
        hospitalFeeDTO1.setId(null);
        assertThat(hospitalFeeDTO1).isNotEqualTo(hospitalFeeDTO2);
    }
}
