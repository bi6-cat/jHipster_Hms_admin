package com.hospital.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HospitalFeeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static HospitalFee getHospitalFeeSample1() {
        return new HospitalFee().id(1L).serviceType("serviceType1").description("description1").phone("phone1");
    }

    public static HospitalFee getHospitalFeeSample2() {
        return new HospitalFee().id(2L).serviceType("serviceType2").description("description2").phone("phone2");
    }

    public static HospitalFee getHospitalFeeRandomSampleGenerator() {
        return new HospitalFee()
            .id(longCount.incrementAndGet())
            .serviceType(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
