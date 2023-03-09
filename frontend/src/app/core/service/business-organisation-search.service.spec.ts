import { TestBed } from '@angular/core/testing';

import { BusinessOrganisationSearchService } from './business-organisation-search.service';

describe('BusinessOrganisationSearchService', () => {
  let service: BusinessOrganisationSearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BusinessOrganisationSearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
