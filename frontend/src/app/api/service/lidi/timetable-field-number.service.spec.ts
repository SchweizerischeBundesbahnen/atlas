import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableFieldNumberService } from './timetable-field-number.service';
import { TimetableFieldNumberVersion } from '../../model/timetableFieldNumberVersion';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';

describe('TimetableFieldNumberService', () => {
  let service: TimetableFieldNumberService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TimetableFieldNumberService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TimetableFieldNumberService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
  });

  it('should createVersion', () => {
    service.createVersion({} as TimetableFieldNumberVersion);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      timetableFieldNumberVersion: {},
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/line-directory/v1/field-numbers/versions', {});
  });

  it('should getAllVersionsVersioned', () => {
    service.getAllVersionsVersioned('123');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      ttfnId: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/v1/field-numbers/versions/123');
  });

  it('should updateVersionWithVersioning', () => {
    service.updateVersionWithVersioning(1, {} as TimetableFieldNumberVersion);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
      timetableFieldNumberVersion: {},
    });
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/line-directory/v1/field-numbers/versions/1', {});
  });
});
