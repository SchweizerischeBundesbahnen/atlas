import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmRecordingObligationComponent } from './prm-recording-obligation.component';
import { AppTestingModule } from '../../app.testing.module';
import { PersonWithReducedMobilityService } from '../../api';
import { EMPTY, of } from 'rxjs';
import { PermissionService } from '../auth/permission/permission.service';
import { adminPermissionServiceMock } from '../../app.testing.mocks';
import {AtlasSlideToggleComponent} from "../form-components/atlas-slide-toggle/atlas-slide-toggle.component";

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
      declarations: [PrmRecordingObligationComponent, AtlasSlideToggleComponent],
      imports: [AppTestingModule],
      providers: [
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
