import {TestBed} from '@angular/core/testing';
import {AtlasApiService} from '../../atlas-api.service';
import {HttpClient} from '@angular/common/http';
import {UserService} from '../../../../core/auth/user/user.service';
import {StopPointInternalService} from './stop-point-internal.service';
import {RecordingObligation} from '../../../model/recordingObligation';

describe('StopPointInternalService', () => {
  let service: StopPointInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [StopPointInternalService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(StopPointInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
  });

  it('should getRecordingObligation', () => {
    service.getRecordingObligation('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: '123'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/internal/stop-points/recording-obligation/123',
    );
  });

  it('should updateRecordingObligation', () => {
    service.updateRecordingObligation('123', {} as RecordingObligation);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: '123',
      recordingObligation: {}
    });
    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/prm-directory/internal/stop-points/recording-obligation/123',
      {}
    );
  });
});
