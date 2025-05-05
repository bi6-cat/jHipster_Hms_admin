package com.hospital.web.rest;

import com.hospital.repository.PrescriptionRepository;
import com.hospital.service.PrescriptionService;
import com.hospital.service.dto.PrescriptionDTO;
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
 * REST controller for managing {@link com.hospital.domain.Prescription}.
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PrescriptionResource.class);

    private static final String ENTITY_NAME = "prescription";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrescriptionService prescriptionService;

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionResource(PrescriptionService prescriptionService, PrescriptionRepository prescriptionRepository) {
        this.prescriptionService = prescriptionService;
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * {@code POST  /prescriptions} : Create a new prescription.
     *
     * @param prescriptionDTO the prescriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prescriptionDTO, or with status {@code 400 (Bad Request)} if the prescription has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PrescriptionDTO> createPrescription(@RequestBody PrescriptionDTO prescriptionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Prescription : {}", prescriptionDTO);
        if (prescriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new prescription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        prescriptionDTO = prescriptionService.save(prescriptionDTO);
        return ResponseEntity.created(new URI("/api/prescriptions/" + prescriptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, prescriptionDTO.getId().toString()))
            .body(prescriptionDTO);
    }

    /**
     * {@code PUT  /prescriptions/:id} : Updates an existing prescription.
     *
     * @param id the id of the prescriptionDTO to save.
     * @param prescriptionDTO the prescriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prescriptionDTO,
     * or with status {@code 400 (Bad Request)} if the prescriptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prescriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> updatePrescription(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PrescriptionDTO prescriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Prescription : {}, {}", id, prescriptionDTO);
        if (prescriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, prescriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!prescriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        prescriptionDTO = prescriptionService.update(prescriptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, prescriptionDTO.getId().toString()))
            .body(prescriptionDTO);
    }

    /**
     * {@code PATCH  /prescriptions/:id} : Partial updates given fields of an existing prescription, field will ignore if it is null
     *
     * @param id the id of the prescriptionDTO to save.
     * @param prescriptionDTO the prescriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prescriptionDTO,
     * or with status {@code 400 (Bad Request)} if the prescriptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the prescriptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the prescriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PrescriptionDTO> partialUpdatePrescription(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PrescriptionDTO prescriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Prescription partially : {}, {}", id, prescriptionDTO);
        if (prescriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, prescriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!prescriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PrescriptionDTO> result = prescriptionService.partialUpdate(prescriptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, prescriptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /prescriptions} : get all the prescriptions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prescriptions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PrescriptionDTO>> getAllPrescriptions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Prescriptions");
        Page<PrescriptionDTO> page;
        if (eagerload) {
            page = prescriptionService.findAllWithEagerRelationships(pageable);
        } else {
            page = prescriptionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /prescriptions/:id} : get the "id" prescription.
     *
     * @param id the id of the prescriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prescriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> getPrescription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Prescription : {}", id);
        Optional<PrescriptionDTO> prescriptionDTO = prescriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(prescriptionDTO);
    }

    /**
     * {@code DELETE  /prescriptions/:id} : delete the "id" prescription.
     *
     * @param id the id of the prescriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Prescription : {}", id);
        prescriptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
