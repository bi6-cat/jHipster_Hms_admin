import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IAppointment } from 'app/entities/appointment/appointment.model';
import { AppointmentService } from 'app/entities/appointment/service/appointment.service';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { IPrescription } from '../prescription.model';
import { PrescriptionService } from '../service/prescription.service';
import { PrescriptionFormService } from './prescription-form.service';

import { PrescriptionUpdateComponent } from './prescription-update.component';

describe('Prescription Management Update Component', () => {
  let comp: PrescriptionUpdateComponent;
  let fixture: ComponentFixture<PrescriptionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let prescriptionFormService: PrescriptionFormService;
  let prescriptionService: PrescriptionService;
  let appointmentService: AppointmentService;
  let doctorService: DoctorService;
  let patientService: PatientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PrescriptionUpdateComponent],
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
      .overrideTemplate(PrescriptionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PrescriptionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    prescriptionFormService = TestBed.inject(PrescriptionFormService);
    prescriptionService = TestBed.inject(PrescriptionService);
    appointmentService = TestBed.inject(AppointmentService);
    doctorService = TestBed.inject(DoctorService);
    patientService = TestBed.inject(PatientService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Appointment query and add missing value', () => {
      const prescription: IPrescription = { id: 9334 };
      const appointment: IAppointment = { id: 3011 };
      prescription.appointment = appointment;

      const appointmentCollection: IAppointment[] = [{ id: 3011 }];
      jest.spyOn(appointmentService, 'query').mockReturnValue(of(new HttpResponse({ body: appointmentCollection })));
      const additionalAppointments = [appointment];
      const expectedCollection: IAppointment[] = [...additionalAppointments, ...appointmentCollection];
      jest.spyOn(appointmentService, 'addAppointmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ prescription });
      comp.ngOnInit();

      expect(appointmentService.query).toHaveBeenCalled();
      expect(appointmentService.addAppointmentToCollectionIfMissing).toHaveBeenCalledWith(
        appointmentCollection,
        ...additionalAppointments.map(expect.objectContaining),
      );
      expect(comp.appointmentsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Doctor query and add missing value', () => {
      const prescription: IPrescription = { id: 9334 };
      const doctor: IDoctor = { id: 758 };
      prescription.doctor = doctor;

      const doctorCollection: IDoctor[] = [{ id: 758 }];
      jest.spyOn(doctorService, 'query').mockReturnValue(of(new HttpResponse({ body: doctorCollection })));
      const additionalDoctors = [doctor];
      const expectedCollection: IDoctor[] = [...additionalDoctors, ...doctorCollection];
      jest.spyOn(doctorService, 'addDoctorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ prescription });
      comp.ngOnInit();

      expect(doctorService.query).toHaveBeenCalled();
      expect(doctorService.addDoctorToCollectionIfMissing).toHaveBeenCalledWith(
        doctorCollection,
        ...additionalDoctors.map(expect.objectContaining),
      );
      expect(comp.doctorsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Patient query and add missing value', () => {
      const prescription: IPrescription = { id: 9334 };
      const patient: IPatient = { id: 16668 };
      prescription.patient = patient;

      const patientCollection: IPatient[] = [{ id: 16668 }];
      jest.spyOn(patientService, 'query').mockReturnValue(of(new HttpResponse({ body: patientCollection })));
      const additionalPatients = [patient];
      const expectedCollection: IPatient[] = [...additionalPatients, ...patientCollection];
      jest.spyOn(patientService, 'addPatientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ prescription });
      comp.ngOnInit();

      expect(patientService.query).toHaveBeenCalled();
      expect(patientService.addPatientToCollectionIfMissing).toHaveBeenCalledWith(
        patientCollection,
        ...additionalPatients.map(expect.objectContaining),
      );
      expect(comp.patientsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const prescription: IPrescription = { id: 9334 };
      const appointment: IAppointment = { id: 3011 };
      prescription.appointment = appointment;
      const doctor: IDoctor = { id: 758 };
      prescription.doctor = doctor;
      const patient: IPatient = { id: 16668 };
      prescription.patient = patient;

      activatedRoute.data = of({ prescription });
      comp.ngOnInit();

      expect(comp.appointmentsSharedCollection).toContainEqual(appointment);
      expect(comp.doctorsSharedCollection).toContainEqual(doctor);
      expect(comp.patientsSharedCollection).toContainEqual(patient);
      expect(comp.prescription).toEqual(prescription);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrescription>>();
      const prescription = { id: 6642 };
      jest.spyOn(prescriptionFormService, 'getPrescription').mockReturnValue(prescription);
      jest.spyOn(prescriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prescription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: prescription }));
      saveSubject.complete();

      // THEN
      expect(prescriptionFormService.getPrescription).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(prescriptionService.update).toHaveBeenCalledWith(expect.objectContaining(prescription));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrescription>>();
      const prescription = { id: 6642 };
      jest.spyOn(prescriptionFormService, 'getPrescription').mockReturnValue({ id: null });
      jest.spyOn(prescriptionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prescription: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: prescription }));
      saveSubject.complete();

      // THEN
      expect(prescriptionFormService.getPrescription).toHaveBeenCalled();
      expect(prescriptionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrescription>>();
      const prescription = { id: 6642 };
      jest.spyOn(prescriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prescription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(prescriptionService.update).toHaveBeenCalled();
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

    describe('compareDoctor', () => {
      it('should forward to doctorService', () => {
        const entity = { id: 758 };
        const entity2 = { id: 23078 };
        jest.spyOn(doctorService, 'compareDoctor');
        comp.compareDoctor(entity, entity2);
        expect(doctorService.compareDoctor).toHaveBeenCalledWith(entity, entity2);
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
