import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../hospital-fee.test-samples';

import { HospitalFeeFormService } from './hospital-fee-form.service';

describe('HospitalFee Form Service', () => {
  let service: HospitalFeeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HospitalFeeFormService);
  });

  describe('Service methods', () => {
    describe('createHospitalFeeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHospitalFeeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            serviceType: expect.any(Object),
            description: expect.any(Object),
            amount: expect.any(Object),
            feeDate: expect.any(Object),
            phone: expect.any(Object),
            appointment: expect.any(Object),
            patient: expect.any(Object),
          }),
        );
      });

      it('passing IHospitalFee should create a new form with FormGroup', () => {
        const formGroup = service.createHospitalFeeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            serviceType: expect.any(Object),
            description: expect.any(Object),
            amount: expect.any(Object),
            feeDate: expect.any(Object),
            phone: expect.any(Object),
            appointment: expect.any(Object),
            patient: expect.any(Object),
          }),
        );
      });
    });

    describe('getHospitalFee', () => {
      it('should return NewHospitalFee for default HospitalFee initial value', () => {
        const formGroup = service.createHospitalFeeFormGroup(sampleWithNewData);

        const hospitalFee = service.getHospitalFee(formGroup) as any;

        expect(hospitalFee).toMatchObject(sampleWithNewData);
      });

      it('should return NewHospitalFee for empty HospitalFee initial value', () => {
        const formGroup = service.createHospitalFeeFormGroup();

        const hospitalFee = service.getHospitalFee(formGroup) as any;

        expect(hospitalFee).toMatchObject({});
      });

      it('should return IHospitalFee', () => {
        const formGroup = service.createHospitalFeeFormGroup(sampleWithRequiredData);

        const hospitalFee = service.getHospitalFee(formGroup) as any;

        expect(hospitalFee).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHospitalFee should not enable id FormControl', () => {
        const formGroup = service.createHospitalFeeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHospitalFee should disable id FormControl', () => {
        const formGroup = service.createHospitalFeeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
