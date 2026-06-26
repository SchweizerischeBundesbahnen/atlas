import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableHearingStatementInternalService } from './timetable-hearing-statement-internal.service';
import { UpdateHearingStatementStatus } from '../../model/updateHearingStatementStatus';
import { UpdateHearingCanton } from '../../model/updateHearingCanton';
import { TimetableHearingStatementV2 } from '../../model/timetableHearingStatementV2';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';
import { TimetableHearingStatementDataProtection } from '../../model/timetableHearingStatementDataProtection';

describe('TimetableHearingStatementInternalService', () => {
  let service: TimetableHearingStatementInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TimetableHearingStatementInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TimetableHearingStatementInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'getBlob').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
  });

  it('should updateHearingStatementStatus', () => {
    service.updateHearingStatementStatus({} as UpdateHearingStatementStatus);

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/update-statement-status',
      {}
    );
  });

  it('should updateHearingCanton', () => {
    service.updateHearingCanton({} as UpdateHearingCanton);

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/update-canton',
      {}
    );
  });

  it('should getStatements', () => {
    service.getStatements(2025);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      timetableHearingYear: 2025,
      statusRestrictions: undefined,
      canton: undefined,
      searchCriterias: undefined,
      ttfnid: undefined,
      transportCompanies: undefined,
      partOfDossier: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements',
      expect.any(HttpParams)
    );
  });

  it('should getStatementsAsCsv', () => {
    service.getStatementsAsCsv('de');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      language: 'de',
    });
    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      timetableHearingYear: undefined,
      canton: undefined,
      searchCriterias: undefined,
      statusRestrictions: undefined,
      ttfnid: undefined,
      transportCompanies: undefined,
      partOfDossier: undefined,
      anonymized: undefined,
    });
    expect(apiService.getBlob).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/csv/de',
      expect.any(HttpParams)
    );
  });

  it('should getStatementsAsCsv with anonymized true', () => {
    service.getStatementsAsCsv('de', undefined, undefined, undefined, undefined, undefined, undefined, undefined, true);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      language: 'de',
    });

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      timetableHearingYear: undefined,
      canton: undefined,
      searchCriterias: undefined,
      statusRestrictions: undefined,
      ttfnid: undefined,
      transportCompanies: undefined,
      partOfDossier: undefined,
      anonymized: true,
    });

    expect(apiService.getBlob).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/csv/de',
      expect.any(HttpParams)
    );
  });

  it('should getStatement', () => {
    service.getStatement(1);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/line-directory/internal/timetable-hearing/statements/1');
  });

  it('should getPreviousStatement', () => {
    service.getPreviousStatement(1);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
    });
    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      timetableHearingYear: undefined,
      canton: undefined,
      searchCriterias: undefined,
      statusRestrictions: undefined,
      ttfnid: undefined,
      transportCompanies: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1/previous',
      expect.any(HttpParams)
    );
  });

  it('should getNextStatement', () => {
    service.getNextStatement(1);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
    });
    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      timetableHearingYear: undefined,
      canton: undefined,
      searchCriterias: undefined,
      statusRestrictions: undefined,
      ttfnid: undefined,
      transportCompanies: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1/next',
      expect.any(HttpParams)
    );
  });

  it('should getStatementDocument', () => {
    service.getStatementDocument(1, 'file');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      id: 1,
      filename: 'file',
    });
    expect(apiService.getBlob).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1/documents/file'
    );
  });

  it('should createStatement', () => {
    service.createStatement({} as TimetableHearingStatementV2, [
      new Blob([JSON.stringify({})], { type: 'application/json' }),
    ]);

    const formData = new FormData();
    formData.append('statement', new Blob([JSON.stringify({})], { type: 'application/json' }));
    formData.append('documents', new Blob([JSON.stringify({})], { type: 'application/json' }));

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      statement: {},
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements',
      formData,
      { responseType: 'json' }
    );
  });

  it('should updateHearingStatement', () => {
    service.updateHearingStatement(1, {} as TimetableHearingStatementV2, [
      new Blob([JSON.stringify({})], { type: 'application/json' }),
    ]);

    const formData = new FormData();
    formData.append('statement', new Blob([JSON.stringify({})], { type: 'application/json' }));
    formData.append('documents', new Blob([JSON.stringify({})], { type: 'application/json' }));

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      statement: {},
      id: 1,
    });
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1',
      formData,
      { responseType: 'json' }
    );
  });

  it('should getResponsibleTransportCompanies', () => {
    service.getResponsibleTransportCompanies('123', 2025);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      year: 2025,
      ttfnid: '123',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/responsible-transport-companies/123/2025'
    );
  });

  it('should checkDataProtection', () => {
    const dataProtectionParam = {} as TimetableHearingStatementDataProtection;
    service.checkDataProtection(dataProtectionParam);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      dataProtection: dataProtectionParam,
    });
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(
      '/line-directory/internal/timetable-hearing/statements/check-data-protection',
      dataProtectionParam
    );
  });
});
