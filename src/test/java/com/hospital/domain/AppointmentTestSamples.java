package com.hospital.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppointmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Appointment getAppointmentSample1() {
        return new Appointment()
            .id(1L)
            .reason("reason1")
            .status("status1")
            .phone("phone1")
            .location("location1")
            .appointmentType("appointmentType1");
    }

    public static Appointment getAppointmentSample2() {
        return new Appointment()
            .id(2L)
            .reason("reason2")
            .status("status2")
            .phone("phone2")
            .location("location2")
            .appointmentType("appointmentType2");
    }

    public static Appointment getAppointmentRandomSampleGenerator() {
        return new Appointment()
            .id(longCount.incrementAndGet())
            .reason(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString())
            .appointmentType(UUID.randomUUID().toString());
    }
}
