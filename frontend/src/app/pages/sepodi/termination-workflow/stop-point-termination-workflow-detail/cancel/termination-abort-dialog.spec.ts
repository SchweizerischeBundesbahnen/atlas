import { TestBed } from '@angular/core/testing';

import { TerminationAbortDialogService } from './termination-abort-dialog.service';

describe('TerminationAbortlDialog', () => {
  let service: TerminationAbortDialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TerminationAbortDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
