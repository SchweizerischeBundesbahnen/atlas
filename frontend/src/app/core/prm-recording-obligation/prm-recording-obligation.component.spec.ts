import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PrmRecordingObligationComponent } from './prm-recording-obligation.component';
import { PersonWithReducedMobilityService } from '../../api/service/prm/person-with-reduced-mobility.service';
import { EMPTY, of } from 'rxjs';
import { PermissionService } from '../auth/permission/permission.service';
import { adminPermissionServiceMock } from '../../app.testing.mocks';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';

describe('PrmRecordingObligationComponent', () => {
  let component: PrmRecordingObligationComponent;
  let fixture: ComponentFixture<PrmRecordingObligationComponent>;

  const personWithReducedMobilityServiceSpy = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getRecordingObligation', 'updateRecordingObligation']
  );
  personWithReducedMobilityServiceSpy.getRecordingObligation.and.returnValue(
    of({ value: true })
  );
  personWithReducedMobilityServiceSpy.updateRecordingObligation.and.returnValue(
    of(EMPTY)
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrmRecordingObligationComponent, TranslateModule.forRoot()],
      providers: [
        TranslatePipe,
        {
          provide: PersonWithReducedMobilityService,
          useValue: personWithReducedMobilityServiceSpy,
        },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PrmRecordingObligationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and init', () => {
    expect(component).toBeTruthy();

    expect(component.recordingObligation).toBeTrue();
    expect(
      personWithReducedMobilityServiceSpy.getRecordingObligation
    ).toHaveBeenCalled();
  });

  it('should toggle recording obligation', () => {
    expect(component.recordingObligation).toBeTrue();

    component.toggleRecordingObligation();
    expect(component.recordingObligation).toBeFalse();
  });
});
