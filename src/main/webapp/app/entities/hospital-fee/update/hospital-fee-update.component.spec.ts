import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IAppointment } from 'app/entities/appointment/appointment.model';
import { AppointmentService } from 'app/entities/appointment/service/appointment.service';
import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { IHospitalFee } from '../hospital-fee.model';
import { HospitalFeeService } from '../service/hospital-fee.service';
import { HospitalFeeFormService } from './hospital-fee-form.service';

import { HospitalFeeUpdateComponent } from './hospital-fee-update.component';

describe('HospitalFee Management Update Component', () => {
  let comp: HospitalFeeUpdateComponent;
  let fixture: ComponentFixture<HospitalFeeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let hospitalFeeFormService: HospitalFeeFormService;
  let hospitalFeeService: HospitalFeeService;
  let appointmentService: AppointmentService;
  let patientService: PatientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HospitalFeeUpdateComponent],
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
      .overrideTemplate(HospitalFeeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HospitalFeeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    hospitalFeeFormService = TestBed.inject(HospitalFeeFormService);
    hospitalFeeService = TestBed.inject(HospitalFeeService);
    appointmentService = TestBed.inject(AppointmentService);
    patientService = TestBed.inject(PatientService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Appointment query and add missing value', () => {
      const hospitalFee: IHospitalFee = { id: 2554 };
      const appointment: IAppointment = { id: 3011 };
      hospitalFee.appointment = appointment;

      const appointmentCollection: IAppointment[] = [{ id: 3011 }];
      jest.spyOn(appointmentService, 'query').mockReturnValue(of(new HttpResponse({ body: appointmentCollection })));
      const additionalAppointments = [appointment];
      const expectedCollection: IAppointment[] = [...additionalAppointments, ...appointmentCollection];
      jest.spyOn(appointmentService, 'addAppointmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ hospitalFee });
      comp.ngOnInit();

      expect(appointmentService.query).toHaveBeenCalled();
      expect(appointmentService.addAppointmentToCollectionIfMissing).toHaveBeenCalledWith(
        appointmentCollection,
        ...additionalAppointments.map(expect.objectContaining),
      );
      expect(comp.appointmentsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Patient query and add missing value', () => {
      const hospitalFee: IHospitalFee = { id: 2554 };
      const patient: IPatient = { id: 16668 };
      hospitalFee.patient = patient;

      const patientCollection: IPatient[] = [{ id: 16668 }];
      jest.spyOn(patientService, 'query').mockReturnValue(of(new HttpResponse({ body: patientCollection })));
      const additionalPatients = [patient];
      const expectedCollection: IPatient[] = [...additionalPatients, ...patientCollection];
      jest.spyOn(patientService, 'addPatientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ hospitalFee });
      comp.ngOnInit();

      expect(patientService.query).toHaveBeenCalled();
      expect(patientService.addPatientToCollectionIfMissing).toHaveBeenCalledWith(
        patientCollection,
        ...additionalPatients.map(expect.objectContaining),
      );
      expect(comp.patientsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const hospitalFee: IHospitalFee = { id: 2554 };
      const appointment: IAppointment = { id: 3011 };
      hospitalFee.appointment = appointment;
      const patient: IPatient = { id: 16668 };
      hospitalFee.patient = patient;

      activatedRoute.data = of({ hospitalFee });
      comp.ngOnInit();

      expect(comp.appointmentsSharedCollection).toContainEqual(appointment);
      expect(comp.patientsSharedCollection).toContainEqual(patient);
      expect(comp.hospitalFee).toEqual(hospitalFee);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHospitalFee>>();
      const hospitalFee = { id: 28434 };
      jest.spyOn(hospitalFeeFormService, 'getHospitalFee').mockReturnValue(hospitalFee);
      jest.spyOn(hospitalFeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hospitalFee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hospitalFee }));
      saveSubject.complete();

      // THEN
      expect(hospitalFeeFormService.getHospitalFee).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(hospitalFeeService.update).toHaveBeenCalledWith(expect.objectContaining(hospitalFee));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHospitalFee>>();
      const hospitalFee = { id: 28434 };
      jest.spyOn(hospitalFeeFormService, 'getHospitalFee').mockReturnValue({ id: null });
      jest.spyOn(hospitalFeeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hospitalFee: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hospitalFee }));
      saveSubject.complete();

      // THEN
      expect(hospitalFeeFormService.getHospitalFee).toHaveBeenCalled();
      expect(hospitalFeeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHospitalFee>>();
      const hospitalFee = { id: 28434 };
      jest.spyOn(hospitalFeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hospitalFee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(hospitalFeeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAppointment', () => {
      it('should forward to appointmentService', () => {
        const entity = { id: 3011 };
        const entity2 = { id: 584 };
        jest.spyOn(appointmentService, 'compareAppointment');
        comp.compareAppointment(entity, entity2);
        expect(appointmentService.compareAppointment).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
