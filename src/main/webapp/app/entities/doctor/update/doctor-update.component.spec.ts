import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { DoctorService } from '../service/doctor.service';
import { IDoctor } from '../doctor.model';
import { DoctorFormService } from './doctor-form.service';

import { DoctorUpdateComponent } from './doctor-update.component';

describe('Doctor Management Update Component', () => {
  let comp: DoctorUpdateComponent;
  let fixture: ComponentFixture<DoctorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let doctorFormService: DoctorFormService;
  let doctorService: DoctorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DoctorUpdateComponent],
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
      .overrideTemplate(DoctorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DoctorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    doctorFormService = TestBed.inject(DoctorFormService);
    doctorService = TestBed.inject(DoctorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const doctor: IDoctor = { id: 23078 };

      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      expect(comp.doctor).toEqual(doctor);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 758 };
      jest.spyOn(doctorFormService, 'getDoctor').mockReturnValue(doctor);
      jest.spyOn(doctorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctor }));
      saveSubject.complete();

      // THEN
      expect(doctorFormService.getDoctor).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(doctorService.update).toHaveBeenCalledWith(expect.objectContaining(doctor));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 758 };
      jest.spyOn(doctorFormService, 'getDoctor').mockReturnValue({ id: null });
      jest.spyOn(doctorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctor }));
      saveSubject.complete();

      // THEN
      expect(doctorFormService.getDoctor).toHaveBeenCalled();
      expect(doctorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 758 };
      jest.spyOn(doctorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(doctorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
