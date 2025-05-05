package com.hospital.service.mapper;

import static com.hospital.domain.PrescriptionAsserts.*;
import static com.hospital.domain.PrescriptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrescriptionMapperTest {

    private PrescriptionMapper prescriptionMapper;

    @BeforeEach
    void setUp() {
        prescriptionMapper = new PrescriptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPrescriptionSample1();
        var actual = prescriptionMapper.toEntity(prescriptionMapper.toDto(expected));
        assertPrescriptionAllPropertiesEquals(expected, actual);
    }
}
