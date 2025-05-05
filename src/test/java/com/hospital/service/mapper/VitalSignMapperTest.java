package com.hospital.service.mapper;

import static com.hospital.domain.VitalSignAsserts.*;
import static com.hospital.domain.VitalSignTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VitalSignMapperTest {

    private VitalSignMapper vitalSignMapper;

    @BeforeEach
    void setUp() {
        vitalSignMapper = new VitalSignMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVitalSignSample1();
        var actual = vitalSignMapper.toEntity(vitalSignMapper.toDto(expected));
        assertVitalSignAllPropertiesEquals(expected, actual);
    }
}
