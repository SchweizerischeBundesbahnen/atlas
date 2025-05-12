import { TestBed } from '@angular/core/testing';

import { StopPointTerminationDialogService } from './stop-point-termination-dialog.service';

describe('StopPointTerminationDialogService', () => {
  let service: StopPointTerminationDialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StopPointTerminationDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
