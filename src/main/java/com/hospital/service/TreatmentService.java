package com.hospital.service;

import com.hospital.domain.Treatment;
import com.hospital.repository.TreatmentRepository;
import com.hospital.service.dto.TreatmentDTO;
import com.hospital.service.mapper.TreatmentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hospital.domain.Treatment}.
 */
@Service
@Transactional
public class TreatmentService {

    private static final Logger LOG = LoggerFactory.getLogger(TreatmentService.class);

    private final TreatmentRepository treatmentRepository;

    private final TreatmentMapper treatmentMapper;

    public TreatmentService(TreatmentRepository treatmentRepository, TreatmentMapper treatmentMapper) {
        this.treatmentRepository = treatmentRepository;
        this.treatmentMapper = treatmentMapper;
    }

    /**
     * Save a treatment.
     *
     * @param treatmentDTO the entity to save.
     * @return the persisted entity.
     */
    public TreatmentDTO save(TreatmentDTO treatmentDTO) {
        LOG.debug("Request to save Treatment : {}", treatmentDTO);
        Treatment treatment = treatmentMapper.toEntity(treatmentDTO);
        treatment = treatmentRepository.save(treatment);
        return treatmentMapper.toDto(treatment);
    }

    /**
     * Update a treatment.
     *
     * @param treatmentDTO the entity to save.
     * @return the persisted entity.
     */
    public TreatmentDTO update(TreatmentDTO treatmentDTO) {
        LOG.debug("Request to update Treatment : {}", treatmentDTO);
        Treatment treatment = treatmentMapper.toEntity(treatmentDTO);
        treatment = treatmentRepository.save(treatment);
        return treatmentMapper.toDto(treatment);
    }

    /**
     * Partially update a treatment.
     *
     * @param treatmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TreatmentDTO> partialUpdate(TreatmentDTO treatmentDTO) {
        LOG.debug("Request to partially update Treatment : {}", treatmentDTO);

        return treatmentRepository
            .findById(treatmentDTO.getId())
            .map(existingTreatment -> {
                treatmentMapper.partialUpdate(existingTreatment, treatmentDTO);

                return existingTreatment;
            })
            .map(treatmentRepository::save)
            .map(treatmentMapper::toDto);
    }

    /**
     * Get all the treatments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TreatmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Treatments");
        return treatmentRepository.findAll(pageable).map(treatmentMapper::toDto);
    }

    /**
     * Get all the treatments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TreatmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return treatmentRepository.findAllWithEagerRelationships(pageable).map(treatmentMapper::toDto);
    }

    /**
     * Get one treatment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TreatmentDTO> findOne(Long id) {
        LOG.debug("Request to get Treatment : {}", id);
        return treatmentRepository.findOneWithEagerRelationships(id).map(treatmentMapper::toDto);
    }

    /**
     * Delete the treatment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Treatment : {}", id);
        treatmentRepository.deleteById(id);
    }
}
