package com.hospital.service.mapper;

import static com.hospital.domain.DiseaseAsserts.*;
import static com.hospital.domain.DiseaseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiseaseMapperTest {

    private DiseaseMapper diseaseMapper;

    @BeforeEach
    void setUp() {
        diseaseMapper = new DiseaseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDiseaseSample1();
        var actual = diseaseMapper.toEntity(diseaseMapper.toDto(expected));
        assertDiseaseAllPropertiesEquals(expected, actual);
    }
}
