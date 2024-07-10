import { TestBed } from '@angular/core/testing';

import { StopPointRestartWorkflowDialogService } from './stop-point-restart-workflow-dialog.service';

describe('StopPointRestartWorkflowDialogService', () => {
  let service: StopPointRestartWorkflowDialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StopPointRestartWorkflowDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
