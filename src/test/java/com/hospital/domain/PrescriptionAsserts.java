package com.hospital.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class PrescriptionAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPrescriptionAllPropertiesEquals(Prescription expected, Prescription actual) {
        assertPrescriptionAutoGeneratedPropertiesEquals(expected, actual);
        assertPrescriptionAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPrescriptionAllUpdatablePropertiesEquals(Prescription expected, Prescription actual) {
        assertPrescriptionUpdatableFieldsEquals(expected, actual);
        assertPrescriptionUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPrescriptionAutoGeneratedPropertiesEquals(Prescription expected, Prescription actual) {
        assertThat(actual)
            .as("Verify Prescription auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPrescriptionUpdatableFieldsEquals(Prescription expected, Prescription actual) {
        assertThat(actual)
            .as("Verify Prescription relevant properties")
            .satisfies(a -> assertThat(a.getMedicineName()).as("check medicineName").isEqualTo(expected.getMedicineName()))
            .satisfies(a -> assertThat(a.getForm()).as("check form").isEqualTo(expected.getForm()))
            .satisfies(a -> assertThat(a.getDosageMg()).as("check dosageMg").isEqualTo(expected.getDosageMg()))
            .satisfies(a -> assertThat(a.getInstruction()).as("check instruction").isEqualTo(expected.getInstruction()))
            .satisfies(a -> assertThat(a.getDurationDays()).as("check durationDays").isEqualTo(expected.getDurationDays()))
            .satisfies(a -> assertThat(a.getNote()).as("check note").isEqualTo(expected.getNote()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPrescriptionUpdatableRelationshipsEquals(Prescription expected, Prescription actual) {
        assertThat(actual)
            .as("Verify Prescription relationships")
            .satisfies(a -> assertThat(a.getAppointment()).as("check appointment").isEqualTo(expected.getAppointment()))
            .satisfies(a -> assertThat(a.getDoctor()).as("check doctor").isEqualTo(expected.getDoctor()))
            .satisfies(a -> assertThat(a.getPatient()).as("check patient").isEqualTo(expected.getPatient()));
    }
}
