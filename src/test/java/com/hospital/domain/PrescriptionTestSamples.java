package com.hospital.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PrescriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Prescription getPrescriptionSample1() {
        return new Prescription()
            .id(1L)
            .medicineName("medicineName1")
            .form("form1")
            .dosageMg(1)
            .instruction("instruction1")
            .durationDays(1)
            .note("note1");
    }

    public static Prescription getPrescriptionSample2() {
        return new Prescription()
            .id(2L)
            .medicineName("medicineName2")
            .form("form2")
            .dosageMg(2)
            .instruction("instruction2")
            .durationDays(2)
            .note("note2");
    }

    public static Prescription getPrescriptionRandomSampleGenerator() {
        return new Prescription()
            .id(longCount.incrementAndGet())
            .medicineName(UUID.randomUUID().toString())
            .form(UUID.randomUUID().toString())
            .dosageMg(intCount.incrementAndGet())
            .instruction(UUID.randomUUID().toString())
            .durationDays(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString());
    }
}
