import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../vital-sign.test-samples';

import { VitalSignFormService } from './vital-sign-form.service';

describe('VitalSign Form Service', () => {
  let service: VitalSignFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VitalSignFormService);
  });

  describe('Service methods', () => {
    describe('createVitalSignFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVitalSignFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            measurementDate: expect.any(Object),
            bloodPressure: expect.any(Object),
            heartRate: expect.any(Object),
            respiratoryRate: expect.any(Object),
            temperature: expect.any(Object),
            oxygenSaturation: expect.any(Object),
            bloodSugar: expect.any(Object),
            patient: expect.any(Object),
          }),
        );
      });

      it('passing IVitalSign should create a new form with FormGroup', () => {
        const formGroup = service.createVitalSignFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            measurementDate: expect.any(Object),
            bloodPressure: expect.any(Object),
            heartRate: expect.any(Object),
            respiratoryRate: expect.any(Object),
            temperature: expect.any(Object),
            oxygenSaturation: expect.any(Object),
            bloodSugar: expect.any(Object),
            patient: expect.any(Object),
          }),
        );
      });
    });

    describe('getVitalSign', () => {
      it('should return NewVitalSign for default VitalSign initial value', () => {
        const formGroup = service.createVitalSignFormGroup(sampleWithNewData);

        const vitalSign = service.getVitalSign(formGroup) as any;

        expect(vitalSign).toMatchObject(sampleWithNewData);
      });

      it('should return NewVitalSign for empty VitalSign initial value', () => {
        const formGroup = service.createVitalSignFormGroup();

        const vitalSign = service.getVitalSign(formGroup) as any;

        expect(vitalSign).toMatchObject({});
      });

      it('should return IVitalSign', () => {
        const formGroup = service.createVitalSignFormGroup(sampleWithRequiredData);

        const vitalSign = service.getVitalSign(formGroup) as any;

        expect(vitalSign).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVitalSign should not enable id FormControl', () => {
        const formGroup = service.createVitalSignFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVitalSign should disable id FormControl', () => {
        const formGroup = service.createVitalSignFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
