package com.hospital.web.rest;

import com.hospital.repository.HospitalFeeRepository;
import com.hospital.service.HospitalFeeService;
import com.hospital.service.dto.HospitalFeeDTO;
import com.hospital.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hospital.domain.HospitalFee}.
 */
@RestController
@RequestMapping("/api/hospital-fees")
public class HospitalFeeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HospitalFeeResource.class);

    private static final String ENTITY_NAME = "hospitalFee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HospitalFeeService hospitalFeeService;

    private final HospitalFeeRepository hospitalFeeRepository;

    public HospitalFeeResource(HospitalFeeService hospitalFeeService, HospitalFeeRepository hospitalFeeRepository) {
        this.hospitalFeeService = hospitalFeeService;
        this.hospitalFeeRepository = hospitalFeeRepository;
    }

    /**
     * {@code POST  /hospital-fees} : Create a new hospitalFee.
     *
     * @param hospitalFeeDTO the hospitalFeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hospitalFeeDTO, or with status {@code 400 (Bad Request)} if the hospitalFee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HospitalFeeDTO> createHospitalFee(@RequestBody HospitalFeeDTO hospitalFeeDTO) throws URISyntaxException {
        LOG.debug("REST request to save HospitalFee : {}", hospitalFeeDTO);
        if (hospitalFeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new hospitalFee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        hospitalFeeDTO = hospitalFeeService.save(hospitalFeeDTO);
        return ResponseEntity.created(new URI("/api/hospital-fees/" + hospitalFeeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, hospitalFeeDTO.getId().toString()))
            .body(hospitalFeeDTO);
    }

    /**
     * {@code PUT  /hospital-fees/:id} : Updates an existing hospitalFee.
     *
     * @param id the id of the hospitalFeeDTO to save.
     * @param hospitalFeeDTO the hospitalFeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hospitalFeeDTO,
     * or with status {@code 400 (Bad Request)} if the hospitalFeeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hospitalFeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HospitalFeeDTO> updateHospitalFee(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HospitalFeeDTO hospitalFeeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update HospitalFee : {}, {}", id, hospitalFeeDTO);
        if (hospitalFeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hospitalFeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hospitalFeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        hospitalFeeDTO = hospitalFeeService.update(hospitalFeeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hospitalFeeDTO.getId().toString()))
            .body(hospitalFeeDTO);
    }

    /**
     * {@code PATCH  /hospital-fees/:id} : Partial updates given fields of an existing hospitalFee, field will ignore if it is null
     *
     * @param id the id of the hospitalFeeDTO to save.
     * @param hospitalFeeDTO the hospitalFeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hospitalFeeDTO,
     * or with status {@code 400 (Bad Request)} if the hospitalFeeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the hospitalFeeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the hospitalFeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HospitalFeeDTO> partialUpdateHospitalFee(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HospitalFeeDTO hospitalFeeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update HospitalFee partially : {}, {}", id, hospitalFeeDTO);
        if (hospitalFeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hospitalFeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hospitalFeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HospitalFeeDTO> result = hospitalFeeService.partialUpdate(hospitalFeeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hospitalFeeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /hospital-fees} : get all the hospitalFees.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of hospitalFees in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HospitalFeeDTO>> getAllHospitalFees(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of HospitalFees");
        Page<HospitalFeeDTO> page;
        if (eagerload) {
            page = hospitalFeeService.findAllWithEagerRelationships(pageable);
        } else {
            page = hospitalFeeService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /hospital-fees/:id} : get the "id" hospitalFee.
     *
     * @param id the id of the hospitalFeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hospitalFeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HospitalFeeDTO> getHospitalFee(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HospitalFee : {}", id);
        Optional<HospitalFeeDTO> hospitalFeeDTO = hospitalFeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(hospitalFeeDTO);
    }

    /**
     * {@code DELETE  /hospital-fees/:id} : delete the "id" hospitalFee.
     *
     * @param id the id of the hospitalFeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospitalFee(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HospitalFee : {}", id);
        hospitalFeeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
