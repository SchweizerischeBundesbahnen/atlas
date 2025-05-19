import {TestBed} from '@angular/core/testing';

import {WorkflowService} from './workflow.service';
import {AtlasApiService} from "../atlasApi.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../../core/auth/user/user.service";

describe('WorkflowService', () => {
  let service: WorkflowService;

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
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
