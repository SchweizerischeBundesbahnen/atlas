import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PrmRecordingObligationComponent } from './prm-recording-obligation.component';
import { StopPointInternalService } from '../../api/service/prm/stop-point/stop-point-internal.service';
import { EMPTY, of } from 'rxjs';
import { PermissionService } from '../auth/permission/permission.service';
import { adminPermissionServiceMock, translateServiceProvider } from '../../app.testing.mocks';

describe('PrmRecordingObligationComponent', () => {
  type StopPointInternalServiceMock = Mocked<
    Pick<StopPointInternalService, 'getRecordingObligation' | 'updateRecordingObligation'>
  >;

  let component: PrmRecordingObligationComponent;
  let fixture: ComponentFixture<PrmRecordingObligationComponent>;
  let stopPointInternalServiceMock: StopPointInternalServiceMock;

  beforeEach(() => {
    stopPointInternalServiceMock = {
      getRecordingObligation: vi.fn().mockReturnValue(of({ value: true })),
      updateRecordingObligation: vi.fn().mockReturnValue(of(EMPTY)),
    };

    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        {
          provide: StopPointInternalService,
          useValue: stopPointInternalServiceMock,
        },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
      ],
    });

    fixture = TestBed.createComponent(PrmRecordingObligationComponent);
    component = fixture.componentInstance;
  });

  it('should create and init', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(component.recordingObligation).toBe(true);
    expect(stopPointInternalServiceMock.getRecordingObligation).toHaveBeenCalledExactlyOnceWith(undefined);
  });

  it('should toggle recording obligation', () => {
    expect(component.recordingObligation).toBe(true);

    component.toggleRecordingObligation();
    expect(component.recordingObligation).toBe(false);
  });
});
