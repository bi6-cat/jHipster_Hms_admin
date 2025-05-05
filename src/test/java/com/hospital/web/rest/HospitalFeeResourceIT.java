package com.hospital.web.rest;

import static com.hospital.domain.HospitalFeeAsserts.*;
import static com.hospital.web.rest.TestUtil.createUpdateProxyForBean;
import static com.hospital.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.IntegrationTest;
import com.hospital.domain.HospitalFee;
import com.hospital.repository.HospitalFeeRepository;
import com.hospital.service.HospitalFeeService;
import com.hospital.service.dto.HospitalFeeDTO;
import com.hospital.service.mapper.HospitalFeeMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link HospitalFeeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HospitalFeeResourceIT {

    private static final String DEFAULT_SERVICE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final LocalDate DEFAULT_FEE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FEE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/hospital-fees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HospitalFeeRepository hospitalFeeRepository;

    @Mock
    private HospitalFeeRepository hospitalFeeRepositoryMock;

    @Autowired
    private HospitalFeeMapper hospitalFeeMapper;

    @Mock
    private HospitalFeeService hospitalFeeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHospitalFeeMockMvc;

    private HospitalFee hospitalFee;

    private HospitalFee insertedHospitalFee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HospitalFee createEntity() {
        return new HospitalFee()
            .serviceType(DEFAULT_SERVICE_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .amount(DEFAULT_AMOUNT)
            .feeDate(DEFAULT_FEE_DATE)
            .phone(DEFAULT_PHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HospitalFee createUpdatedEntity() {
        return new HospitalFee()
            .serviceType(UPDATED_SERVICE_TYPE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .feeDate(UPDATED_FEE_DATE)
            .phone(UPDATED_PHONE);
    }

    @BeforeEach
    void initTest() {
        hospitalFee = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHospitalFee != null) {
            hospitalFeeRepository.delete(insertedHospitalFee);
            insertedHospitalFee = null;
        }
    }

    @Test
    @Transactional
    void createHospitalFee() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HospitalFee
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(hospitalFee);
        var returnedHospitalFeeDTO = om.readValue(
            restHospitalFeeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hospitalFeeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HospitalFeeDTO.class
        );

        // Validate the HospitalFee in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHospitalFee = hospitalFeeMapper.toEntity(returnedHospitalFeeDTO);
        assertHospitalFeeUpdatableFieldsEquals(returnedHospitalFee, getPersistedHospitalFee(returnedHospitalFee));

        insertedHospitalFee = returnedHospitalFee;
    }

    @Test
    @Transactional
    void createHospitalFeeWithExistingId() throws Exception {
        // Create the HospitalFee with an existing ID
        hospitalFee.setId(1L);
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(hospitalFee);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHospitalFeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hospitalFeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the HospitalFee in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHospitalFees() throws Exception {
        // Initialize the database
        insertedHospitalFee = hospitalFeeRepository.saveAndFlush(hospitalFee);

        // Get all the hospitalFeeList
        restHospitalFeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hospitalFee.getId().intValue())))
            .andExpect(jsonPath("$.[*].serviceType").value(hasItem(DEFAULT_SERVICE_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].feeDate").value(hasItem(DEFAULT_FEE_DATE.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHospitalFeesWithEagerRelationshipsIsEnabled() throws Exception {
        when(hospitalFeeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHospitalFeeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(hospitalFeeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHospitalFeesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(hospitalFeeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHospitalFeeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(hospitalFeeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getHospitalFee() throws Exception {
        // Initialize the database
        insertedHospitalFee = hospitalFeeRepository.saveAndFlush(hospitalFee);

        // Get the hospitalFee
        restHospitalFeeMockMvc
            .perform(get(ENTITY_API_URL_ID, hospitalFee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hospitalFee.getId().intValue()))
            .andExpect(jsonPath("$.serviceType").value(DEFAULT_SERVICE_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.feeDate").value(DEFAULT_FEE_DATE.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    void getNonExistingHospitalFee() throws Exception {
        // Get the hospitalFee
        restHospitalFeeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHospitalFee() throws Exception {
        // Initialize the database
        insertedHospitalFee = hospitalFeeRepository.saveAndFlush(hospitalFee);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hospitalFee
        HospitalFee updatedHospitalFee = hospitalFeeRepository.findById(hospitalFee.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHospitalFee are not directly saved in db
        em.detach(updatedHospitalFee);
        updatedHospitalFee
            .serviceType(UPDATED_SERVICE_TYPE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .feeDate(UPDATED_FEE_DATE)
            .phone(UPDATED_PHONE);
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(updatedHospitalFee);

        restHospitalFeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hospitalFeeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hospitalFeeDTO))
            )
            .andExpect(status().isOk());

        // Validate the HospitalFee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHospitalFeeToMatchAllProperties(updatedHospitalFee);
    }

    @Test
    @Transactional
    void putNonExistingHospitalFee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hospitalFee.setId(longCount.incrementAndGet());

        // Create the HospitalFee
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(hospitalFee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHospitalFeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hospitalFeeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hospitalFeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HospitalFee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHospitalFee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hospitalFee.setId(longCount.incrementAndGet());

        // Create the HospitalFee
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(hospitalFee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHospitalFeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hospitalFeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HospitalFee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHospitalFee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hospitalFee.setId(longCount.incrementAndGet());

        // Create the HospitalFee
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(hospitalFee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHospitalFeeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hospitalFeeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HospitalFee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHospitalFeeWithPatch() throws Exception {
        // Initialize the database
        insertedHospitalFee = hospitalFeeRepository.saveAndFlush(hospitalFee);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hospitalFee using partial update
        HospitalFee partialUpdatedHospitalFee = new HospitalFee();
        partialUpdatedHospitalFee.setId(hospitalFee.getId());

        partialUpdatedHospitalFee.serviceType(UPDATED_SERVICE_TYPE).phone(UPDATED_PHONE);

        restHospitalFeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHospitalFee.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHospitalFee))
            )
            .andExpect(status().isOk());

        // Validate the HospitalFee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHospitalFeeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHospitalFee, hospitalFee),
            getPersistedHospitalFee(hospitalFee)
        );
    }

    @Test
    @Transactional
    void fullUpdateHospitalFeeWithPatch() throws Exception {
        // Initialize the database
        insertedHospitalFee = hospitalFeeRepository.saveAndFlush(hospitalFee);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hospitalFee using partial update
        HospitalFee partialUpdatedHospitalFee = new HospitalFee();
        partialUpdatedHospitalFee.setId(hospitalFee.getId());

        partialUpdatedHospitalFee
            .serviceType(UPDATED_SERVICE_TYPE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .feeDate(UPDATED_FEE_DATE)
            .phone(UPDATED_PHONE);

        restHospitalFeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHospitalFee.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHospitalFee))
            )
            .andExpect(status().isOk());

        // Validate the HospitalFee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHospitalFeeUpdatableFieldsEquals(partialUpdatedHospitalFee, getPersistedHospitalFee(partialUpdatedHospitalFee));
    }

    @Test
    @Transactional
    void patchNonExistingHospitalFee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hospitalFee.setId(longCount.incrementAndGet());

        // Create the HospitalFee
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(hospitalFee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHospitalFeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hospitalFeeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(hospitalFeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HospitalFee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHospitalFee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hospitalFee.setId(longCount.incrementAndGet());

        // Create the HospitalFee
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(hospitalFee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHospitalFeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(hospitalFeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HospitalFee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHospitalFee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hospitalFee.setId(longCount.incrementAndGet());

        // Create the HospitalFee
        HospitalFeeDTO hospitalFeeDTO = hospitalFeeMapper.toDto(hospitalFee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHospitalFeeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(hospitalFeeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HospitalFee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHospitalFee() throws Exception {
        // Initialize the database
        insertedHospitalFee = hospitalFeeRepository.saveAndFlush(hospitalFee);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the hospitalFee
        restHospitalFeeMockMvc
            .perform(delete(ENTITY_API_URL_ID, hospitalFee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return hospitalFeeRepository.count();
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

    protected HospitalFee getPersistedHospitalFee(HospitalFee hospitalFee) {
        return hospitalFeeRepository.findById(hospitalFee.getId()).orElseThrow();
    }

    protected void assertPersistedHospitalFeeToMatchAllProperties(HospitalFee expectedHospitalFee) {
        assertHospitalFeeAllPropertiesEquals(expectedHospitalFee, getPersistedHospitalFee(expectedHospitalFee));
    }

    protected void assertPersistedHospitalFeeToMatchUpdatableProperties(HospitalFee expectedHospitalFee) {
        assertHospitalFeeAllUpdatablePropertiesEquals(expectedHospitalFee, getPersistedHospitalFee(expectedHospitalFee));
    }
}
