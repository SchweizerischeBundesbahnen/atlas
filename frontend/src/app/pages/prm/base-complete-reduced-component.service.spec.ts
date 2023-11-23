import { TestBed } from '@angular/core/testing';

import { BasePrmComponentService } from './base-prm-component.service';

describe('BaseCompleteReducedComponentService', () => {
  let service: BasePrmComponentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BasePrmComponentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
