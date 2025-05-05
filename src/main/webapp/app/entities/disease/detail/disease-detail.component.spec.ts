import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DiseaseDetailComponent } from './disease-detail.component';

describe('Disease Management Detail Component', () => {
  let comp: DiseaseDetailComponent;
  let fixture: ComponentFixture<DiseaseDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiseaseDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./disease-detail.component').then(m => m.DiseaseDetailComponent),
              resolve: { disease: () => of({ id: 23904 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DiseaseDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DiseaseDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load disease on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DiseaseDetailComponent);

      // THEN
      expect(instance.disease()).toEqual(expect.objectContaining({ id: 23904 }));
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
