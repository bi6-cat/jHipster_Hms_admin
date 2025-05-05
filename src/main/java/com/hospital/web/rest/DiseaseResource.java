package com.hospital.web.rest;

import com.hospital.repository.DiseaseRepository;
import com.hospital.service.DiseaseService;
import com.hospital.service.dto.DiseaseDTO;
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
 * REST controller for managing {@link com.hospital.domain.Disease}.
 */
@RestController
@RequestMapping("/api/diseases")
public class DiseaseResource {

    private static final Logger LOG = LoggerFactory.getLogger(DiseaseResource.class);

    private static final String ENTITY_NAME = "disease";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DiseaseService diseaseService;

    private final DiseaseRepository diseaseRepository;

    public DiseaseResource(DiseaseService diseaseService, DiseaseRepository diseaseRepository) {
        this.diseaseService = diseaseService;
        this.diseaseRepository = diseaseRepository;
    }

    /**
     * {@code POST  /diseases} : Create a new disease.
     *
     * @param diseaseDTO the diseaseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new diseaseDTO, or with status {@code 400 (Bad Request)} if the disease has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DiseaseDTO> createDisease(@RequestBody DiseaseDTO diseaseDTO) throws URISyntaxException {
        LOG.debug("REST request to save Disease : {}", diseaseDTO);
        if (diseaseDTO.getId() != null) {
            throw new BadRequestAlertException("A new disease cannot already have an ID", ENTITY_NAME, "idexists");
        }
        diseaseDTO = diseaseService.save(diseaseDTO);
        return ResponseEntity.created(new URI("/api/diseases/" + diseaseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, diseaseDTO.getId().toString()))
            .body(diseaseDTO);
    }

    /**
     * {@code PUT  /diseases/:id} : Updates an existing disease.
     *
     * @param id the id of the diseaseDTO to save.
     * @param diseaseDTO the diseaseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diseaseDTO,
     * or with status {@code 400 (Bad Request)} if the diseaseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the diseaseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiseaseDTO> updateDisease(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DiseaseDTO diseaseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Disease : {}, {}", id, diseaseDTO);
        if (diseaseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, diseaseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!diseaseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        diseaseDTO = diseaseService.update(diseaseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, diseaseDTO.getId().toString()))
            .body(diseaseDTO);
    }

    /**
     * {@code PATCH  /diseases/:id} : Partial updates given fields of an existing disease, field will ignore if it is null
     *
     * @param id the id of the diseaseDTO to save.
     * @param diseaseDTO the diseaseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diseaseDTO,
     * or with status {@code 400 (Bad Request)} if the diseaseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the diseaseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the diseaseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DiseaseDTO> partialUpdateDisease(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DiseaseDTO diseaseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Disease partially : {}, {}", id, diseaseDTO);
        if (diseaseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, diseaseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!diseaseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DiseaseDTO> result = diseaseService.partialUpdate(diseaseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, diseaseDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /diseases} : get all the diseases.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of diseases in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DiseaseDTO>> getAllDiseases(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Diseases");
        Page<DiseaseDTO> page;
        if (eagerload) {
            page = diseaseService.findAllWithEagerRelationships(pageable);
        } else {
            page = diseaseService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /diseases/:id} : get the "id" disease.
     *
     * @param id the id of the diseaseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the diseaseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiseaseDTO> getDisease(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Disease : {}", id);
        Optional<DiseaseDTO> diseaseDTO = diseaseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(diseaseDTO);
    }

    /**
     * {@code DELETE  /diseases/:id} : delete the "id" disease.
     *
     * @param id the id of the diseaseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisease(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Disease : {}", id);
        diseaseService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
