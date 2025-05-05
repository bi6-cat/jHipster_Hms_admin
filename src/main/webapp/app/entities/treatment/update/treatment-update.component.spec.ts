import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { IDisease } from 'app/entities/disease/disease.model';
import { DiseaseService } from 'app/entities/disease/service/disease.service';
import { ITreatment } from '../treatment.model';
import { TreatmentService } from '../service/treatment.service';
import { TreatmentFormService } from './treatment-form.service';

import { TreatmentUpdateComponent } from './treatment-update.component';

describe('Treatment Management Update Component', () => {
  let comp: TreatmentUpdateComponent;
  let fixture: ComponentFixture<TreatmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let treatmentFormService: TreatmentFormService;
  let treatmentService: TreatmentService;
  let patientService: PatientService;
  let doctorService: DoctorService;
  let diseaseService: DiseaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TreatmentUpdateComponent],
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
      .overrideTemplate(TreatmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TreatmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    treatmentFormService = TestBed.inject(TreatmentFormService);
    treatmentService = TestBed.inject(TreatmentService);
    patientService = TestBed.inject(PatientService);
    doctorService = TestBed.inject(DoctorService);
    diseaseService = TestBed.inject(DiseaseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Patient query and add missing value', () => {
      const treatment: ITreatment = { id: 17698 };
      const patient: IPatient = { id: 16668 };
      treatment.patient = patient;

      const patientCollection: IPatient[] = [{ id: 16668 }];
      jest.spyOn(patientService, 'query').mockReturnValue(of(new HttpResponse({ body: patientCollection })));
      const additionalPatients = [patient];
      const expectedCollection: IPatient[] = [...additionalPatients, ...patientCollection];
      jest.spyOn(patientService, 'addPatientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ treatment });
      comp.ngOnInit();

      expect(patientService.query).toHaveBeenCalled();
      expect(patientService.addPatientToCollectionIfMissing).toHaveBeenCalledWith(
        patientCollection,
        ...additionalPatients.map(expect.objectContaining),
      );
      expect(comp.patientsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Doctor query and add missing value', () => {
      const treatment: ITreatment = { id: 17698 };
      const doctor: IDoctor = { id: 758 };
      treatment.doctor = doctor;

      const doctorCollection: IDoctor[] = [{ id: 758 }];
      jest.spyOn(doctorService, 'query').mockReturnValue(of(new HttpResponse({ body: doctorCollection })));
      const additionalDoctors = [doctor];
      const expectedCollection: IDoctor[] = [...additionalDoctors, ...doctorCollection];
      jest.spyOn(doctorService, 'addDoctorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ treatment });
      comp.ngOnInit();

      expect(doctorService.query).toHaveBeenCalled();
      expect(doctorService.addDoctorToCollectionIfMissing).toHaveBeenCalledWith(
        doctorCollection,
        ...additionalDoctors.map(expect.objectContaining),
      );
      expect(comp.doctorsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Disease query and add missing value', () => {
      const treatment: ITreatment = { id: 17698 };
      const disease: IDisease = { id: 23904 };
      treatment.disease = disease;

      const diseaseCollection: IDisease[] = [{ id: 23904 }];
      jest.spyOn(diseaseService, 'query').mockReturnValue(of(new HttpResponse({ body: diseaseCollection })));
      const additionalDiseases = [disease];
      const expectedCollection: IDisease[] = [...additionalDiseases, ...diseaseCollection];
      jest.spyOn(diseaseService, 'addDiseaseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ treatment });
      comp.ngOnInit();

      expect(diseaseService.query).toHaveBeenCalled();
      expect(diseaseService.addDiseaseToCollectionIfMissing).toHaveBeenCalledWith(
        diseaseCollection,
        ...additionalDiseases.map(expect.objectContaining),
      );
      expect(comp.diseasesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const treatment: ITreatment = { id: 17698 };
      const patient: IPatient = { id: 16668 };
      treatment.patient = patient;
      const doctor: IDoctor = { id: 758 };
      treatment.doctor = doctor;
      const disease: IDisease = { id: 23904 };
      treatment.disease = disease;

      activatedRoute.data = of({ treatment });
      comp.ngOnInit();

      expect(comp.patientsSharedCollection).toContainEqual(patient);
      expect(comp.doctorsSharedCollection).toContainEqual(doctor);
      expect(comp.diseasesSharedCollection).toContainEqual(disease);
      expect(comp.treatment).toEqual(treatment);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITreatment>>();
      const treatment = { id: 32598 };
      jest.spyOn(treatmentFormService, 'getTreatment').mockReturnValue(treatment);
      jest.spyOn(treatmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ treatment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: treatment }));
      saveSubject.complete();

      // THEN
      expect(treatmentFormService.getTreatment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(treatmentService.update).toHaveBeenCalledWith(expect.objectContaining(treatment));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITreatment>>();
      const treatment = { id: 32598 };
      jest.spyOn(treatmentFormService, 'getTreatment').mockReturnValue({ id: null });
      jest.spyOn(treatmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ treatment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: treatment }));
      saveSubject.complete();

      // THEN
      expect(treatmentFormService.getTreatment).toHaveBeenCalled();
      expect(treatmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITreatment>>();
      const treatment = { id: 32598 };
      jest.spyOn(treatmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ treatment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(treatmentService.update).toHaveBeenCalled();
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

    describe('compareDoctor', () => {
      it('should forward to doctorService', () => {
        const entity = { id: 758 };
        const entity2 = { id: 23078 };
        jest.spyOn(doctorService, 'compareDoctor');
        comp.compareDoctor(entity, entity2);
        expect(doctorService.compareDoctor).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDisease', () => {
      it('should forward to diseaseService', () => {
        const entity = { id: 23904 };
        const entity2 = { id: 25050 };
        jest.spyOn(diseaseService, 'compareDisease');
        comp.compareDisease(entity, entity2);
        expect(diseaseService.compareDisease).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
