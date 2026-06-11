import {TestBed} from '@angular/core/testing';
import {beforeEach, describe, expect, it, vi} from 'vitest';
import {AtlasApiService} from '../atlas-api.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {UserService} from '../../../core/auth/user/user.service';
import {DossierInternalService} from './dossier-internal.service';
import {TthDossier} from '../../model/tthDossier';
import {SwissCanton} from '../../model/swissCanton';
import {DossierStatus} from '../../model/dossierStatus';
import {BoAnswer} from '../../model/boAnswer';
import {Language} from '../../model/language';
import {HearingStatus} from '../../model/hearingStatus';
import {StatementStatus} from '../../model/statementStatus';
import {EMPTY} from 'rxjs';

describe('DossierInternalService', () => {
  let service: DossierInternalService;
  let apiService: AtlasApiService;

  const dossier: TthDossier = {
    swissCanton: SwissCanton.Bern,
    boContactMail: 'info@bls.ch',
    boDeadlineToAnswer: new Date('2014-12-14'),
    questions: [{ question: 'Habt ihr mehr Busse?' }],
    statementIds: [1000],
    id: 1234,
    topic: 'Mehr Busse bitte',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DossierInternalService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(DossierInternalService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'getBlob').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'paramsOf');
  });

  it('should get dossier', () => {
    // when
    service.getDossier(5);

    // then
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/workflow/internal/tth/dossier/5');
  });

  it('should get dossier overview', () => {
    // when
    service.getOverview(2026, HearingStatus.Active, SwissCanton.Bern, undefined, ['Busse']);

    // then
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith('/workflow/internal/tth/dossier', expect.any(HttpParams));
  });

  it('should create dossier', () => {
    // when
    service.createDossier(dossier);

    // then
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/workflow/internal/tth/dossier', dossier);
  });

  it('should update dossier', () => {
    // when
    service.updateDossier(dossier);

    // then
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith('/workflow/internal/tth/dossier/1234', dossier);
  });

  it('should send dossier to bo', () => {
    // when
    service.sendDossierToBo(5);

    // then
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/workflow/internal/tth/dossier/5/send-to-bo');
  });

  it('should complete dossier', () => {
    // when
    service.completeDossier(5, DossierStatus.Accepted);

    // then
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/workflow/internal/tth/dossier/5/complete/ACCEPTED');
  });

  it('should answer question', () => {
    // when
    const baAnswer: BoAnswer = {answerToCanton: "Alles gut!"}
    service.answerQuestion(5, baAnswer);

    // then
    expect(apiService.post).toHaveBeenCalledExactlyOnceWith('/workflow/internal/tth/dossier/answer/5', baAnswer);
  });

  it('should get dossiers as csv with all optional parameters', () => {
    // given
    const statusRestrictions: StatementStatus[] = [StatementStatus.Accepted, StatementStatus.Rejected];

    // when
    service.getDossiersAsCsv(
      Language.Fr,
      2026,
      HearingStatus.Active,
      SwissCanton.Bern,
      'U000001',
      ['Fahrplan', 'Bahn'],
      statusRestrictions,
    );

    // then
    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      'timetableHearingYear.timetableYear': 2026,
      'timetableHearingYear.hearingStatus': HearingStatus.Active,
      canton: SwissCanton.Bern,
      boContactSbbuid: 'U000001',
      searchCriterias: ['Fahrplan', 'Bahn'],
      statusRestrictions,
      lang: Language.Fr,
    });
    expect(apiService.getBlob).toHaveBeenCalledExactlyOnceWith(
      '/workflow/internal/tth/dossier/csv',
      expect.any(HttpParams),
    );
  });

});
