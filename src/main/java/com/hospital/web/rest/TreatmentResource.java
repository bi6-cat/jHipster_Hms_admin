package com.hospital.web.rest;

import com.hospital.repository.TreatmentRepository;
import com.hospital.service.TreatmentService;
import com.hospital.service.dto.TreatmentDTO;
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
 * REST controller for managing {@link com.hospital.domain.Treatment}.
 */
@RestController
@RequestMapping("/api/treatments")
public class TreatmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TreatmentResource.class);

    private static final String ENTITY_NAME = "treatment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TreatmentService treatmentService;

    private final TreatmentRepository treatmentRepository;

    public TreatmentResource(TreatmentService treatmentService, TreatmentRepository treatmentRepository) {
        this.treatmentService = treatmentService;
        this.treatmentRepository = treatmentRepository;
    }

    /**
     * {@code POST  /treatments} : Create a new treatment.
     *
     * @param treatmentDTO the treatmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new treatmentDTO, or with status {@code 400 (Bad Request)} if the treatment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TreatmentDTO> createTreatment(@RequestBody TreatmentDTO treatmentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Treatment : {}", treatmentDTO);
        if (treatmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new treatment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        treatmentDTO = treatmentService.save(treatmentDTO);
        return ResponseEntity.created(new URI("/api/treatments/" + treatmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, treatmentDTO.getId().toString()))
            .body(treatmentDTO);
    }

    /**
     * {@code PUT  /treatments/:id} : Updates an existing treatment.
     *
     * @param id the id of the treatmentDTO to save.
     * @param treatmentDTO the treatmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated treatmentDTO,
     * or with status {@code 400 (Bad Request)} if the treatmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the treatmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TreatmentDTO> updateTreatment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TreatmentDTO treatmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Treatment : {}, {}", id, treatmentDTO);
        if (treatmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, treatmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!treatmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        treatmentDTO = treatmentService.update(treatmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, treatmentDTO.getId().toString()))
            .body(treatmentDTO);
    }

    /**
     * {@code PATCH  /treatments/:id} : Partial updates given fields of an existing treatment, field will ignore if it is null
     *
     * @param id the id of the treatmentDTO to save.
     * @param treatmentDTO the treatmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated treatmentDTO,
     * or with status {@code 400 (Bad Request)} if the treatmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the treatmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the treatmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TreatmentDTO> partialUpdateTreatment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TreatmentDTO treatmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Treatment partially : {}, {}", id, treatmentDTO);
        if (treatmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, treatmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!treatmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TreatmentDTO> result = treatmentService.partialUpdate(treatmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, treatmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /treatments} : get all the treatments.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of treatments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TreatmentDTO>> getAllTreatments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Treatments");
        Page<TreatmentDTO> page;
        if (eagerload) {
            page = treatmentService.findAllWithEagerRelationships(pageable);
        } else {
            page = treatmentService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /treatments/:id} : get the "id" treatment.
     *
     * @param id the id of the treatmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the treatmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TreatmentDTO> getTreatment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Treatment : {}", id);
        Optional<TreatmentDTO> treatmentDTO = treatmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(treatmentDTO);
    }

    /**
     * {@code DELETE  /treatments/:id} : delete the "id" treatment.
     *
     * @param id the id of the treatmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreatment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Treatment : {}", id);
        treatmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
