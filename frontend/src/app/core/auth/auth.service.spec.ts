import { TestBed } from '@angular/core/testing';
import { AuthService, BC_TOKEN } from './auth.service';
import { of } from 'rxjs';
import { UserService } from './user/user.service';
import { PageService } from '../pages/page.service';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { Configuration } from '../../api';
import { Router } from '@angular/router';
import { User } from './user/user';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

const fakeToken =
  'eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkZha2UgVXNlciIsInJvbGVzIjpbImF0bGFzLWFkbWluIl0sImlzcyI6Im15LWFwcCIsImV4cCI6MTcwNTA5NjAwMH0.';

describe('AuthService', () => {
  let authService: AuthService;

  let userServiceSpy: SpyObj<UserService>;
  let pageServiceSpy: SpyObj<PageService>;
  let oidcSecurityServiceSpy: SpyObj<OidcSecurityService>;
  let apiServiceConfigMock: Partial<Configuration>;
  let bcTokenSpy: Spy;
  let routerSpy: SpyObj<Router>;

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
      'logoffAndRevokeTokens',
      'getAccessToken',
    ]);

    apiServiceConfigMock = {
      basePath: undefined,
    };

    bcTokenSpy = jasmine.createSpy('BC_TOKEN_SPY');
    routerSpy = jasmine.createSpyObj(['navigateByUrl']);

    TestBed.configureTestingModule({
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: PageService, useValue: pageServiceSpy },
        { provide: OidcSecurityService, useValue: oidcSecurityServiceSpy },
        { provide: Configuration, useValue: apiServiceConfigMock },
        { provide: BC_TOKEN, useValue: bcTokenSpy },
        { provide: Router, useValue: routerSpy },
        AuthService,
      ],
    });
  });

  it('should login', () => {
    authService = TestBed.inject(AuthService);
    authService.login();

    expect(sessionStorage.itemset);
    expect(oidcSecurityServiceSpy.authorize).toHaveBeenCalledOnceWith();
  });

  it('should logout', (done) => {
    oidcSecurityServiceSpy.logoffAndRevokeTokens.and.returnValue(of(null));

    authService = TestBed.inject(AuthService);
    authService.logout();

    expect(
      oidcSecurityServiceSpy.logoffAndRevokeTokens
    ).toHaveBeenCalledOnceWith();
    expect(localStorage.removeItem).toHaveBeenCalledOnceWith('tryLogin');
  });

  // TDD Ansatz
  it('should provide auth init when userData defined', (done) => {
    // Arrange
    oidcSecurityServiceSpy.getUserData.and.returnValue(
      of({
        email: 'test@sbb.ch',
        name: 'test',
        sbbuid: 'u123456',
      })
    );
    oidcSecurityServiceSpy.getAccessToken.and.returnValue(of(fakeToken));
    routerSpy.navigateByUrl.and.returnValue(Promise.resolve(true));
    userServiceSpy.setCurrentUserAndLoadPermissions.and.returnValue(
      of({} as User)
    );
    // Act
    authService = TestBed.inject(AuthService);
    authService.initAuth().subscribe((result) => {
      expect(oidcSecurityServiceSpy.getUserData).toHaveBeenCalledOnceWith();
      expect(apiServiceConfigMock.basePath).toEqual('http://localhost:8888');
      expect(bcTokenSpy).toHaveBeenCalledOnceWith(); // todo: can i match fn param?
      expect(
        userServiceSpy.setCurrentUserAndLoadPermissions
      ).toHaveBeenCalledOnceWith({
        email: 'test@sbb.ch',
        name: 'test',
        sbbuid: 'u123456',
        isAdmin: true,
        permissions: [],
      });
      expect(
        pageServiceSpy.addPagesBasedOnPermissions
      ).toHaveBeenCalledOnceWith();
      expect(noReturnUrlInSessionStorage);
      expect(tryLoginInLocalstorage);
      expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/test');
      expect(result).toBeTrue();
      done();
    });
  });

  describe('should provide auth init when userData is not defined', () => {
    it('should not try login', (done) => {
      // Arrange
      oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));

      // Act
      authService = TestBed.inject(AuthService);
      authService.initAuth().subscribe((result) => {
        expect(oidcSecurityServiceSpy.getUserData).toHaveBeenCalledOnceWith();
        expect(
          userServiceSpy.setToUnauthenticatedUser
        ).toHaveBeenCalledOnceWith();
        expect(result).toBeTrue();
        done();
      });
    });

    it('should try login', (done) => {
      // Arrange
      oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));
      authService = TestBed.inject(AuthService);
      const loginSpy = spyOn(authService, 'login');

      // Act
      authService.initAuth().subscribe((result) => {
        expect(oidcSecurityServiceSpy.getUserData).toHaveBeenCalledOnceWith();
        expect(tryLoginNotInLocalStorage);
        expect(loginSpy).toHaveBeenCalledOnceWith();
        expect(result).toBeTrue();
        done();
      });
    });
  });
});
