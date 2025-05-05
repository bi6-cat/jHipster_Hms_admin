import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { VitalSignDetailComponent } from './vital-sign-detail.component';

describe('VitalSign Management Detail Component', () => {
  let comp: VitalSignDetailComponent;
  let fixture: ComponentFixture<VitalSignDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VitalSignDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./vital-sign-detail.component').then(m => m.VitalSignDetailComponent),
              resolve: { vitalSign: () => of({ id: 21174 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(VitalSignDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VitalSignDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load vitalSign on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', VitalSignDetailComponent);

      // THEN
      expect(instance.vitalSign()).toEqual(expect.objectContaining({ id: 21174 }));
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
