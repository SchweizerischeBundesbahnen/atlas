import { TestBed } from '@angular/core/testing';

import { TerminationCancelDialogService } from './termination-cancel-dialog.service';

describe('TerminationCancelDialog', () => {
  let service: TerminationCancelDialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TerminationCancelDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
