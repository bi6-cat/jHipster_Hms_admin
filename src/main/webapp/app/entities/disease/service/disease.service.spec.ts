import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDisease } from '../disease.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../disease.test-samples';

import { DiseaseService, RestDisease } from './disease.service';

const requireRestSample: RestDisease = {
  ...sampleWithRequiredData,
  diagnosisDate: sampleWithRequiredData.diagnosisDate?.format(DATE_FORMAT),
};

describe('Disease Service', () => {
  let service: DiseaseService;
  let httpMock: HttpTestingController;
  let expectedResult: IDisease | IDisease[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DiseaseService);
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

    it('should create a Disease', () => {
      const disease = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(disease).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Disease', () => {
      const disease = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(disease).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Disease', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Disease', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Disease', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDiseaseToCollectionIfMissing', () => {
      it('should add a Disease to an empty array', () => {
        const disease: IDisease = sampleWithRequiredData;
        expectedResult = service.addDiseaseToCollectionIfMissing([], disease);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(disease);
      });

      it('should not add a Disease to an array that contains it', () => {
        const disease: IDisease = sampleWithRequiredData;
        const diseaseCollection: IDisease[] = [
          {
            ...disease,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDiseaseToCollectionIfMissing(diseaseCollection, disease);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Disease to an array that doesn't contain it", () => {
        const disease: IDisease = sampleWithRequiredData;
        const diseaseCollection: IDisease[] = [sampleWithPartialData];
        expectedResult = service.addDiseaseToCollectionIfMissing(diseaseCollection, disease);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(disease);
      });

      it('should add only unique Disease to an array', () => {
        const diseaseArray: IDisease[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const diseaseCollection: IDisease[] = [sampleWithRequiredData];
        expectedResult = service.addDiseaseToCollectionIfMissing(diseaseCollection, ...diseaseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const disease: IDisease = sampleWithRequiredData;
        const disease2: IDisease = sampleWithPartialData;
        expectedResult = service.addDiseaseToCollectionIfMissing([], disease, disease2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(disease);
        expect(expectedResult).toContain(disease2);
      });

      it('should accept null and undefined values', () => {
        const disease: IDisease = sampleWithRequiredData;
        expectedResult = service.addDiseaseToCollectionIfMissing([], null, disease, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(disease);
      });

      it('should return initial array if no Disease is added', () => {
        const diseaseCollection: IDisease[] = [sampleWithRequiredData];
        expectedResult = service.addDiseaseToCollectionIfMissing(diseaseCollection, undefined, null);
        expect(expectedResult).toEqual(diseaseCollection);
      });
    });

    describe('compareDisease', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDisease(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23904 };
        const entity2 = null;

        const compareResult1 = service.compareDisease(entity1, entity2);
        const compareResult2 = service.compareDisease(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23904 };
        const entity2 = { id: 25050 };

        const compareResult1 = service.compareDisease(entity1, entity2);
        const compareResult2 = service.compareDisease(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23904 };
        const entity2 = { id: 23904 };

        const compareResult1 = service.compareDisease(entity1, entity2);
        const compareResult2 = service.compareDisease(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
