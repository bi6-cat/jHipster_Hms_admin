import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IVitalSign } from '../vital-sign.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../vital-sign.test-samples';

import { RestVitalSign, VitalSignService } from './vital-sign.service';

const requireRestSample: RestVitalSign = {
  ...sampleWithRequiredData,
  measurementDate: sampleWithRequiredData.measurementDate?.format(DATE_FORMAT),
};

describe('VitalSign Service', () => {
  let service: VitalSignService;
  let httpMock: HttpTestingController;
  let expectedResult: IVitalSign | IVitalSign[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(VitalSignService);
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

    it('should create a VitalSign', () => {
      const vitalSign = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(vitalSign).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a VitalSign', () => {
      const vitalSign = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(vitalSign).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a VitalSign', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of VitalSign', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a VitalSign', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addVitalSignToCollectionIfMissing', () => {
      it('should add a VitalSign to an empty array', () => {
        const vitalSign: IVitalSign = sampleWithRequiredData;
        expectedResult = service.addVitalSignToCollectionIfMissing([], vitalSign);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(vitalSign);
      });

      it('should not add a VitalSign to an array that contains it', () => {
        const vitalSign: IVitalSign = sampleWithRequiredData;
        const vitalSignCollection: IVitalSign[] = [
          {
            ...vitalSign,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addVitalSignToCollectionIfMissing(vitalSignCollection, vitalSign);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a VitalSign to an array that doesn't contain it", () => {
        const vitalSign: IVitalSign = sampleWithRequiredData;
        const vitalSignCollection: IVitalSign[] = [sampleWithPartialData];
        expectedResult = service.addVitalSignToCollectionIfMissing(vitalSignCollection, vitalSign);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(vitalSign);
      });

      it('should add only unique VitalSign to an array', () => {
        const vitalSignArray: IVitalSign[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const vitalSignCollection: IVitalSign[] = [sampleWithRequiredData];
        expectedResult = service.addVitalSignToCollectionIfMissing(vitalSignCollection, ...vitalSignArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const vitalSign: IVitalSign = sampleWithRequiredData;
        const vitalSign2: IVitalSign = sampleWithPartialData;
        expectedResult = service.addVitalSignToCollectionIfMissing([], vitalSign, vitalSign2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(vitalSign);
        expect(expectedResult).toContain(vitalSign2);
      });

      it('should accept null and undefined values', () => {
        const vitalSign: IVitalSign = sampleWithRequiredData;
        expectedResult = service.addVitalSignToCollectionIfMissing([], null, vitalSign, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(vitalSign);
      });

      it('should return initial array if no VitalSign is added', () => {
        const vitalSignCollection: IVitalSign[] = [sampleWithRequiredData];
        expectedResult = service.addVitalSignToCollectionIfMissing(vitalSignCollection, undefined, null);
        expect(expectedResult).toEqual(vitalSignCollection);
      });
    });

    describe('compareVitalSign', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareVitalSign(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 21174 };
        const entity2 = null;

        const compareResult1 = service.compareVitalSign(entity1, entity2);
        const compareResult2 = service.compareVitalSign(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 21174 };
        const entity2 = { id: 2346 };

        const compareResult1 = service.compareVitalSign(entity1, entity2);
        const compareResult2 = service.compareVitalSign(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 21174 };
        const entity2 = { id: 21174 };

        const compareResult1 = service.compareVitalSign(entity1, entity2);
        const compareResult2 = service.compareVitalSign(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
