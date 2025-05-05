import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../treatment.test-samples';

import { TreatmentFormService } from './treatment-form.service';

describe('Treatment Form Service', () => {
  let service: TreatmentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TreatmentFormService);
  });

  describe('Service methods', () => {
    describe('createTreatmentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTreatmentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            treatmentDescription: expect.any(Object),
            treatmentDate: expect.any(Object),
            patient: expect.any(Object),
            doctor: expect.any(Object),
            disease: expect.any(Object),
          }),
        );
      });

      it('passing ITreatment should create a new form with FormGroup', () => {
        const formGroup = service.createTreatmentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            treatmentDescription: expect.any(Object),
            treatmentDate: expect.any(Object),
            patient: expect.any(Object),
            doctor: expect.any(Object),
            disease: expect.any(Object),
          }),
        );
      });
    });

    describe('getTreatment', () => {
      it('should return NewTreatment for default Treatment initial value', () => {
        const formGroup = service.createTreatmentFormGroup(sampleWithNewData);

        const treatment = service.getTreatment(formGroup) as any;

        expect(treatment).toMatchObject(sampleWithNewData);
      });

      it('should return NewTreatment for empty Treatment initial value', () => {
        const formGroup = service.createTreatmentFormGroup();

        const treatment = service.getTreatment(formGroup) as any;

        expect(treatment).toMatchObject({});
      });

      it('should return ITreatment', () => {
        const formGroup = service.createTreatmentFormGroup(sampleWithRequiredData);

        const treatment = service.getTreatment(formGroup) as any;

        expect(treatment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITreatment should not enable id FormControl', () => {
        const formGroup = service.createTreatmentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTreatment should disable id FormControl', () => {
        const formGroup = service.createTreatmentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
