import { TestBed } from '@angular/core/testing';
import { WorkflowService } from './workflow.service';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { TerminationStopPointAddWorkflow } from '../../model/terminationStopPointAddWorkflow';
import any = jasmine.any;

describe('WorkflowService', () => {
  let service: WorkflowService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorkflowService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(WorkflowService);
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
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/termination-stop-point/workflows', terminationStopPointAddWorkflow);
  });

  it('test getTerminationInfoBySloid', () => {
    // when
    service.getTerminationInfoBySloid('ch:1:sloid:1');

    // then
    expect(apiService.validateParams).toHaveBeenCalledOnceWith({ sloid: 'ch:1:sloid:1' });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/workflow/internal/termination-stop-point/workflows/termination-info/ch%3A1%3Asloid%3A1',
    );
  });

  it('test getTerminationStopPointWorkflows', () => {
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
      '/workflow/internal/termination-stop-point/workflows',
      any(HttpParams)
    );
  });
});
