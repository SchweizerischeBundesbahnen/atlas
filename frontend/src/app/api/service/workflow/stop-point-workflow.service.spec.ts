import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { StopPointWorkflowService } from './stop-point-workflow.service';
import { StopPointAddWorkflow } from '../../model/stopPointAddWorkflow';

describe('StopPointWorkflowService', () => {
  let service: StopPointWorkflowService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StopPointWorkflowService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(StopPointWorkflowService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'get');
    spyOn(apiService, 'post');
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'paramsOf').and.callThrough();
  });

  it('should addStopPointWorkflow', () => {
    // given
    const stopPointAddWorkflow: StopPointAddWorkflow = {
      sloid: 'ch:1sloid:700',
      versionId: 123,
      applicantMail: 'a@b.ch',
      workflowComment: 'Comment',
    };

    // when
    service.addStopPointWorkflow(stopPointAddWorkflow);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/v1/stop-point/workflows', stopPointAddWorkflow);
  });

});
