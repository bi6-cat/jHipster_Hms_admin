package com.hospital.web.rest;

import static com.hospital.domain.VitalSignAsserts.*;
import static com.hospital.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.IntegrationTest;
import com.hospital.domain.VitalSign;
import com.hospital.repository.VitalSignRepository;
import com.hospital.service.VitalSignService;
import com.hospital.service.dto.VitalSignDTO;
import com.hospital.service.mapper.VitalSignMapper;
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
 * Integration tests for the {@link VitalSignResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VitalSignResourceIT {

    private static final LocalDate DEFAULT_MEASUREMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MEASUREMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_BLOOD_PRESSURE = "AAAAAAAAAA";
    private static final String UPDATED_BLOOD_PRESSURE = "BBBBBBBBBB";

    private static final Integer DEFAULT_HEART_RATE = 1;
    private static final Integer UPDATED_HEART_RATE = 2;

    private static final Integer DEFAULT_RESPIRATORY_RATE = 1;
    private static final Integer UPDATED_RESPIRATORY_RATE = 2;

    private static final Double DEFAULT_TEMPERATURE = 1D;
    private static final Double UPDATED_TEMPERATURE = 2D;

    private static final Integer DEFAULT_OXYGEN_SATURATION = 1;
    private static final Integer UPDATED_OXYGEN_SATURATION = 2;

    private static final Integer DEFAULT_BLOOD_SUGAR = 1;
    private static final Integer UPDATED_BLOOD_SUGAR = 2;

    private static final String ENTITY_API_URL = "/api/vital-signs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Mock
    private VitalSignRepository vitalSignRepositoryMock;

    @Autowired
    private VitalSignMapper vitalSignMapper;

    @Mock
    private VitalSignService vitalSignServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVitalSignMockMvc;

    private VitalSign vitalSign;

    private VitalSign insertedVitalSign;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VitalSign createEntity() {
        return new VitalSign()
            .measurementDate(DEFAULT_MEASUREMENT_DATE)
            .bloodPressure(DEFAULT_BLOOD_PRESSURE)
            .heartRate(DEFAULT_HEART_RATE)
            .respiratoryRate(DEFAULT_RESPIRATORY_RATE)
            .temperature(DEFAULT_TEMPERATURE)
            .oxygenSaturation(DEFAULT_OXYGEN_SATURATION)
            .bloodSugar(DEFAULT_BLOOD_SUGAR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VitalSign createUpdatedEntity() {
        return new VitalSign()
            .measurementDate(UPDATED_MEASUREMENT_DATE)
            .bloodPressure(UPDATED_BLOOD_PRESSURE)
            .heartRate(UPDATED_HEART_RATE)
            .respiratoryRate(UPDATED_RESPIRATORY_RATE)
            .temperature(UPDATED_TEMPERATURE)
            .oxygenSaturation(UPDATED_OXYGEN_SATURATION)
            .bloodSugar(UPDATED_BLOOD_SUGAR);
    }

    @BeforeEach
    void initTest() {
        vitalSign = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVitalSign != null) {
            vitalSignRepository.delete(insertedVitalSign);
            insertedVitalSign = null;
        }
    }

    @Test
    @Transactional
    void createVitalSign() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VitalSign
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(vitalSign);
        var returnedVitalSignDTO = om.readValue(
            restVitalSignMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vitalSignDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VitalSignDTO.class
        );

        // Validate the VitalSign in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVitalSign = vitalSignMapper.toEntity(returnedVitalSignDTO);
        assertVitalSignUpdatableFieldsEquals(returnedVitalSign, getPersistedVitalSign(returnedVitalSign));

        insertedVitalSign = returnedVitalSign;
    }

    @Test
    @Transactional
    void createVitalSignWithExistingId() throws Exception {
        // Create the VitalSign with an existing ID
        vitalSign.setId(1L);
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(vitalSign);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVitalSignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vitalSignDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VitalSign in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVitalSigns() throws Exception {
        // Initialize the database
        insertedVitalSign = vitalSignRepository.saveAndFlush(vitalSign);

        // Get all the vitalSignList
        restVitalSignMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vitalSign.getId().intValue())))
            .andExpect(jsonPath("$.[*].measurementDate").value(hasItem(DEFAULT_MEASUREMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].bloodPressure").value(hasItem(DEFAULT_BLOOD_PRESSURE)))
            .andExpect(jsonPath("$.[*].heartRate").value(hasItem(DEFAULT_HEART_RATE)))
            .andExpect(jsonPath("$.[*].respiratoryRate").value(hasItem(DEFAULT_RESPIRATORY_RATE)))
            .andExpect(jsonPath("$.[*].temperature").value(hasItem(DEFAULT_TEMPERATURE)))
            .andExpect(jsonPath("$.[*].oxygenSaturation").value(hasItem(DEFAULT_OXYGEN_SATURATION)))
            .andExpect(jsonPath("$.[*].bloodSugar").value(hasItem(DEFAULT_BLOOD_SUGAR)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVitalSignsWithEagerRelationshipsIsEnabled() throws Exception {
        when(vitalSignServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVitalSignMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(vitalSignServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVitalSignsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(vitalSignServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVitalSignMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(vitalSignRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVitalSign() throws Exception {
        // Initialize the database
        insertedVitalSign = vitalSignRepository.saveAndFlush(vitalSign);

        // Get the vitalSign
        restVitalSignMockMvc
            .perform(get(ENTITY_API_URL_ID, vitalSign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vitalSign.getId().intValue()))
            .andExpect(jsonPath("$.measurementDate").value(DEFAULT_MEASUREMENT_DATE.toString()))
            .andExpect(jsonPath("$.bloodPressure").value(DEFAULT_BLOOD_PRESSURE))
            .andExpect(jsonPath("$.heartRate").value(DEFAULT_HEART_RATE))
            .andExpect(jsonPath("$.respiratoryRate").value(DEFAULT_RESPIRATORY_RATE))
            .andExpect(jsonPath("$.temperature").value(DEFAULT_TEMPERATURE))
            .andExpect(jsonPath("$.oxygenSaturation").value(DEFAULT_OXYGEN_SATURATION))
            .andExpect(jsonPath("$.bloodSugar").value(DEFAULT_BLOOD_SUGAR));
    }

    @Test
    @Transactional
    void getNonExistingVitalSign() throws Exception {
        // Get the vitalSign
        restVitalSignMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVitalSign() throws Exception {
        // Initialize the database
        insertedVitalSign = vitalSignRepository.saveAndFlush(vitalSign);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vitalSign
        VitalSign updatedVitalSign = vitalSignRepository.findById(vitalSign.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVitalSign are not directly saved in db
        em.detach(updatedVitalSign);
        updatedVitalSign
            .measurementDate(UPDATED_MEASUREMENT_DATE)
            .bloodPressure(UPDATED_BLOOD_PRESSURE)
            .heartRate(UPDATED_HEART_RATE)
            .respiratoryRate(UPDATED_RESPIRATORY_RATE)
            .temperature(UPDATED_TEMPERATURE)
            .oxygenSaturation(UPDATED_OXYGEN_SATURATION)
            .bloodSugar(UPDATED_BLOOD_SUGAR);
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(updatedVitalSign);

        restVitalSignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vitalSignDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vitalSignDTO))
            )
            .andExpect(status().isOk());

        // Validate the VitalSign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVitalSignToMatchAllProperties(updatedVitalSign);
    }

    @Test
    @Transactional
    void putNonExistingVitalSign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vitalSign.setId(longCount.incrementAndGet());

        // Create the VitalSign
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(vitalSign);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVitalSignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vitalSignDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vitalSignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VitalSign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVitalSign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vitalSign.setId(longCount.incrementAndGet());

        // Create the VitalSign
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(vitalSign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVitalSignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vitalSignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VitalSign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVitalSign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vitalSign.setId(longCount.incrementAndGet());

        // Create the VitalSign
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(vitalSign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVitalSignMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vitalSignDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VitalSign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVitalSignWithPatch() throws Exception {
        // Initialize the database
        insertedVitalSign = vitalSignRepository.saveAndFlush(vitalSign);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vitalSign using partial update
        VitalSign partialUpdatedVitalSign = new VitalSign();
        partialUpdatedVitalSign.setId(vitalSign.getId());

        partialUpdatedVitalSign
            .measurementDate(UPDATED_MEASUREMENT_DATE)
            .temperature(UPDATED_TEMPERATURE)
            .oxygenSaturation(UPDATED_OXYGEN_SATURATION)
            .bloodSugar(UPDATED_BLOOD_SUGAR);

        restVitalSignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVitalSign.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVitalSign))
            )
            .andExpect(status().isOk());

        // Validate the VitalSign in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVitalSignUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVitalSign, vitalSign),
            getPersistedVitalSign(vitalSign)
        );
    }

    @Test
    @Transactional
    void fullUpdateVitalSignWithPatch() throws Exception {
        // Initialize the database
        insertedVitalSign = vitalSignRepository.saveAndFlush(vitalSign);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vitalSign using partial update
        VitalSign partialUpdatedVitalSign = new VitalSign();
        partialUpdatedVitalSign.setId(vitalSign.getId());

        partialUpdatedVitalSign
            .measurementDate(UPDATED_MEASUREMENT_DATE)
            .bloodPressure(UPDATED_BLOOD_PRESSURE)
            .heartRate(UPDATED_HEART_RATE)
            .respiratoryRate(UPDATED_RESPIRATORY_RATE)
            .temperature(UPDATED_TEMPERATURE)
            .oxygenSaturation(UPDATED_OXYGEN_SATURATION)
            .bloodSugar(UPDATED_BLOOD_SUGAR);

        restVitalSignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVitalSign.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVitalSign))
            )
            .andExpect(status().isOk());

        // Validate the VitalSign in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVitalSignUpdatableFieldsEquals(partialUpdatedVitalSign, getPersistedVitalSign(partialUpdatedVitalSign));
    }

    @Test
    @Transactional
    void patchNonExistingVitalSign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vitalSign.setId(longCount.incrementAndGet());

        // Create the VitalSign
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(vitalSign);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVitalSignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, vitalSignDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vitalSignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VitalSign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVitalSign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vitalSign.setId(longCount.incrementAndGet());

        // Create the VitalSign
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(vitalSign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVitalSignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vitalSignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VitalSign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVitalSign() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vitalSign.setId(longCount.incrementAndGet());

        // Create the VitalSign
        VitalSignDTO vitalSignDTO = vitalSignMapper.toDto(vitalSign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVitalSignMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(vitalSignDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VitalSign in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVitalSign() throws Exception {
        // Initialize the database
        insertedVitalSign = vitalSignRepository.saveAndFlush(vitalSign);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the vitalSign
        restVitalSignMockMvc
            .perform(delete(ENTITY_API_URL_ID, vitalSign.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return vitalSignRepository.count();
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

    protected VitalSign getPersistedVitalSign(VitalSign vitalSign) {
        return vitalSignRepository.findById(vitalSign.getId()).orElseThrow();
    }

    protected void assertPersistedVitalSignToMatchAllProperties(VitalSign expectedVitalSign) {
        assertVitalSignAllPropertiesEquals(expectedVitalSign, getPersistedVitalSign(expectedVitalSign));
    }

    protected void assertPersistedVitalSignToMatchUpdatableProperties(VitalSign expectedVitalSign) {
        assertVitalSignAllUpdatablePropertiesEquals(expectedVitalSign, getPersistedVitalSign(expectedVitalSign));
    }
}
