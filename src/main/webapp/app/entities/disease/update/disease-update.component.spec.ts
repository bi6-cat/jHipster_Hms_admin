import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { DiseaseService } from '../service/disease.service';
import { IDisease } from '../disease.model';
import { DiseaseFormService } from './disease-form.service';

import { DiseaseUpdateComponent } from './disease-update.component';

describe('Disease Management Update Component', () => {
  let comp: DiseaseUpdateComponent;
  let fixture: ComponentFixture<DiseaseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let diseaseFormService: DiseaseFormService;
  let diseaseService: DiseaseService;
  let patientService: PatientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DiseaseUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DiseaseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DiseaseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    diseaseFormService = TestBed.inject(DiseaseFormService);
    diseaseService = TestBed.inject(DiseaseService);
    patientService = TestBed.inject(PatientService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Patient query and add missing value', () => {
      const disease: IDisease = { id: 25050 };
      const patient: IPatient = { id: 16668 };
      disease.patient = patient;

      const patientCollection: IPatient[] = [{ id: 16668 }];
      jest.spyOn(patientService, 'query').mockReturnValue(of(new HttpResponse({ body: patientCollection })));
      const additionalPatients = [patient];
      const expectedCollection: IPatient[] = [...additionalPatients, ...patientCollection];
      jest.spyOn(patientService, 'addPatientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ disease });
      comp.ngOnInit();

      expect(patientService.query).toHaveBeenCalled();
      expect(patientService.addPatientToCollectionIfMissing).toHaveBeenCalledWith(
        patientCollection,
        ...additionalPatients.map(expect.objectContaining),
      );
      expect(comp.patientsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const disease: IDisease = { id: 25050 };
      const patient: IPatient = { id: 16668 };
      disease.patient = patient;

      activatedRoute.data = of({ disease });
      comp.ngOnInit();

      expect(comp.patientsSharedCollection).toContainEqual(patient);
      expect(comp.disease).toEqual(disease);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDisease>>();
      const disease = { id: 23904 };
      jest.spyOn(diseaseFormService, 'getDisease').mockReturnValue(disease);
      jest.spyOn(diseaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disease });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: disease }));
      saveSubject.complete();

      // THEN
      expect(diseaseFormService.getDisease).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(diseaseService.update).toHaveBeenCalledWith(expect.objectContaining(disease));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDisease>>();
      const disease = { id: 23904 };
      jest.spyOn(diseaseFormService, 'getDisease').mockReturnValue({ id: null });
      jest.spyOn(diseaseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disease: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: disease }));
      saveSubject.complete();

      // THEN
      expect(diseaseFormService.getDisease).toHaveBeenCalled();
      expect(diseaseService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDisease>>();
      const disease = { id: 23904 };
      jest.spyOn(diseaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disease });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(diseaseService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePatient', () => {
      it('should forward to patientService', () => {
        const entity = { id: 16668 };
        const entity2 = { id: 16914 };
        jest.spyOn(patientService, 'comparePatient');
        comp.comparePatient(entity, entity2);
        expect(patientService.comparePatient).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
