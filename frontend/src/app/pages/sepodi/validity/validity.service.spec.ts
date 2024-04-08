import { TestBed } from '@angular/core/testing';

import { ValidityService } from './validity.service';

describe('ValidityService', () => {
  let service: ValidityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ValidityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
