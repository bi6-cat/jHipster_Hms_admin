package com.hospital.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VitalSignTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static VitalSign getVitalSignSample1() {
        return new VitalSign().id(1L).bloodPressure("bloodPressure1").heartRate(1).respiratoryRate(1).oxygenSaturation(1).bloodSugar(1);
    }

    public static VitalSign getVitalSignSample2() {
        return new VitalSign().id(2L).bloodPressure("bloodPressure2").heartRate(2).respiratoryRate(2).oxygenSaturation(2).bloodSugar(2);
    }

    public static VitalSign getVitalSignRandomSampleGenerator() {
        return new VitalSign()
            .id(longCount.incrementAndGet())
            .bloodPressure(UUID.randomUUID().toString())
            .heartRate(intCount.incrementAndGet())
            .respiratoryRate(intCount.incrementAndGet())
            .oxygenSaturation(intCount.incrementAndGet())
            .bloodSugar(intCount.incrementAndGet());
    }
}
