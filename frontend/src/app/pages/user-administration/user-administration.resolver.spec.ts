import { TestBed } from '@angular/core/testing';

import { UserAdministrationResolver } from './user-administration.resolver';
import { AppTestingModule } from '../../app.testing.module';

describe('UserAdministrationResolver', () => {
  let resolver: UserAdministrationResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
    });
    resolver = TestBed.inject(UserAdministrationResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
