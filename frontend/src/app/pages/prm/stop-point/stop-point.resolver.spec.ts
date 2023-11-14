import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { stopPointResolver } from './stop-point.resolver';

describe('stopPointResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => stopPointResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
