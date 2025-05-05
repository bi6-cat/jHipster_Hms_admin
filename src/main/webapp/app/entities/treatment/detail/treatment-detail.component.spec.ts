import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TreatmentDetailComponent } from './treatment-detail.component';

describe('Treatment Management Detail Component', () => {
  let comp: TreatmentDetailComponent;
  let fixture: ComponentFixture<TreatmentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TreatmentDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./treatment-detail.component').then(m => m.TreatmentDetailComponent),
              resolve: { treatment: () => of({ id: 32598 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TreatmentDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TreatmentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load treatment on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TreatmentDetailComponent);

      // THEN
      expect(instance.treatment()).toEqual(expect.objectContaining({ id: 32598 }));
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
