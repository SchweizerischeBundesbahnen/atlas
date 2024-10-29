import { AuthService } from '../auth.service';
import { TestBed } from '@angular/core/testing';
import { LoginGuard } from './login.guard';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../user/user.service';
import { Observable } from 'rxjs';

describe('LoginGuard', () => {
  let loginGuard: LoginGuard;
  let authServiceSpy: AuthService;
  let router: Router;

  let onPermissionsLoadedCalled = false;
  let loggedIn = false;
  const userServiceMock: Partial<UserService> = {
    get loggedIn(): boolean {
      return loggedIn;
    },
    onPermissionsLoaded(): Observable<void> {
      onPermissionsLoadedCalled = true;
      return new Observable((subsbriber) => {
        subsbriber.next();
      });
    },
  };

  beforeEach(() => {
    loggedIn = false;
    onPermissionsLoadedCalled = false;
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

  it('should return true when user is logged in and permissions are loaded', (done) => {
    loggedIn = true;
    loginGuard.canActivate().subscribe({
      next: (result) => {
        expect(onPermissionsLoadedCalled).toBeTrue();
        expect(result).toBeTrue();
        done();
      },
    });
  });

  it('should login and redirect to / if user is not logged in and permissions are loaded', (done) => {
    loginGuard.canActivate().subscribe({
      next: (result) => {
        expect(onPermissionsLoadedCalled).toBeTrue();
        expect(authServiceSpy.login).toHaveBeenCalledOnceWith();
        expect(result).toEqual(router.parseUrl('/'));
        done();
      },
    });
  });
});
