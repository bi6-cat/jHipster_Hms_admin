import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IHospitalFee } from '../hospital-fee.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../hospital-fee.test-samples';

import { HospitalFeeService, RestHospitalFee } from './hospital-fee.service';

const requireRestSample: RestHospitalFee = {
  ...sampleWithRequiredData,
  feeDate: sampleWithRequiredData.feeDate?.format(DATE_FORMAT),
};

describe('HospitalFee Service', () => {
  let service: HospitalFeeService;
  let httpMock: HttpTestingController;
  let expectedResult: IHospitalFee | IHospitalFee[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(HospitalFeeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a HospitalFee', () => {
      const hospitalFee = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(hospitalFee).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a HospitalFee', () => {
      const hospitalFee = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(hospitalFee).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a HospitalFee', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of HospitalFee', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a HospitalFee', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addHospitalFeeToCollectionIfMissing', () => {
      it('should add a HospitalFee to an empty array', () => {
        const hospitalFee: IHospitalFee = sampleWithRequiredData;
        expectedResult = service.addHospitalFeeToCollectionIfMissing([], hospitalFee);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hospitalFee);
      });

      it('should not add a HospitalFee to an array that contains it', () => {
        const hospitalFee: IHospitalFee = sampleWithRequiredData;
        const hospitalFeeCollection: IHospitalFee[] = [
          {
            ...hospitalFee,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHospitalFeeToCollectionIfMissing(hospitalFeeCollection, hospitalFee);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a HospitalFee to an array that doesn't contain it", () => {
        const hospitalFee: IHospitalFee = sampleWithRequiredData;
        const hospitalFeeCollection: IHospitalFee[] = [sampleWithPartialData];
        expectedResult = service.addHospitalFeeToCollectionIfMissing(hospitalFeeCollection, hospitalFee);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hospitalFee);
      });

      it('should add only unique HospitalFee to an array', () => {
        const hospitalFeeArray: IHospitalFee[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const hospitalFeeCollection: IHospitalFee[] = [sampleWithRequiredData];
        expectedResult = service.addHospitalFeeToCollectionIfMissing(hospitalFeeCollection, ...hospitalFeeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const hospitalFee: IHospitalFee = sampleWithRequiredData;
        const hospitalFee2: IHospitalFee = sampleWithPartialData;
        expectedResult = service.addHospitalFeeToCollectionIfMissing([], hospitalFee, hospitalFee2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hospitalFee);
        expect(expectedResult).toContain(hospitalFee2);
      });

      it('should accept null and undefined values', () => {
        const hospitalFee: IHospitalFee = sampleWithRequiredData;
        expectedResult = service.addHospitalFeeToCollectionIfMissing([], null, hospitalFee, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hospitalFee);
      });

      it('should return initial array if no HospitalFee is added', () => {
        const hospitalFeeCollection: IHospitalFee[] = [sampleWithRequiredData];
        expectedResult = service.addHospitalFeeToCollectionIfMissing(hospitalFeeCollection, undefined, null);
        expect(expectedResult).toEqual(hospitalFeeCollection);
      });
    });

    describe('compareHospitalFee', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHospitalFee(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28434 };
        const entity2 = null;

        const compareResult1 = service.compareHospitalFee(entity1, entity2);
        const compareResult2 = service.compareHospitalFee(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28434 };
        const entity2 = { id: 2554 };

        const compareResult1 = service.compareHospitalFee(entity1, entity2);
        const compareResult2 = service.compareHospitalFee(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 28434 };
        const entity2 = { id: 28434 };

        const compareResult1 = service.compareHospitalFee(entity1, entity2);
        const compareResult2 = service.compareHospitalFee(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
