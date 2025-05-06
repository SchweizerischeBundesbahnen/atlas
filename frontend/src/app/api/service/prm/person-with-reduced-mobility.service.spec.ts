import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { PersonWithReducedMobilityService } from './person-with-reduced-mobility.service';
import { RecordingObligation } from '../../model/recordingObligation';

describe('PersonWithReducedMobilityService', () => {
  let service: PersonWithReducedMobilityService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PersonWithReducedMobilityService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(PersonWithReducedMobilityService);
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
      '/prm-directory/v1/stop-points/recording-obligation/123',
    );
  });

  it('should updateRecordingObligation', () => {
    service.updateRecordingObligation('123', {} as RecordingObligation);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: '123',
      recordingObligation: {}
    });
    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/prm-directory/v1/stop-points/recording-obligation/123',
      {}
    );
  });
});
