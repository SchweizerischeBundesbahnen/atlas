import {TestBed} from '@angular/core/testing';
import {StopPointTerminationWorkflowService} from './stop-point-termination-workflow.service';
import {AtlasApiService} from '../atlas-api.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {UserService} from '../../../core/auth/user/user.service';
import {TerminationStopPointAddWorkflow} from '../../model/terminationStopPointAddWorkflow';
import {TerminationAbort} from "../../model/terminationAbort";
import any = jasmine.any;
import { TerminationDecision } from '../../model/terminationDecision';
import { JudgementType } from '../../model/judgementType';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

describe('StopPointTerminationWorkflowService', () => {
  let service: StopPointTerminationWorkflowService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StopPointTerminationWorkflowService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(StopPointTerminationWorkflowService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'get');
    spyOn(apiService, 'post');
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'paramsOf').and.callThrough();
  });

  it('should start termination', () => {
    // given
    const terminationStopPointAddWorkflow: TerminationStopPointAddWorkflow = {
      sloid: 'ch:1sloid:700',
      versionId: 123,
      boTerminationDate: new Date(),
      applicantMail: 'a@b.ch',
      workflowComment: 'Comment',
    };

    // when
    service.startTermination(terminationStopPointAddWorkflow);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/v1/termination-stop-point/workflows', terminationStopPointAddWorkflow);
  });

  it('should getTerminationInfoBySloid', () => {
    // when
    service.getTerminationInfoBySloid('ch:1:sloid:1');

    // then
    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ sloid: 'ch:1:sloid:1' });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/workflow/internal/termination-stop-point/workflows/termination-info/ch%3A1%3Asloid%3A1',
    );
  });

  it('should getTerminationStopPointWorkflows', () => {
    // when
    service.getTerminationStopPointWorkflows(undefined, undefined, [1, 2]);

    // then
    expect(apiService.paramsOf).toHaveBeenCalledOnceWith(
      {
        searchCriterias: undefined,
        workflowIds: [1, 2],
        sboids: undefined,
        status: undefined,
        page: undefined,
        size: undefined,
        sort: undefined,
      },
    );
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/workflow/v1/termination-stop-point/workflows',
      any(HttpParams)
    );
  });

  it('should decide as info+', () => {
    const decision: TerminationDecision = {
      judgement: JudgementType.No, terminationDecisionPerson: TerminationDecisionPersonEnum.InfoPlus
    };

    // when
    service.decisionInfoPlus(1, decision);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/workflow/internal/termination-stop-point/workflows/decision/info-plus/1',
      decision
    );
  });

  it('should decide as nova', () => {
    const decision: TerminationDecision = {
      judgement: JudgementType.No, terminationDecisionPerson: TerminationDecisionPersonEnum.Nova
    };

    // when
    service.decisionNova(1, decision);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/workflow/internal/termination-stop-point/workflows/decision/nova/1',
      decision
    );
  });

  it('should abort termination', () => {
    // given
    const terminationAbort: TerminationAbort = {
      abortComment: 'abort comment'
    };

    // when
    service.abortTermination(123, terminationAbort);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/termination-stop-point/workflows/abort/123', terminationAbort);
  });
});
