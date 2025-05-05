package com.hospital.web.rest;

import static com.hospital.domain.TreatmentAsserts.*;
import static com.hospital.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.IntegrationTest;
import com.hospital.domain.Treatment;
import com.hospital.repository.TreatmentRepository;
import com.hospital.service.TreatmentService;
import com.hospital.service.dto.TreatmentDTO;
import com.hospital.service.mapper.TreatmentMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link TreatmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TreatmentResourceIT {

    private static final String DEFAULT_TREATMENT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_TREATMENT_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_TREATMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TREATMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/treatments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Mock
    private TreatmentRepository treatmentRepositoryMock;

    @Autowired
    private TreatmentMapper treatmentMapper;

    @Mock
    private TreatmentService treatmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTreatmentMockMvc;

    private Treatment treatment;

    private Treatment insertedTreatment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Treatment createEntity() {
        return new Treatment().treatmentDescription(DEFAULT_TREATMENT_DESCRIPTION).treatmentDate(DEFAULT_TREATMENT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Treatment createUpdatedEntity() {
        return new Treatment().treatmentDescription(UPDATED_TREATMENT_DESCRIPTION).treatmentDate(UPDATED_TREATMENT_DATE);
    }

    @BeforeEach
    void initTest() {
        treatment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTreatment != null) {
            treatmentRepository.delete(insertedTreatment);
            insertedTreatment = null;
        }
    }

    @Test
    @Transactional
    void createTreatment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Treatment
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(treatment);
        var returnedTreatmentDTO = om.readValue(
            restTreatmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(treatmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TreatmentDTO.class
        );

        // Validate the Treatment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTreatment = treatmentMapper.toEntity(returnedTreatmentDTO);
        assertTreatmentUpdatableFieldsEquals(returnedTreatment, getPersistedTreatment(returnedTreatment));

        insertedTreatment = returnedTreatment;
    }

    @Test
    @Transactional
    void createTreatmentWithExistingId() throws Exception {
        // Create the Treatment with an existing ID
        treatment.setId(1L);
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(treatment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTreatmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(treatmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Treatment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTreatments() throws Exception {
        // Initialize the database
        insertedTreatment = treatmentRepository.saveAndFlush(treatment);

        // Get all the treatmentList
        restTreatmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(treatment.getId().intValue())))
            .andExpect(jsonPath("$.[*].treatmentDescription").value(hasItem(DEFAULT_TREATMENT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].treatmentDate").value(hasItem(DEFAULT_TREATMENT_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTreatmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(treatmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTreatmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(treatmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTreatmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(treatmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTreatmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(treatmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTreatment() throws Exception {
        // Initialize the database
        insertedTreatment = treatmentRepository.saveAndFlush(treatment);

        // Get the treatment
        restTreatmentMockMvc
            .perform(get(ENTITY_API_URL_ID, treatment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(treatment.getId().intValue()))
            .andExpect(jsonPath("$.treatmentDescription").value(DEFAULT_TREATMENT_DESCRIPTION))
            .andExpect(jsonPath("$.treatmentDate").value(DEFAULT_TREATMENT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTreatment() throws Exception {
        // Get the treatment
        restTreatmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTreatment() throws Exception {
        // Initialize the database
        insertedTreatment = treatmentRepository.saveAndFlush(treatment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the treatment
        Treatment updatedTreatment = treatmentRepository.findById(treatment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTreatment are not directly saved in db
        em.detach(updatedTreatment);
        updatedTreatment.treatmentDescription(UPDATED_TREATMENT_DESCRIPTION).treatmentDate(UPDATED_TREATMENT_DATE);
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(updatedTreatment);

        restTreatmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, treatmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(treatmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Treatment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTreatmentToMatchAllProperties(updatedTreatment);
    }

    @Test
    @Transactional
    void putNonExistingTreatment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        treatment.setId(longCount.incrementAndGet());

        // Create the Treatment
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(treatment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTreatmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, treatmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(treatmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Treatment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTreatment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        treatment.setId(longCount.incrementAndGet());

        // Create the Treatment
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(treatment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTreatmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(treatmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Treatment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTreatment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        treatment.setId(longCount.incrementAndGet());

        // Create the Treatment
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(treatment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTreatmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(treatmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Treatment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTreatmentWithPatch() throws Exception {
        // Initialize the database
        insertedTreatment = treatmentRepository.saveAndFlush(treatment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the treatment using partial update
        Treatment partialUpdatedTreatment = new Treatment();
        partialUpdatedTreatment.setId(treatment.getId());

        restTreatmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTreatment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTreatment))
            )
            .andExpect(status().isOk());

        // Validate the Treatment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTreatmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTreatment, treatment),
            getPersistedTreatment(treatment)
        );
    }

    @Test
    @Transactional
    void fullUpdateTreatmentWithPatch() throws Exception {
        // Initialize the database
        insertedTreatment = treatmentRepository.saveAndFlush(treatment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the treatment using partial update
        Treatment partialUpdatedTreatment = new Treatment();
        partialUpdatedTreatment.setId(treatment.getId());

        partialUpdatedTreatment.treatmentDescription(UPDATED_TREATMENT_DESCRIPTION).treatmentDate(UPDATED_TREATMENT_DATE);

        restTreatmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTreatment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTreatment))
            )
            .andExpect(status().isOk());

        // Validate the Treatment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTreatmentUpdatableFieldsEquals(partialUpdatedTreatment, getPersistedTreatment(partialUpdatedTreatment));
    }

    @Test
    @Transactional
    void patchNonExistingTreatment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        treatment.setId(longCount.incrementAndGet());

        // Create the Treatment
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(treatment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTreatmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, treatmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(treatmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Treatment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTreatment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        treatment.setId(longCount.incrementAndGet());

        // Create the Treatment
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(treatment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTreatmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(treatmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Treatment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTreatment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        treatment.setId(longCount.incrementAndGet());

        // Create the Treatment
        TreatmentDTO treatmentDTO = treatmentMapper.toDto(treatment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTreatmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(treatmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Treatment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTreatment() throws Exception {
        // Initialize the database
        insertedTreatment = treatmentRepository.saveAndFlush(treatment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the treatment
        restTreatmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, treatment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return treatmentRepository.count();
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

    protected Treatment getPersistedTreatment(Treatment treatment) {
        return treatmentRepository.findById(treatment.getId()).orElseThrow();
    }

    protected void assertPersistedTreatmentToMatchAllProperties(Treatment expectedTreatment) {
        assertTreatmentAllPropertiesEquals(expectedTreatment, getPersistedTreatment(expectedTreatment));
    }

    protected void assertPersistedTreatmentToMatchUpdatableProperties(Treatment expectedTreatment) {
        assertTreatmentAllUpdatablePropertiesEquals(expectedTreatment, getPersistedTreatment(expectedTreatment));
    }
}
