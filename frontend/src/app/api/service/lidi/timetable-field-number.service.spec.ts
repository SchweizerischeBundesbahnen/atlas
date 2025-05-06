import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableFieldNumberService } from './timetable-field-number.service';
import { TimetableFieldNumberVersion } from '../../model/timetableFieldNumberVersion';

describe('TimetableFieldNumberService', () => {
  let service: TimetableFieldNumberService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TimetableFieldNumberService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(TimetableFieldNumberService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'post');
    spyOn(apiService, 'get');
  });

  it('should createVersion', () => {
    service.createVersion({} as TimetableFieldNumberVersion);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      timetableFieldNumberVersion: {},
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/v1/field-numbers/versions',
      {}
    );
  });

  it('should getAllVersionsVersioned', () => {
    service.getAllVersionsVersioned('123');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      ttfnId: '123',
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/v1/field-numbers/versions/123',
    );
  });

  it('should updateVersionWithVersioning', () => {
    service.updateVersionWithVersioning(1,{} as TimetableFieldNumberVersion);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
      timetableFieldNumberVersion: {},
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/v1/field-numbers/versions/1',
      {}
    );
  });
});
