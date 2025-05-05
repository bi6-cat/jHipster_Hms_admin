package com.hospital.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PatientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Patient getPatientSample1() {
        return new Patient().id(1L).name("name1").gender("gender1").dob("dob1").address("address1").phone("phone1").email("email1");
    }

    public static Patient getPatientSample2() {
        return new Patient().id(2L).name("name2").gender("gender2").dob("dob2").address("address2").phone("phone2").email("email2");
    }

    public static Patient getPatientRandomSampleGenerator() {
        return new Patient()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .gender(UUID.randomUUID().toString())
            .dob(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString());
    }
}
