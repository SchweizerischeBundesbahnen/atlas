import { TestBed } from '@angular/core/testing';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../user/user.service';
import { AdminGuard } from './admin.guard';

describe('AdminGuard', () => {
  let router: Router;
  let adminGuard: AdminGuard;

  let isAdmin: boolean;
  const userServiceMock: Partial<UserService> = {
    get isAdmin(): boolean {
      return isAdmin;
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [{ provide: UserService, useValue: userServiceMock }],
    });

    router = TestBed.inject(Router);
    adminGuard = TestBed.inject(AdminGuard);
  });

  it('should return true', () => {
    isAdmin = true;
    expect(adminGuard.canActivate()).toBeTrue();
  });

  it('should return / redirect', () => {
    isAdmin = false;
    expect(adminGuard.canActivate()).toEqual(router.parseUrl('/'));
  });
});
