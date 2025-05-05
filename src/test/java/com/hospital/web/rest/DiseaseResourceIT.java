package com.hospital.web.rest;

import static com.hospital.domain.DiseaseAsserts.*;
import static com.hospital.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.IntegrationTest;
import com.hospital.domain.Disease;
import com.hospital.repository.DiseaseRepository;
import com.hospital.service.DiseaseService;
import com.hospital.service.dto.DiseaseDTO;
import com.hospital.service.mapper.DiseaseMapper;
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
 * Integration tests for the {@link DiseaseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DiseaseResourceIT {

    private static final String DEFAULT_DISEASE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISEASE_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DIAGNOSIS_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DIAGNOSIS_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/diseases";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Mock
    private DiseaseRepository diseaseRepositoryMock;

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Mock
    private DiseaseService diseaseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiseaseMockMvc;

    private Disease disease;

    private Disease insertedDisease;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Disease createEntity() {
        return new Disease().diseaseName(DEFAULT_DISEASE_NAME).diagnosisDate(DEFAULT_DIAGNOSIS_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Disease createUpdatedEntity() {
        return new Disease().diseaseName(UPDATED_DISEASE_NAME).diagnosisDate(UPDATED_DIAGNOSIS_DATE);
    }

    @BeforeEach
    void initTest() {
        disease = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDisease != null) {
            diseaseRepository.delete(insertedDisease);
            insertedDisease = null;
        }
    }

    @Test
    @Transactional
    void createDisease() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);
        var returnedDiseaseDTO = om.readValue(
            restDiseaseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diseaseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DiseaseDTO.class
        );

        // Validate the Disease in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDisease = diseaseMapper.toEntity(returnedDiseaseDTO);
        assertDiseaseUpdatableFieldsEquals(returnedDisease, getPersistedDisease(returnedDisease));

        insertedDisease = returnedDisease;
    }

    @Test
    @Transactional
    void createDiseaseWithExistingId() throws Exception {
        // Create the Disease with an existing ID
        disease.setId(1L);
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiseaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diseaseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Disease in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDiseases() throws Exception {
        // Initialize the database
        insertedDisease = diseaseRepository.saveAndFlush(disease);

        // Get all the diseaseList
        restDiseaseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(disease.getId().intValue())))
            .andExpect(jsonPath("$.[*].diseaseName").value(hasItem(DEFAULT_DISEASE_NAME)))
            .andExpect(jsonPath("$.[*].diagnosisDate").value(hasItem(DEFAULT_DIAGNOSIS_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDiseasesWithEagerRelationshipsIsEnabled() throws Exception {
        when(diseaseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDiseaseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(diseaseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDiseasesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(diseaseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDiseaseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(diseaseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDisease() throws Exception {
        // Initialize the database
        insertedDisease = diseaseRepository.saveAndFlush(disease);

        // Get the disease
        restDiseaseMockMvc
            .perform(get(ENTITY_API_URL_ID, disease.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(disease.getId().intValue()))
            .andExpect(jsonPath("$.diseaseName").value(DEFAULT_DISEASE_NAME))
            .andExpect(jsonPath("$.diagnosisDate").value(DEFAULT_DIAGNOSIS_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDisease() throws Exception {
        // Get the disease
        restDiseaseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDisease() throws Exception {
        // Initialize the database
        insertedDisease = diseaseRepository.saveAndFlush(disease);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disease
        Disease updatedDisease = diseaseRepository.findById(disease.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDisease are not directly saved in db
        em.detach(updatedDisease);
        updatedDisease.diseaseName(UPDATED_DISEASE_NAME).diagnosisDate(UPDATED_DIAGNOSIS_DATE);
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(updatedDisease);

        restDiseaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, diseaseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diseaseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Disease in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDiseaseToMatchAllProperties(updatedDisease);
    }

    @Test
    @Transactional
    void putNonExistingDisease() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disease.setId(longCount.incrementAndGet());

        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiseaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, diseaseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diseaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Disease in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDisease() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disease.setId(longCount.incrementAndGet());

        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiseaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(diseaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Disease in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDisease() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disease.setId(longCount.incrementAndGet());

        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiseaseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(diseaseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Disease in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDiseaseWithPatch() throws Exception {
        // Initialize the database
        insertedDisease = diseaseRepository.saveAndFlush(disease);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disease using partial update
        Disease partialUpdatedDisease = new Disease();
        partialUpdatedDisease.setId(disease.getId());

        partialUpdatedDisease.diseaseName(UPDATED_DISEASE_NAME);

        restDiseaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDisease.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDisease))
            )
            .andExpect(status().isOk());

        // Validate the Disease in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiseaseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDisease, disease), getPersistedDisease(disease));
    }

    @Test
    @Transactional
    void fullUpdateDiseaseWithPatch() throws Exception {
        // Initialize the database
        insertedDisease = diseaseRepository.saveAndFlush(disease);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disease using partial update
        Disease partialUpdatedDisease = new Disease();
        partialUpdatedDisease.setId(disease.getId());

        partialUpdatedDisease.diseaseName(UPDATED_DISEASE_NAME).diagnosisDate(UPDATED_DIAGNOSIS_DATE);

        restDiseaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDisease.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDisease))
            )
            .andExpect(status().isOk());

        // Validate the Disease in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiseaseUpdatableFieldsEquals(partialUpdatedDisease, getPersistedDisease(partialUpdatedDisease));
    }

    @Test
    @Transactional
    void patchNonExistingDisease() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disease.setId(longCount.incrementAndGet());

        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiseaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, diseaseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(diseaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Disease in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDisease() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disease.setId(longCount.incrementAndGet());

        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiseaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(diseaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Disease in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDisease() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disease.setId(longCount.incrementAndGet());

        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiseaseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(diseaseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Disease in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDisease() throws Exception {
        // Initialize the database
        insertedDisease = diseaseRepository.saveAndFlush(disease);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the disease
        restDiseaseMockMvc
            .perform(delete(ENTITY_API_URL_ID, disease.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return diseaseRepository.count();
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

    protected Disease getPersistedDisease(Disease disease) {
        return diseaseRepository.findById(disease.getId()).orElseThrow();
    }

    protected void assertPersistedDiseaseToMatchAllProperties(Disease expectedDisease) {
        assertDiseaseAllPropertiesEquals(expectedDisease, getPersistedDisease(expectedDisease));
    }

    protected void assertPersistedDiseaseToMatchUpdatableProperties(Disease expectedDisease) {
        assertDiseaseAllUpdatablePropertiesEquals(expectedDisease, getPersistedDisease(expectedDisease));
    }
}
