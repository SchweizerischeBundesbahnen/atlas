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

function storageMockImplOf(storageMock: { [k: string]: string }) {
  return {
    getItem: (key: string): string => {
      return key in storageMock ? storageMock[key] : '';
    },
    setItem: (key: string, value: string) => {
      storageMock[key] = value;
    },
    removeItem: (key: string) => {
      delete storageMock[key];
    },
  };
}

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

    const localStorageMock: { [k: string]: string } = {};
    const localStorageMockImpl = storageMockImplOf(localStorageMock);
    spyOn(localStorage, 'getItem').and.callFake(localStorageMockImpl.getItem);
    spyOn(localStorage, 'setItem').and.callFake(localStorageMockImpl.setItem);
    spyOn(localStorage, 'removeItem').and.callFake(
      localStorageMockImpl.removeItem
    );

    const sessionStorageMock: { [k: string]: string } = {};
    const sessionStorageMockImpl = storageMockImplOf(sessionStorageMock);
    spyOn(sessionStorage, 'getItem').and.callFake(
      sessionStorageMockImpl.getItem
    );
    spyOn(sessionStorage, 'setItem').and.callFake(
      sessionStorageMockImpl.setItem
    );
    spyOn(sessionStorage, 'removeItem').and.callFake(
      sessionStorageMockImpl.removeItem
    );
  });

  it('should login', () => {
    // Arrange
    authService = TestBed.inject(AuthService);
    // Act
    authService.login();
    // Assert
    expect(sessionStorage.itemset);
    expect(oidcSecurityServiceSpy.authorize).toHaveBeenCalledOnceWith();
  });

  it('should logout', () => {
    // Arrange
    oidcSecurityServiceSpy.logoffAndRevokeTokens.and.returnValue(of(null));
    authService = TestBed.inject(AuthService);
    // Act
    authService.logout();
    // Assert
    expect(
      oidcSecurityServiceSpy.logoffAndRevokeTokens
    ).toHaveBeenCalledOnceWith();
    expect(localStorage.removeItem).toHaveBeenCalledOnceWith('tryLogin');
  });

  it('should initAuth when userData defined', (done) => {
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
    sessionStorage.setItem('returnUrl', '/test');
    authService = TestBed.inject(AuthService);
    // Act
    authService.initAuth().subscribe((result) => {
      // Assert
      expect(oidcSecurityServiceSpy.getUserData).toHaveBeenCalledOnceWith();
      expect(apiServiceConfigMock.basePath).toEqual('http://localhost:8888');
      expect(bcTokenSpy).toHaveBeenCalledTimes(1);
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
      expect(sessionStorage.getItem('returnUrl')).toEqual('');
      expect(localStorage.getItem('tryLogin')).toEqual('yes');
      expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/test');
      expect(result).toBeTrue();
      done();
    });
  });

  describe('should initAuth when userData is not defined', () => {
    it('should not try login', (done) => {
      // Arrange
      oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));
      authService = TestBed.inject(AuthService);
      // Act
      authService.initAuth().subscribe((result) => {
        // Assert
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
      localStorage.setItem('tryLogin', 'yes');
      // Act
      authService.initAuth().subscribe((result) => {
        // Assert
        expect(oidcSecurityServiceSpy.getUserData).toHaveBeenCalledOnceWith();
        expect(localStorage.getItem('tryLogin')).toEqual('');
        expect(loginSpy).toHaveBeenCalledOnceWith();
        expect(result).toBeTrue();
        done();
      });
    });
  });

  it('should catchError in initAuth', (done) => {
    // Arrange
    oidcSecurityServiceSpy.getUserData.and.returnValue(
      of({ email: 'test@sbb.ch' })
    );
    oidcSecurityServiceSpy.getAccessToken.and.throwError('testError');
    authService = TestBed.inject(AuthService);
    // Act
    authService.initAuth().subscribe((result) => {
      // Assert
      expect(result).toBeTrue();
      done();
    });
  });
});
