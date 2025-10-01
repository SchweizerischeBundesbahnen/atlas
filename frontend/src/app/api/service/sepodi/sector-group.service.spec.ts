import { TestBed } from '@angular/core/testing';

import { SectorGroupService } from './sector-group.service';

describe('SectorGroupService', () => {
  let service: SectorGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SectorGroupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
