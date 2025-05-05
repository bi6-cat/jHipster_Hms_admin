package com.hospital.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TreatmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Treatment getTreatmentSample1() {
        return new Treatment().id(1L).treatmentDescription("treatmentDescription1");
    }

    public static Treatment getTreatmentSample2() {
        return new Treatment().id(2L).treatmentDescription("treatmentDescription2");
    }

    public static Treatment getTreatmentRandomSampleGenerator() {
        return new Treatment().id(longCount.incrementAndGet()).treatmentDescription(UUID.randomUUID().toString());
    }
}
