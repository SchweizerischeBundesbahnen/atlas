import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { featureToggleGuard } from './feature-toggle.guard';

describe('featureToggleGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => featureToggleGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
