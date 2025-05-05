package com.hospital.service.mapper;

import static com.hospital.domain.TreatmentAsserts.*;
import static com.hospital.domain.TreatmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TreatmentMapperTest {

    private TreatmentMapper treatmentMapper;

    @BeforeEach
    void setUp() {
        treatmentMapper = new TreatmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTreatmentSample1();
        var actual = treatmentMapper.toEntity(treatmentMapper.toDto(expected));
        assertTreatmentAllPropertiesEquals(expected, actual);
    }
}
