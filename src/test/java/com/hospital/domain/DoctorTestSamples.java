package com.hospital.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DoctorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Doctor getDoctorSample1() {
        return new Doctor().id(1L).name("name1").lastName("lastName1").specialization("specialization1").phone("phone1").email("email1");
    }

    public static Doctor getDoctorSample2() {
        return new Doctor().id(2L).name("name2").lastName("lastName2").specialization("specialization2").phone("phone2").email("email2");
    }

    public static Doctor getDoctorRandomSampleGenerator() {
        return new Doctor()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .specialization(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString());
    }
}
