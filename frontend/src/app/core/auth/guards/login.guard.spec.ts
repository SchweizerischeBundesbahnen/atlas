import { AuthService } from '../auth.service';
import { TestBed } from '@angular/core/testing';
import { LoginGuard } from './login.guard';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../user/user.service';
import { BehaviorSubject, Subscription } from 'rxjs';

describe('LoginGuard', () => {
  let sub: Subscription;

  let loginGuard: LoginGuard;
  let authServiceSpy: AuthService;
  let router: Router;

  let permissionsLoaded = false;
  let loggedIn = false;
  const userServiceMock: Partial<UserService> = {
    get loggedIn(): boolean {
      return loggedIn;
    },
    permissionsLoaded: new BehaviorSubject(permissionsLoaded),
  };

  function publishPermissionsLoaded(loaded: boolean) {
    permissionsLoaded = loaded;
    userServiceMock.permissionsLoaded?.next(permissionsLoaded);
  }

  beforeEach(() => {
    loggedIn = false;
    publishPermissionsLoaded(false);
    authServiceSpy = jasmine.createSpyObj<AuthService>(['login']);

    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: AuthService, useValue: authServiceSpy },
      ],
    });

    loginGuard = TestBed.inject(LoginGuard);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    sub.unsubscribe();
  });

  it('should return true when user is logged in and permissions are loaded', (done) => {
    sub = loginGuard.canActivate().subscribe({
      next: (result) => {
        if (!permissionsLoaded) fail('permissions should be loaded!');
        expect(result).toBeTrue();
        done();
      },
    });

    publishPermissionsLoaded(false);
    loggedIn = true;
    publishPermissionsLoaded(true);
  });

  it('should login and redirect to / if user is not logged in and permissions are loaded', (done) => {
    sub = loginGuard.canActivate().subscribe({
      next: (result) => {
        if (!permissionsLoaded) fail('permissions should be loaded!');
        expect(authServiceSpy.login).toHaveBeenCalledOnceWith();
        expect(result).toEqual(router.parseUrl('/'));
        done();
      },
    });

    publishPermissionsLoaded(false);
    loggedIn = false;
    publishPermissionsLoaded(true);
  });
});
