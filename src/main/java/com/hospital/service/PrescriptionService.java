package com.hospital.service;

import com.hospital.domain.Prescription;
import com.hospital.repository.PrescriptionRepository;
import com.hospital.service.dto.PrescriptionDTO;
import com.hospital.service.mapper.PrescriptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hospital.domain.Prescription}.
 */
@Service
@Transactional
public class PrescriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(PrescriptionService.class);

    private final PrescriptionRepository prescriptionRepository;

    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, PrescriptionMapper prescriptionMapper) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionMapper = prescriptionMapper;
    }

    /**
     * Save a prescription.
     *
     * @param prescriptionDTO the entity to save.
     * @return the persisted entity.
     */
    public PrescriptionDTO save(PrescriptionDTO prescriptionDTO) {
        LOG.debug("Request to save Prescription : {}", prescriptionDTO);
        Prescription prescription = prescriptionMapper.toEntity(prescriptionDTO);
        prescription = prescriptionRepository.save(prescription);
        return prescriptionMapper.toDto(prescription);
    }

    /**
     * Update a prescription.
     *
     * @param prescriptionDTO the entity to save.
     * @return the persisted entity.
     */
    public PrescriptionDTO update(PrescriptionDTO prescriptionDTO) {
        LOG.debug("Request to update Prescription : {}", prescriptionDTO);
        Prescription prescription = prescriptionMapper.toEntity(prescriptionDTO);
        prescription = prescriptionRepository.save(prescription);
        return prescriptionMapper.toDto(prescription);
    }

    /**
     * Partially update a prescription.
     *
     * @param prescriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PrescriptionDTO> partialUpdate(PrescriptionDTO prescriptionDTO) {
        LOG.debug("Request to partially update Prescription : {}", prescriptionDTO);

        return prescriptionRepository
            .findById(prescriptionDTO.getId())
            .map(existingPrescription -> {
                prescriptionMapper.partialUpdate(existingPrescription, prescriptionDTO);

                return existingPrescription;
            })
            .map(prescriptionRepository::save)
            .map(prescriptionMapper::toDto);
    }

    /**
     * Get all the prescriptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PrescriptionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Prescriptions");
        return prescriptionRepository.findAll(pageable).map(prescriptionMapper::toDto);
    }

    /**
     * Get all the prescriptions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PrescriptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return prescriptionRepository.findAllWithEagerRelationships(pageable).map(prescriptionMapper::toDto);
    }

    /**
     * Get one prescription by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PrescriptionDTO> findOne(Long id) {
        LOG.debug("Request to get Prescription : {}", id);
        return prescriptionRepository.findOneWithEagerRelationships(id).map(prescriptionMapper::toDto);
    }

    /**
     * Delete the prescription by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Prescription : {}", id);
        prescriptionRepository.deleteById(id);
    }
}
