import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { of } from 'rxjs';
import { UserService } from './user/user.service';
import { PageService } from '../pages/page.service';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { User } from './user/user';
import { Configuration } from '../../api';
import SpyObj = jasmine.SpyObj;

const fakeToken =
  'eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkZha2UgVXNlciIsInJvbGVzIjpbImF0bGFzLWFkbWluIl0sImlzcyI6Im15LWFwcCIsImV4cCI6MTcwNTA5NjAwMH0.';

describe('AuthService', () => {
  let authService: AuthService;

  let userServiceSpy: SpyObj<UserService>;
  let pageServiceSpy: SpyObj<PageService>;
  let oidcSecurityServiceSpy: SpyObj<OidcSecurityService>;
  let apiServiceConfigMock: Partial<Configuration>;

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

    TestBed.configureTestingModule({
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: PageService, useValue: pageServiceSpy },
        { provide: OidcSecurityService, useValue: oidcSecurityServiceSpy },
        { provide: Configuration, useValue: apiServiceConfigMock },
        AuthService,
      ],
    });
  });

  it('init: should setToUnauthenticatedUser if user data is unavailable', () => {
    oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));

    authService = TestBed.inject(AuthService);

    expect(userServiceSpy.setToUnauthenticatedUser).toHaveBeenCalledOnceWith();
    expect(
      userServiceSpy.setCurrentUserAndLoadPermissions
    ).not.toHaveBeenCalled();
    expect(pageServiceSpy.addPagesBasedOnPermissions).not.toHaveBeenCalled();
    expect(apiServiceConfigMock.basePath).toBeUndefined();
  });

  it('init: should set current user, load permissions and show pages if user data is available', () => {
    oidcSecurityServiceSpy.getUserData.and.returnValue(
      of({
        email: 'test@sbb.ch',
        name: 'test',
        sbbuid: 'u123456',
      })
    );
    oidcSecurityServiceSpy.getAccessToken.and.returnValue(of(fakeToken));
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
      isAdmin: true,
      permissions: [],
    });
    expect(
      pageServiceSpy.addPagesBasedOnPermissions
    ).toHaveBeenCalledOnceWith();
    expect(apiServiceConfigMock.basePath).toEqual('http://localhost:8888');
  });

  it('should login', () => {
    oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));

    authService = TestBed.inject(AuthService);
    authService.login();

    expect(oidcSecurityServiceSpy.authorize).toHaveBeenCalledOnceWith();
  });

  it('should logout', () => {
    oidcSecurityServiceSpy.getUserData.and.returnValue(of(null));
    oidcSecurityServiceSpy.logoffAndRevokeTokens.and.returnValue(of(null));

    authService = TestBed.inject(AuthService);
    authService.logout();

    expect(
      oidcSecurityServiceSpy.logoffAndRevokeTokens
    ).toHaveBeenCalledOnceWith();
  });
});
