import { TestBed } from '@angular/core/testing';

import { UserAdministrationEditResolver } from './user-administration-edit.resolver';
import { AppTestingModule } from '../../app.testing.module';

describe('UserAdministrationEditResolver', () => {
  let resolver: UserAdministrationEditResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
    });
    resolver = TestBed.inject(UserAdministrationEditResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
