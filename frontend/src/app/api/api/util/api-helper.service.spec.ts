import { TestBed } from '@angular/core/testing';

import { ApiHelperService } from './api-helper.service';

describe('ApiHelperService', () => {
  let service: ApiHelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
