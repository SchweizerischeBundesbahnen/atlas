import {TestBed} from '@angular/core/testing';
import {AdminGuard} from './admin.guard';
import {RouterModule} from '@angular/router';
import {adminUserServiceMock} from "../../../app.testing.mocks";
import {UserService} from "../user/user.service";

describe('AdminGuard', () => {
  let guard: AdminGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        { provide: UserService, useValue: adminUserServiceMock },
      ],
    });
    guard = TestBed.inject(AdminGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return UrlTree', () => {
    const canActivateResult = guard.canActivate();
    expect(canActivateResult).toBeDefined();
    expect(canActivateResult).toBeTrue();
  });
});
