package com.hospital.service;

import com.hospital.domain.VitalSign;
import com.hospital.repository.VitalSignRepository;
import com.hospital.service.dto.VitalSignDTO;
import com.hospital.service.mapper.VitalSignMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hospital.domain.VitalSign}.
 */
@Service
@Transactional
public class VitalSignService {

    private static final Logger LOG = LoggerFactory.getLogger(VitalSignService.class);

    private final VitalSignRepository vitalSignRepository;

    private final VitalSignMapper vitalSignMapper;

    public VitalSignService(VitalSignRepository vitalSignRepository, VitalSignMapper vitalSignMapper) {
        this.vitalSignRepository = vitalSignRepository;
        this.vitalSignMapper = vitalSignMapper;
    }

    /**
     * Save a vitalSign.
     *
     * @param vitalSignDTO the entity to save.
     * @return the persisted entity.
     */
    public VitalSignDTO save(VitalSignDTO vitalSignDTO) {
        LOG.debug("Request to save VitalSign : {}", vitalSignDTO);
        VitalSign vitalSign = vitalSignMapper.toEntity(vitalSignDTO);
        vitalSign = vitalSignRepository.save(vitalSign);
        return vitalSignMapper.toDto(vitalSign);
    }

    /**
     * Update a vitalSign.
     *
     * @param vitalSignDTO the entity to save.
     * @return the persisted entity.
     */
    public VitalSignDTO update(VitalSignDTO vitalSignDTO) {
        LOG.debug("Request to update VitalSign : {}", vitalSignDTO);
        VitalSign vitalSign = vitalSignMapper.toEntity(vitalSignDTO);
        vitalSign = vitalSignRepository.save(vitalSign);
        return vitalSignMapper.toDto(vitalSign);
    }

    /**
     * Partially update a vitalSign.
     *
     * @param vitalSignDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VitalSignDTO> partialUpdate(VitalSignDTO vitalSignDTO) {
        LOG.debug("Request to partially update VitalSign : {}", vitalSignDTO);

        return vitalSignRepository
            .findById(vitalSignDTO.getId())
            .map(existingVitalSign -> {
                vitalSignMapper.partialUpdate(existingVitalSign, vitalSignDTO);

                return existingVitalSign;
            })
            .map(vitalSignRepository::save)
            .map(vitalSignMapper::toDto);
    }

    /**
     * Get all the vitalSigns.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VitalSignDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all VitalSigns");
        return vitalSignRepository.findAll(pageable).map(vitalSignMapper::toDto);
    }

    /**
     * Get all the vitalSigns with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<VitalSignDTO> findAllWithEagerRelationships(Pageable pageable) {
        return vitalSignRepository.findAllWithEagerRelationships(pageable).map(vitalSignMapper::toDto);
    }

    /**
     * Get one vitalSign by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VitalSignDTO> findOne(Long id) {
        LOG.debug("Request to get VitalSign : {}", id);
        return vitalSignRepository.findOneWithEagerRelationships(id).map(vitalSignMapper::toDto);
    }

    /**
     * Delete the vitalSign by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete VitalSign : {}", id);
        vitalSignRepository.deleteById(id);
    }
}
