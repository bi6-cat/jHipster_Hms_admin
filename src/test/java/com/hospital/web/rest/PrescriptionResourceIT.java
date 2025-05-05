package com.hospital.web.rest;

import static com.hospital.domain.PrescriptionAsserts.*;
import static com.hospital.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.IntegrationTest;
import com.hospital.domain.Prescription;
import com.hospital.repository.PrescriptionRepository;
import com.hospital.service.PrescriptionService;
import com.hospital.service.dto.PrescriptionDTO;
import com.hospital.service.mapper.PrescriptionMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PrescriptionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PrescriptionResourceIT {

    private static final String DEFAULT_MEDICINE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MEDICINE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FORM = "AAAAAAAAAA";
    private static final String UPDATED_FORM = "BBBBBBBBBB";

    private static final Integer DEFAULT_DOSAGE_MG = 1;
    private static final Integer UPDATED_DOSAGE_MG = 2;

    private static final String DEFAULT_INSTRUCTION = "AAAAAAAAAA";
    private static final String UPDATED_INSTRUCTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION_DAYS = 1;
    private static final Integer UPDATED_DURATION_DAYS = 2;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/prescriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PrescriptionRepository prescriptionRepositoryMock;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Mock
    private PrescriptionService prescriptionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrescriptionMockMvc;

    private Prescription prescription;

    private Prescription insertedPrescription;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prescription createEntity() {
        return new Prescription()
            .medicineName(DEFAULT_MEDICINE_NAME)
            .form(DEFAULT_FORM)
            .dosageMg(DEFAULT_DOSAGE_MG)
            .instruction(DEFAULT_INSTRUCTION)
            .durationDays(DEFAULT_DURATION_DAYS)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prescription createUpdatedEntity() {
        return new Prescription()
            .medicineName(UPDATED_MEDICINE_NAME)
            .form(UPDATED_FORM)
            .dosageMg(UPDATED_DOSAGE_MG)
            .instruction(UPDATED_INSTRUCTION)
            .durationDays(UPDATED_DURATION_DAYS)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    void initTest() {
        prescription = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPrescription != null) {
            prescriptionRepository.delete(insertedPrescription);
            insertedPrescription = null;
        }
    }

    @Test
    @Transactional
    void createPrescription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Prescription
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(prescription);
        var returnedPrescriptionDTO = om.readValue(
            restPrescriptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(prescriptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PrescriptionDTO.class
        );

        // Validate the Prescription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPrescription = prescriptionMapper.toEntity(returnedPrescriptionDTO);
        assertPrescriptionUpdatableFieldsEquals(returnedPrescription, getPersistedPrescription(returnedPrescription));

        insertedPrescription = returnedPrescription;
    }

    @Test
    @Transactional
    void createPrescriptionWithExistingId() throws Exception {
        // Create the Prescription with an existing ID
        prescription.setId(1L);
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(prescription);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrescriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(prescriptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Prescription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPrescriptions() throws Exception {
        // Initialize the database
        insertedPrescription = prescriptionRepository.saveAndFlush(prescription);

        // Get all the prescriptionList
        restPrescriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prescription.getId().intValue())))
            .andExpect(jsonPath("$.[*].medicineName").value(hasItem(DEFAULT_MEDICINE_NAME)))
            .andExpect(jsonPath("$.[*].form").value(hasItem(DEFAULT_FORM)))
            .andExpect(jsonPath("$.[*].dosageMg").value(hasItem(DEFAULT_DOSAGE_MG)))
            .andExpect(jsonPath("$.[*].instruction").value(hasItem(DEFAULT_INSTRUCTION)))
            .andExpect(jsonPath("$.[*].durationDays").value(hasItem(DEFAULT_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPrescriptionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(prescriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPrescriptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(prescriptionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPrescriptionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(prescriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPrescriptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(prescriptionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPrescription() throws Exception {
        // Initialize the database
        insertedPrescription = prescriptionRepository.saveAndFlush(prescription);

        // Get the prescription
        restPrescriptionMockMvc
            .perform(get(ENTITY_API_URL_ID, prescription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prescription.getId().intValue()))
            .andExpect(jsonPath("$.medicineName").value(DEFAULT_MEDICINE_NAME))
            .andExpect(jsonPath("$.form").value(DEFAULT_FORM))
            .andExpect(jsonPath("$.dosageMg").value(DEFAULT_DOSAGE_MG))
            .andExpect(jsonPath("$.instruction").value(DEFAULT_INSTRUCTION))
            .andExpect(jsonPath("$.durationDays").value(DEFAULT_DURATION_DAYS))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingPrescription() throws Exception {
        // Get the prescription
        restPrescriptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPrescription() throws Exception {
        // Initialize the database
        insertedPrescription = prescriptionRepository.saveAndFlush(prescription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the prescription
        Prescription updatedPrescription = prescriptionRepository.findById(prescription.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPrescription are not directly saved in db
        em.detach(updatedPrescription);
        updatedPrescription
            .medicineName(UPDATED_MEDICINE_NAME)
            .form(UPDATED_FORM)
            .dosageMg(UPDATED_DOSAGE_MG)
            .instruction(UPDATED_INSTRUCTION)
            .durationDays(UPDATED_DURATION_DAYS)
            .note(UPDATED_NOTE);
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(updatedPrescription);

        restPrescriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, prescriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(prescriptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Prescription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPrescriptionToMatchAllProperties(updatedPrescription);
    }

    @Test
    @Transactional
    void putNonExistingPrescription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        prescription.setId(longCount.incrementAndGet());

        // Create the Prescription
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(prescription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrescriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, prescriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(prescriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prescription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPrescription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        prescription.setId(longCount.incrementAndGet());

        // Create the Prescription
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(prescription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrescriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(prescriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prescription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPrescription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        prescription.setId(longCount.incrementAndGet());

        // Create the Prescription
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(prescription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrescriptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(prescriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prescription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePrescriptionWithPatch() throws Exception {
        // Initialize the database
        insertedPrescription = prescriptionRepository.saveAndFlush(prescription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the prescription using partial update
        Prescription partialUpdatedPrescription = new Prescription();
        partialUpdatedPrescription.setId(prescription.getId());

        partialUpdatedPrescription
            .medicineName(UPDATED_MEDICINE_NAME)
            .form(UPDATED_FORM)
            .dosageMg(UPDATED_DOSAGE_MG)
            .instruction(UPDATED_INSTRUCTION)
            .note(UPDATED_NOTE);

        restPrescriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrescription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPrescription))
            )
            .andExpect(status().isOk());

        // Validate the Prescription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPrescriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPrescription, prescription),
            getPersistedPrescription(prescription)
        );
    }

    @Test
    @Transactional
    void fullUpdatePrescriptionWithPatch() throws Exception {
        // Initialize the database
        insertedPrescription = prescriptionRepository.saveAndFlush(prescription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the prescription using partial update
        Prescription partialUpdatedPrescription = new Prescription();
        partialUpdatedPrescription.setId(prescription.getId());

        partialUpdatedPrescription
            .medicineName(UPDATED_MEDICINE_NAME)
            .form(UPDATED_FORM)
            .dosageMg(UPDATED_DOSAGE_MG)
            .instruction(UPDATED_INSTRUCTION)
            .durationDays(UPDATED_DURATION_DAYS)
            .note(UPDATED_NOTE);

        restPrescriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrescription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPrescription))
            )
            .andExpect(status().isOk());

        // Validate the Prescription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPrescriptionUpdatableFieldsEquals(partialUpdatedPrescription, getPersistedPrescription(partialUpdatedPrescription));
    }

    @Test
    @Transactional
    void patchNonExistingPrescription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        prescription.setId(longCount.incrementAndGet());

        // Create the Prescription
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(prescription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrescriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, prescriptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(prescriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prescription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPrescription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        prescription.setId(longCount.incrementAndGet());

        // Create the Prescription
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(prescription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrescriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(prescriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prescription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPrescription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        prescription.setId(longCount.incrementAndGet());

        // Create the Prescription
        PrescriptionDTO prescriptionDTO = prescriptionMapper.toDto(prescription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrescriptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(prescriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prescription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePrescription() throws Exception {
        // Initialize the database
        insertedPrescription = prescriptionRepository.saveAndFlush(prescription);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the prescription
        restPrescriptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, prescription.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return prescriptionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Prescription getPersistedPrescription(Prescription prescription) {
        return prescriptionRepository.findById(prescription.getId()).orElseThrow();
    }

    protected void assertPersistedPrescriptionToMatchAllProperties(Prescription expectedPrescription) {
        assertPrescriptionAllPropertiesEquals(expectedPrescription, getPersistedPrescription(expectedPrescription));
    }

    protected void assertPersistedPrescriptionToMatchUpdatableProperties(Prescription expectedPrescription) {
        assertPrescriptionAllUpdatablePropertiesEquals(expectedPrescription, getPersistedPrescription(expectedPrescription));
    }
}
