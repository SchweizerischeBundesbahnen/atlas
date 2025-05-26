import {TestBed} from '@angular/core/testing';

import {WorkflowService} from './workflow.service';
import {AtlasApiService} from "../atlasApi.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../core/auth/user/user.service";
import {TerminationStopPointAddWorkflow} from "../../model/terminationStopPointAddWorkflow";

describe('WorkflowService', () => {
  let service: WorkflowService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorkflowService,
        AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ]
    });
    service = TestBed.inject(WorkflowService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'post');
  });

  it('should start termination', () => {
    //given
    const terminationStopPointAddWorkflow: TerminationStopPointAddWorkflow = {
      sloid: 'ch:1sloid:700',
      versionId: 123,
      boTerminationDate: new Date(),
      applicantMail: "a@b.ch",
      workflowComment: "Comment"
    }
    //when
    service.startTermination(terminationStopPointAddWorkflow);

    //then
    expect(apiService.post).toHaveBeenCalledWith('/workflow/internal/termination-stop-point/workflows', terminationStopPointAddWorkflow)
  });
});
