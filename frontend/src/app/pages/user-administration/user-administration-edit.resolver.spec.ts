import { TestBed } from '@angular/core/testing';

import { UserAdministrationEditResolver } from './user-administration-edit.resolver';

describe('UserAdministrationEditResolver', () => {
  let resolver: UserAdministrationEditResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    resolver = TestBed.inject(UserAdministrationEditResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
