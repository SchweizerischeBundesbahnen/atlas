import { TestBed } from '@angular/core/testing';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../user/user.service';
import { LoginGuard } from './login.guard';
import { of } from 'rxjs';
import { AdminGuard } from './admin.guard';
import SpyObj = jasmine.SpyObj;

describe('AdminGuard', () => {
  let router: Router;
  let adminGuard: AdminGuard;

  let loginGuardSpy: SpyObj<LoginGuard>;
  let isAdmin: boolean;
  const userServiceMock: Partial<UserService> = {
    get isAdmin(): boolean {
      return isAdmin;
    },
  };

  beforeEach(() => {
    loginGuardSpy = jasmine.createSpyObj<LoginGuard>(['canActivate']);
    loginGuardSpy.canActivate.and.returnValue(of(true));

    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: LoginGuard, useValue: loginGuardSpy },
      ],
    });

    router = TestBed.inject(Router);
    adminGuard = TestBed.inject(AdminGuard);
  });

  it('should return true', (done) => {
    isAdmin = true;
    let asserted = false;
    adminGuard.canActivate().subscribe({
      next: (value) => {
        expect(loginGuardSpy.canActivate).toHaveBeenCalledTimes(1);
        expect(value).toBeTrue();
        asserted = true;
      },
      complete: () => (asserted ? done() : {}),
    });
  });

  it('should return / redirect', (done) => {
    isAdmin = false;
    let asserted = false;
    adminGuard.canActivate().subscribe({
      next: (value) => {
        expect(loginGuardSpy.canActivate).toHaveBeenCalledTimes(1);
        expect(value).toEqual(router.parseUrl('/'));
        asserted = true;
      },
      complete: () => (asserted ? done() : {}),
    });
  });
});
