import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AtlasApiService } from '../../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core/auth/user/user.service';
import { StopPointInternalService } from './stop-point-internal.service';
import { RecordingObligation } from '../../../model/recordingObligation';
import { EMPTY } from 'rxjs';

describe('StopPointInternalService', () => {
  let service: StopPointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StopPointInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(StopPointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
  });

  it('should getRecordingObligation', () => {
    service.getRecordingObligation('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/prm-directory/internal/stop-points/recording-obligation/123'
    );
  });

  it('should updateRecordingObligation', () => {
    service.updateRecordingObligation('123', {} as RecordingObligation);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      sloid: '123',
      recordingObligation: {},
    });
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      '/prm-directory/internal/stop-points/recording-obligation/123',
      {}
    );
  });
});
