import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlasApi.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TimetableHearingStatementInternalService } from './timetable-hearing-statement-internal.service';
import { UpdateHearingStatementStatus } from '../../model/updateHearingStatementStatus';
import { UpdateHearingCanton } from '../../model/updateHearingCanton';
import { TimetableHearingStatementV2 } from '../../model/timetableHearingStatementV2';
import any = jasmine.any;

describe('TimetableHearingStatementInternalService', () => {
  let service: TimetableHearingStatementInternalService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TimetableHearingStatementInternalService, AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(TimetableHearingStatementInternalService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'paramsOf').and.callThrough();
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'put');
    spyOn(apiService, 'get');
    spyOn(apiService, 'getBlob');
    spyOn(apiService, 'post');
  });

  it('should updateHearingStatementStatus', () => {
    service.updateHearingStatementStatus({} as UpdateHearingStatementStatus);

    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/update-statement-status',
      {},
    );
  });

  it('should updateHearingCanton', () => {
    service.updateHearingCanton({} as UpdateHearingCanton);

    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/update-canton',
      {},
    );
  });

  it('should getStatements', () => {
    service.getStatements(2025);

    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
      timetableHearingYear: 2025,
      statusRestrictions: undefined,
      canton: undefined,
      searchCriterias: undefined,
      ttfnid: undefined,
      transportCompanies: undefined,
      page: undefined,
      size: undefined,
      sort: undefined,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements',
      any(HttpParams),
    );
  });

  it('should getStatementsAsCsv', () => {
    service.getStatementsAsCsv('de');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      language: 'de',
    });
    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
      timetableHearingYear: undefined,
      canton: undefined,
      searchCriterias: undefined,
      statusRestrictions: undefined,
      ttfnid: undefined,
      transportCompanies: undefined,
    });
    expect(apiService.getBlob).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/csv/de',
      any(HttpParams),
    );
  });

  it('should getStatement', () => {
    service.getStatement(1);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1',
    );
  });

  it('should getPreviousStatement', () => {
    service.getPreviousStatement(1);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
    });
    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
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
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1/previous',
      any(HttpParams),
    );
  });

  it('should getNextStatement', () => {
    service.getNextStatement(1);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
    });
    expect(apiService.paramsOf).toHaveBeenCalledOnceWith({
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
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1/next',
      any(HttpParams),
    );
  });

  it('should getStatementDocument', () => {
    service.getStatementDocument(1, 'file');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      id: 1,
      filename: 'file',
    });
    expect(apiService.getBlob).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1/documents/file',
    );
  });

  it('should createStatement', () => {
    service.createStatement(
      {} as TimetableHearingStatementV2,
      [new Blob([JSON.stringify({})], { type: 'application/json' })]);

    const formData = new FormData();
    formData.append('statement', new Blob([JSON.stringify({})], { type: 'application/json' }));
    formData.append('documents', new Blob([JSON.stringify({})], { type: 'application/json' }));

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      statement: {},
    });
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements',
      formData,
      { responseType: 'json' },
    );
  });

  it('should updateHearingStatement', () => {
    service.updateHearingStatement(1,
      {} as TimetableHearingStatementV2,
      [new Blob([JSON.stringify({})], { type: 'application/json' })]);

    const formData = new FormData();
    formData.append('statement', new Blob([JSON.stringify({})], { type: 'application/json' }));
    formData.append('documents', new Blob([JSON.stringify({})], { type: 'application/json' }));

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      statement: {},
      id: 1,
    });
    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/1',
      formData,
      { responseType: 'json' },
    );
  });

  it('should getResponsibleTransportCompanies', () => {
    service.getResponsibleTransportCompanies('123', 2025);

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      year: 2025,
      ttfnid: '123',
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/line-directory/internal/timetable-hearing/statements/responsible-transport-companies/123/2025',
    );
  });
});
