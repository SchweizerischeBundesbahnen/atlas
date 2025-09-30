import {TestBed} from '@angular/core/testing';
import {AtlasApiService} from '../atlas-api.service';
import {HttpClient} from '@angular/common/http';
import {UserService} from '../../../core/auth/user/user.service';
import {LineWorkflowService} from "./line-workflow.service";
import {WorkflowStart} from "../../model/workflowStart";
import {Workflow} from "../../model/workflow";
import {ExaminantWorkflowCheck} from "../../model/examinantWorkflowCheck";
import WorkflowTypeEnum = Workflow.WorkflowTypeEnum;

describe('LineWorkflowService', () => {
  let service: LineWorkflowService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LineWorkflowService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(LineWorkflowService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'get');
    spyOn(apiService, 'post');
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'paramsOf').and.callThrough();
  });

  it('should startWorkflow', () => {
    // given
    const workflowStart: WorkflowStart = {
      businessObjectId:
        1000,
      swissId: 'ch:1:slnid:1456',
      workflowType: WorkflowTypeEnum.Line,
      description: 'Linie 1',
      workflowComment: 'Should be changed',
      number: '1',
      client: {
        firstName: 'Max',
        lastName: 'Roger',
        mail: 'max.roger@bls.ch',
        personFunction: 'Linienverantwortlicher',
      },
    };

    // when
    service.startWorkflow(workflowStart);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/v1/line/workflows', workflowStart);
  });

  it('should getWorkflow', () => {
    // when
    service.getWorkflow(5);

    // then
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/workflow/internal/line/workflows/5',
    );
  });

  it('should examinantCheck', () => {
    // given
    const examinantWorkflowCheck: ExaminantWorkflowCheck = {
      accepted: true,
      checkComment: 'Approved',
      examinant: {
        firstName: 'Hansjörg',
        lastName: 'Peterlin'!,
        personFunction: 'Important Person',
      }
    };

    // when
    service.examinantCheck(123, examinantWorkflowCheck);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/line/workflows/123/examinant-check', examinantWorkflowCheck);
  });
});
