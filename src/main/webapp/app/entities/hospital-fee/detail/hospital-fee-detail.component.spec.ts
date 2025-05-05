import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { HospitalFeeDetailComponent } from './hospital-fee-detail.component';

describe('HospitalFee Management Detail Component', () => {
  let comp: HospitalFeeDetailComponent;
  let fixture: ComponentFixture<HospitalFeeDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HospitalFeeDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./hospital-fee-detail.component').then(m => m.HospitalFeeDetailComponent),
              resolve: { hospitalFee: () => of({ id: 28434 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(HospitalFeeDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HospitalFeeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load hospitalFee on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', HospitalFeeDetailComponent);

      // THEN
      expect(instance.hospitalFee()).toEqual(expect.objectContaining({ id: 28434 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
