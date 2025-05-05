package com.hospital.service.mapper;

import static com.hospital.domain.HospitalFeeAsserts.*;
import static com.hospital.domain.HospitalFeeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HospitalFeeMapperTest {

    private HospitalFeeMapper hospitalFeeMapper;

    @BeforeEach
    void setUp() {
        hospitalFeeMapper = new HospitalFeeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHospitalFeeSample1();
        var actual = hospitalFeeMapper.toEntity(hospitalFeeMapper.toDto(expected));
        assertHospitalFeeAllPropertiesEquals(expected, actual);
    }
}
