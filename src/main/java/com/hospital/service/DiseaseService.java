package com.hospital.service;

import com.hospital.domain.Disease;
import com.hospital.repository.DiseaseRepository;
import com.hospital.service.dto.DiseaseDTO;
import com.hospital.service.mapper.DiseaseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hospital.domain.Disease}.
 */
@Service
@Transactional
public class DiseaseService {

    private static final Logger LOG = LoggerFactory.getLogger(DiseaseService.class);

    private final DiseaseRepository diseaseRepository;

    private final DiseaseMapper diseaseMapper;

    public DiseaseService(DiseaseRepository diseaseRepository, DiseaseMapper diseaseMapper) {
        this.diseaseRepository = diseaseRepository;
        this.diseaseMapper = diseaseMapper;
    }

    /**
     * Save a disease.
     *
     * @param diseaseDTO the entity to save.
     * @return the persisted entity.
     */
    public DiseaseDTO save(DiseaseDTO diseaseDTO) {
        LOG.debug("Request to save Disease : {}", diseaseDTO);
        Disease disease = diseaseMapper.toEntity(diseaseDTO);
        disease = diseaseRepository.save(disease);
        return diseaseMapper.toDto(disease);
    }

    /**
     * Update a disease.
     *
     * @param diseaseDTO the entity to save.
     * @return the persisted entity.
     */
    public DiseaseDTO update(DiseaseDTO diseaseDTO) {
        LOG.debug("Request to update Disease : {}", diseaseDTO);
        Disease disease = diseaseMapper.toEntity(diseaseDTO);
        disease = diseaseRepository.save(disease);
        return diseaseMapper.toDto(disease);
    }

    /**
     * Partially update a disease.
     *
     * @param diseaseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DiseaseDTO> partialUpdate(DiseaseDTO diseaseDTO) {
        LOG.debug("Request to partially update Disease : {}", diseaseDTO);

        return diseaseRepository
            .findById(diseaseDTO.getId())
            .map(existingDisease -> {
                diseaseMapper.partialUpdate(existingDisease, diseaseDTO);

                return existingDisease;
            })
            .map(diseaseRepository::save)
            .map(diseaseMapper::toDto);
    }

    /**
     * Get all the diseases.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DiseaseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Diseases");
        return diseaseRepository.findAll(pageable).map(diseaseMapper::toDto);
    }

    /**
     * Get all the diseases with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<DiseaseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return diseaseRepository.findAllWithEagerRelationships(pageable).map(diseaseMapper::toDto);
    }

    /**
     * Get one disease by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DiseaseDTO> findOne(Long id) {
        LOG.debug("Request to get Disease : {}", id);
        return diseaseRepository.findOneWithEagerRelationships(id).map(diseaseMapper::toDto);
    }

    /**
     * Delete the disease by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Disease : {}", id);
        diseaseRepository.deleteById(id);
    }
}
