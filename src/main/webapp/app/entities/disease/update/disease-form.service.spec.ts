import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../disease.test-samples';

import { DiseaseFormService } from './disease-form.service';

describe('Disease Form Service', () => {
  let service: DiseaseFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DiseaseFormService);
  });

  describe('Service methods', () => {
    describe('createDiseaseFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDiseaseFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            diseaseName: expect.any(Object),
            diagnosisDate: expect.any(Object),
            patient: expect.any(Object),
          }),
        );
      });

      it('passing IDisease should create a new form with FormGroup', () => {
        const formGroup = service.createDiseaseFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            diseaseName: expect.any(Object),
            diagnosisDate: expect.any(Object),
            patient: expect.any(Object),
          }),
        );
      });
    });

    describe('getDisease', () => {
      it('should return NewDisease for default Disease initial value', () => {
        const formGroup = service.createDiseaseFormGroup(sampleWithNewData);

        const disease = service.getDisease(formGroup) as any;

        expect(disease).toMatchObject(sampleWithNewData);
      });

      it('should return NewDisease for empty Disease initial value', () => {
        const formGroup = service.createDiseaseFormGroup();

        const disease = service.getDisease(formGroup) as any;

        expect(disease).toMatchObject({});
      });

      it('should return IDisease', () => {
        const formGroup = service.createDiseaseFormGroup(sampleWithRequiredData);

        const disease = service.getDisease(formGroup) as any;

        expect(disease).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDisease should not enable id FormControl', () => {
        const formGroup = service.createDiseaseFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDisease should disable id FormControl', () => {
        const formGroup = service.createDiseaseFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
