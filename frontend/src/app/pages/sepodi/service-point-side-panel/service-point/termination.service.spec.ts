import { TestBed } from '@angular/core/testing';

import { TerminationService } from './termination.service';

describe('TerminationService', () => {
  let service: TerminationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TerminationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
