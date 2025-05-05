package com.hospital.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DiseaseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Disease getDiseaseSample1() {
        return new Disease().id(1L).diseaseName("diseaseName1");
    }

    public static Disease getDiseaseSample2() {
        return new Disease().id(2L).diseaseName("diseaseName2");
    }

    public static Disease getDiseaseRandomSampleGenerator() {
        return new Disease().id(longCount.incrementAndGet()).diseaseName(UUID.randomUUID().toString());
    }
}
