import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { of } from 'rxjs';
import { UserService } from './user/user.service';
import { PageService } from '../pages/page.service';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { User } from './user/user';
import SpyObj = jasmine.SpyObj;

const fakeToken =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6W119.yjh-DMdelyF78dO4LdVa--VDaJOcdk8OYJ-FOQnAkKA';

describe('AuthService', () => {
  let authService: AuthService;

  let userServiceSpy: SpyObj<UserService>;
  let pageServiceSpy: SpyObj<PageService>;
  let oidcSecurityServiceSpy: SpyObj<OidcSecurityService>;

  beforeEach(() => {
    userServiceSpy = jasmine.createSpyObj<UserService>([
      'setCurrentUserAndLoadPermissions',
      'setToUnauthenticatedUser',
    ]);
    userServiceSpy.setCurrentUserAndLoadPermissions.and.returnValue(
      of({
        name: 'Test (ITC)',
        email: 'test@test.ch',
        sbbuid: 'e123456',
        isAdmin: true,
        permissions: [],
      })
    );

    pageServiceSpy = jasmine.createSpyObj(['addPagesBasedOnPermissions']);

    oidcSecurityServiceSpy = jasmine.createSpyObj<OidcSecurityService>([
      'getUserData',
      'authorize',
      'logoff',
    ]);

    TestBed.configureTestingModule({
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: PageService, useValue: pageServiceSpy },
        { provide: OidcSecurityService, useValue: oidcSecurityServiceSpy },
        AuthService,
      ],
    });
  });

  it('init: should setToUnauthenticatedUser if user data is unavailable', () => {
    oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));

    authService = TestBed.inject(AuthService);

    expect(userServiceSpy.setToUnauthenticatedUser).toHaveBeenCalledOnceWith();
  });

  it('init: should set current user, load permissions and show pages if user data is available', () => {
    oidcSecurityServiceSpy.getUserData.and.returnValue(
      of({
        email: 'test@sbb.ch',
        name: 'test',
        sbbuid: 'u123456',
      })
    );
    userServiceSpy.setCurrentUserAndLoadPermissions.and.returnValue(
      of({} as User)
    );

    authService = TestBed.inject(AuthService);

    expect(
      userServiceSpy.setCurrentUserAndLoadPermissions
    ).toHaveBeenCalledOnceWith({
      email: 'test@sbb.ch',
      name: 'test',
      sbbuid: 'u123456',
      isAdmin: false,
      permissions: [],
    });
    expect(
      pageServiceSpy.addPagesBasedOnPermissions
    ).toHaveBeenCalledOnceWith();
  });

  it('should login', () => {
    oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));

    authService = TestBed.inject(AuthService);
    authService.login();

    expect(oidcSecurityServiceSpy.authorize).toHaveBeenCalledOnceWith();
  });

  it('should logout', () => {
    oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));
    oidcSecurityServiceSpy.logoff.and.returnValue(of(null));

    authService = TestBed.inject(AuthService);
    authService.logout();

    expect(oidcSecurityServiceSpy.logoff).toHaveBeenCalledOnceWith();
  });
});
