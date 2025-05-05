package com.hospital.service;

import com.hospital.domain.HospitalFee;
import com.hospital.repository.HospitalFeeRepository;
import com.hospital.service.dto.HospitalFeeDTO;
import com.hospital.service.mapper.HospitalFeeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hospital.domain.HospitalFee}.
 */
@Service
@Transactional
public class HospitalFeeService {

    private static final Logger LOG = LoggerFactory.getLogger(HospitalFeeService.class);

    private final HospitalFeeRepository hospitalFeeRepository;

    private final HospitalFeeMapper hospitalFeeMapper;

    public HospitalFeeService(HospitalFeeRepository hospitalFeeRepository, HospitalFeeMapper hospitalFeeMapper) {
        this.hospitalFeeRepository = hospitalFeeRepository;
        this.hospitalFeeMapper = hospitalFeeMapper;
    }

    /**
     * Save a hospitalFee.
     *
     * @param hospitalFeeDTO the entity to save.
     * @return the persisted entity.
     */
    public HospitalFeeDTO save(HospitalFeeDTO hospitalFeeDTO) {
        LOG.debug("Request to save HospitalFee : {}", hospitalFeeDTO);
        HospitalFee hospitalFee = hospitalFeeMapper.toEntity(hospitalFeeDTO);
        hospitalFee = hospitalFeeRepository.save(hospitalFee);
        return hospitalFeeMapper.toDto(hospitalFee);
    }

    /**
     * Update a hospitalFee.
     *
     * @param hospitalFeeDTO the entity to save.
     * @return the persisted entity.
     */
    public HospitalFeeDTO update(HospitalFeeDTO hospitalFeeDTO) {
        LOG.debug("Request to update HospitalFee : {}", hospitalFeeDTO);
        HospitalFee hospitalFee = hospitalFeeMapper.toEntity(hospitalFeeDTO);
        hospitalFee = hospitalFeeRepository.save(hospitalFee);
        return hospitalFeeMapper.toDto(hospitalFee);
    }

    /**
     * Partially update a hospitalFee.
     *
     * @param hospitalFeeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HospitalFeeDTO> partialUpdate(HospitalFeeDTO hospitalFeeDTO) {
        LOG.debug("Request to partially update HospitalFee : {}", hospitalFeeDTO);

        return hospitalFeeRepository
            .findById(hospitalFeeDTO.getId())
            .map(existingHospitalFee -> {
                hospitalFeeMapper.partialUpdate(existingHospitalFee, hospitalFeeDTO);

                return existingHospitalFee;
            })
            .map(hospitalFeeRepository::save)
            .map(hospitalFeeMapper::toDto);
    }

    /**
     * Get all the hospitalFees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HospitalFeeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all HospitalFees");
        return hospitalFeeRepository.findAll(pageable).map(hospitalFeeMapper::toDto);
    }

    /**
     * Get all the hospitalFees with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<HospitalFeeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return hospitalFeeRepository.findAllWithEagerRelationships(pageable).map(hospitalFeeMapper::toDto);
    }

    /**
     * Get one hospitalFee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HospitalFeeDTO> findOne(Long id) {
        LOG.debug("Request to get HospitalFee : {}", id);
        return hospitalFeeRepository.findOneWithEagerRelationships(id).map(hospitalFeeMapper::toDto);
    }

    /**
     * Delete the hospitalFee by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete HospitalFee : {}", id);
        hospitalFeeRepository.deleteById(id);
    }
}
