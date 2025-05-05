import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { VitalSignService } from '../service/vital-sign.service';
import { IVitalSign } from '../vital-sign.model';
import { VitalSignFormService } from './vital-sign-form.service';

import { VitalSignUpdateComponent } from './vital-sign-update.component';

describe('VitalSign Management Update Component', () => {
  let comp: VitalSignUpdateComponent;
  let fixture: ComponentFixture<VitalSignUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let vitalSignFormService: VitalSignFormService;
  let vitalSignService: VitalSignService;
  let patientService: PatientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VitalSignUpdateComponent],
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
      .overrideTemplate(VitalSignUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VitalSignUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    vitalSignFormService = TestBed.inject(VitalSignFormService);
    vitalSignService = TestBed.inject(VitalSignService);
    patientService = TestBed.inject(PatientService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Patient query and add missing value', () => {
      const vitalSign: IVitalSign = { id: 2346 };
      const patient: IPatient = { id: 16668 };
      vitalSign.patient = patient;

      const patientCollection: IPatient[] = [{ id: 16668 }];
      jest.spyOn(patientService, 'query').mockReturnValue(of(new HttpResponse({ body: patientCollection })));
      const additionalPatients = [patient];
      const expectedCollection: IPatient[] = [...additionalPatients, ...patientCollection];
      jest.spyOn(patientService, 'addPatientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ vitalSign });
      comp.ngOnInit();

      expect(patientService.query).toHaveBeenCalled();
      expect(patientService.addPatientToCollectionIfMissing).toHaveBeenCalledWith(
        patientCollection,
        ...additionalPatients.map(expect.objectContaining),
      );
      expect(comp.patientsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const vitalSign: IVitalSign = { id: 2346 };
      const patient: IPatient = { id: 16668 };
      vitalSign.patient = patient;

      activatedRoute.data = of({ vitalSign });
      comp.ngOnInit();

      expect(comp.patientsSharedCollection).toContainEqual(patient);
      expect(comp.vitalSign).toEqual(vitalSign);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVitalSign>>();
      const vitalSign = { id: 21174 };
      jest.spyOn(vitalSignFormService, 'getVitalSign').mockReturnValue(vitalSign);
      jest.spyOn(vitalSignService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vitalSign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vitalSign }));
      saveSubject.complete();

      // THEN
      expect(vitalSignFormService.getVitalSign).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(vitalSignService.update).toHaveBeenCalledWith(expect.objectContaining(vitalSign));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVitalSign>>();
      const vitalSign = { id: 21174 };
      jest.spyOn(vitalSignFormService, 'getVitalSign').mockReturnValue({ id: null });
      jest.spyOn(vitalSignService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vitalSign: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vitalSign }));
      saveSubject.complete();

      // THEN
      expect(vitalSignFormService.getVitalSign).toHaveBeenCalled();
      expect(vitalSignService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVitalSign>>();
      const vitalSign = { id: 21174 };
      jest.spyOn(vitalSignService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vitalSign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(vitalSignService.update).toHaveBeenCalled();
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
