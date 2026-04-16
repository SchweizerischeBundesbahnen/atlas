import { beforeEach, describe, expect, it, type Mock, Mocked, vi } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { AuthService, BC_TOKEN } from './auth.service';
import { lastValueFrom, of } from 'rxjs';
import { UserService } from './user/user.service';
import { PageService } from '../pages/page.service';
import { LoginResponse, OidcSecurityService } from 'angular-auth-oidc-client';
import { Router } from '@angular/router';
import { User } from './user/user';

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

  let userServiceSpy: Mocked<Pick<UserService, 'setToUnauthenticatedUser' | 'setCurrentUserAndLoadPermissions'>>;
  let pageServiceSpy: Mocked<Pick<PageService, 'addPagesBasedOnPermissions'>>;
  let oidcSecurityServiceSpy: Mocked<Pick<OidcSecurityService, 'checkAuth' | 'authorize' | 'logoffAndRevokeTokens'>>;
  let bcTokenSpy: Mock;
  let routerSpy: Mocked<Pick<Router, 'navigateByUrl'>>;

  let localGetCalled: Mock;
  let sessionGetCalled: Mock;
  let localSetCalled: Mock;
  let sessionSetCalled: Mock;
  let localRemoveCalled: Mock;
  let sessionRemoveCalled: Mock;

  beforeEach(() => {
    localGetCalled = vi.fn();
    sessionGetCalled = vi.fn();
    localSetCalled = vi.fn();
    sessionSetCalled = vi.fn();
    localRemoveCalled = vi.fn();
    sessionRemoveCalled = vi.fn();

    userServiceSpy = {
      setCurrentUserAndLoadPermissions: vi.fn(),
      setToUnauthenticatedUser: vi.fn(),
    };

    pageServiceSpy = {
      addPagesBasedOnPermissions: vi.fn(),
    };

    oidcSecurityServiceSpy = {
      checkAuth: vi.fn(),
      authorize: vi.fn(),
      logoffAndRevokeTokens: vi.fn(),
    };

    bcTokenSpy = vi.fn();
    routerSpy = {
      navigateByUrl: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: PageService, useValue: pageServiceSpy },
        { provide: OidcSecurityService, useValue: oidcSecurityServiceSpy },
        { provide: BC_TOKEN, useValue: bcTokenSpy },
        { provide: Router, useValue: routerSpy },
        AuthService,
      ],
    });

    const localStorageMock: {
      [k: string]: string;
    } = {};
    const localStorageMockImpl = storageMockImplOf(localStorageMock);

    const sessionStorageMock: {
      [k: string]: string;
    } = {};
    const sessionStorageMockImpl = storageMockImplOf(sessionStorageMock);

    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(function (this: Storage, key: string): string | null {
      if (this === localStorage) {
        localGetCalled(key);
        return localStorageMockImpl.getItem(key);
      } else {
        sessionGetCalled(key);
        return sessionStorageMockImpl.getItem(key);
      }
    });

    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(function (
      this: Storage,
      key: string,
      value: string
    ): void {
      if (this === localStorage) {
        localStorageMockImpl.setItem(key, value);
        localSetCalled(key, value);
      } else {
        sessionStorageMockImpl.setItem(key, value);
        sessionSetCalled(key, value);
      }
    });

    vi.spyOn(Storage.prototype, 'removeItem').mockImplementation(function (this: Storage, key: string): void {
      if (this === localStorage) {
        localStorageMockImpl.removeItem(key);
        localRemoveCalled(key);
      } else {
        sessionStorageMockImpl.removeItem(key);
        sessionRemoveCalled(key);
      }
    });
  });

  it('should login', () => {
    // Arrange
    authService = TestBed.inject(AuthService);
    // Act
    authService.login();
    // Assert
    expect(sessionSetCalled).toHaveBeenCalledTimes(1);
    expect(oidcSecurityServiceSpy.authorize).toHaveBeenCalledExactlyOnceWith();
  });

  it('should logout', () => {
    // Arrange
    oidcSecurityServiceSpy.logoffAndRevokeTokens.mockReturnValue(of(null));
    authService = TestBed.inject(AuthService);
    // Act
    authService.logout();
    // Assert
    expect(oidcSecurityServiceSpy.logoffAndRevokeTokens).toHaveBeenCalledExactlyOnceWith();
    expect(localRemoveCalled).toHaveBeenCalledExactlyOnceWith('tryLogin');
  });

  it('should initAuth when userData defined', async () => {
    // Arrange
    const loginResponse: LoginResponse = {
      accessToken: fakeToken,
      idToken: fakeToken,
      isAuthenticated: true,
      userData: {
        email: 'test@sbb.ch',
        name: 'test',
        sbbuid: 'u123456',
      },
    };
    oidcSecurityServiceSpy.checkAuth.mockReturnValue(of(loginResponse));
    routerSpy.navigateByUrl.mockReturnValue(Promise.resolve(true));
    userServiceSpy.setCurrentUserAndLoadPermissions.mockReturnValue(of({} as User));
    sessionStorage.setItem('returnUrl', '/test');
    authService = TestBed.inject(AuthService);
    // Act
    const authResult = await lastValueFrom(authService.initAuth());
    expect(oidcSecurityServiceSpy.checkAuth).toHaveBeenCalledExactlyOnceWith();
    expect(bcTokenSpy).toHaveBeenCalledTimes(1);
    expect(userServiceSpy.setCurrentUserAndLoadPermissions).toHaveBeenCalledTimes(1);
    expect(userServiceSpy.setCurrentUserAndLoadPermissions).toHaveBeenCalledWith({
      email: 'test@sbb.ch',
      name: 'test',
      sbbuid: 'u123456',
      isAdmin: true,
      permissions: [],
    });
    expect(pageServiceSpy.addPagesBasedOnPermissions).toHaveBeenCalledExactlyOnceWith();
    expect(sessionStorage.getItem('returnUrl')).toEqual('');
    expect(localStorage.getItem('tryLogin')).toEqual('yes');
    expect(routerSpy.navigateByUrl).toHaveBeenCalledExactlyOnceWith('/test');
    expect(authResult).toBe(true);
  });

  describe('should initAuth when userData is not defined', () => {
    it('should not try login', async () => {
      // Arrange
      const loginResponse: LoginResponse = {
        accessToken: '',
        idToken: '',
        isAuthenticated: false,
        userData: {},
      };
      oidcSecurityServiceSpy.checkAuth.mockReturnValue(of(loginResponse));
      authService = TestBed.inject(AuthService);
      // Act
      const authResult = await lastValueFrom(authService.initAuth());
      expect(oidcSecurityServiceSpy.checkAuth).toHaveBeenCalledExactlyOnceWith();
      expect(userServiceSpy.setToUnauthenticatedUser).toHaveBeenCalledExactlyOnceWith();
      expect(authResult).toBe(true);
    });

    it('should try login', async () => {
      // Arrange
      const loginResponse: LoginResponse = {
        accessToken: '',
        idToken: '',
        isAuthenticated: false,
        userData: {},
      };
      oidcSecurityServiceSpy.checkAuth.mockReturnValue(of(loginResponse));
      authService = TestBed.inject(AuthService);
      const loginSpy = vi.spyOn(authService, 'login');
      localStorage.setItem('tryLogin', 'yes');
      // Act
      const authResult = await lastValueFrom(authService.initAuth());
      // Assert
      expect(oidcSecurityServiceSpy.checkAuth).toHaveBeenCalledExactlyOnceWith();
      expect(localStorage.getItem('tryLogin')).toEqual('');
      expect(loginSpy).toHaveBeenCalledExactlyOnceWith();
      expect(authResult).toBe(true);
    });
  });

  it('should catchError in initAuth', async () => {
    vi.spyOn(console, 'log').mockImplementation(() => undefined);

    // Arrange
    const loginResponse: LoginResponse = {
      accessToken: fakeToken,
      idToken: fakeToken,
      isAuthenticated: true,
      userData: {
        email: 'test@sbb.ch',
        name: 'test',
        sbbuid: 'u123456',
      },
    };
    oidcSecurityServiceSpy.checkAuth.mockReturnValue(of(loginResponse));

    userServiceSpy.setCurrentUserAndLoadPermissions.mockImplementation(() => {
      throw new Error('testError');
    });
    authService = TestBed.inject(AuthService);
    // Act
    const authResult = await lastValueFrom(authService.initAuth());
    // Assert
    expect(authResult).toBe(true);
  });
});
