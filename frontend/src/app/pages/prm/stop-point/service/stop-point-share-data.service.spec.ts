import { TestBed } from '@angular/core/testing';

import { StopPointExistsDataShareService } from './stop-point-exists-data-share.service';

describe('StopPointShareDataService', () => {
  let service: StopPointExistsDataShareService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StopPointExistsDataShareService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
