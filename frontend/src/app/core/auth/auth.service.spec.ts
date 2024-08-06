import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {AuthService} from './auth.service';
import {OAuthService, OAuthStorage, OAuthSuccessEvent} from 'angular-oauth2-oidc';
import {of, Subject} from 'rxjs';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {Router} from "@angular/router";
import {UserService} from "./user/user.service";
import {PageService} from "../pages/page.service";

function createOauthServiceSpy() {
  const oauthServiceSpy = jasmine.createSpyObj<OAuthService>('OAuthService', [
    'getIdentityClaims',
    'getGrantedScopes',
    'configure',
    'setupAutomaticSilentRefresh',
    'loadDiscoveryDocumentAndLogin',
    'loadDiscoveryDocumentAndTryLogin',
    'loadDiscoveryDocument',
    'logOut',
    'initCodeFlow',
    'getAccessToken',
    'hasValidAccessToken',
  ]);
  oauthServiceSpy.loadDiscoveryDocumentAndLogin.and.returnValue(
    new Promise((resolve: (v: boolean) => void): void => {
      oauthServiceSpy.state = undefined;
      resolve(true);
    })
  );
  oauthServiceSpy.loadDiscoveryDocumentAndTryLogin.and.returnValue(
    new Promise((resolve: (v: boolean) => void): void => {
      oauthServiceSpy.state = undefined;
      resolve(true);
    })
  );
  oauthServiceSpy.loadDiscoveryDocument.and.returnValue(
    new Promise((resolve: (v: OAuthSuccessEvent) => void): void => {
      oauthServiceSpy.state = undefined;
      resolve({} as OAuthSuccessEvent);
    })
  );
  oauthServiceSpy.events = new Subject();
  oauthServiceSpy.state = undefined;

  oauthServiceSpy.getIdentityClaims.and.returnValue({name: 'me', email: 'me@sbb.ch', roles: []});
  const fakeToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6W119.yjh-DMdelyF78dO4LdVa--VDaJOcdk8OYJ-FOQnAkKA'
  oauthServiceSpy.getAccessToken.and.returnValue(fakeToken);
  oauthServiceSpy.hasValidAccessToken.and.returnValue(true);
  return oauthServiceSpy;
}

const oauthService = createOauthServiceSpy();

describe('AuthService', () => {
  sessionStorage.setItem('requested_route', 'mock');

  let authService: AuthService;
  const userService = jasmine.createSpyObj(['setCurrentUserAndLoadPermissions', 'setToUnauthenticatedUser']);
  userService.setCurrentUserAndLoadPermissions.and.returnValue(of({
    name: 'Test (ITC)',
    email: 'test@test.ch',
    sbbuid: 'e123456',
    isAdmin: true,
    permissions: []
  }));

  const pageService = jasmine.createSpyObj(['addPagesBasedOnPermissions','resetPages']);
  const oauthStorage = jasmine.createSpyObj<OAuthStorage>(['removeItem']);
  const router = jasmine.createSpyObj(['navigateByUrl', 'navigate']);
  router.url = '/';
  router.navigateByUrl.and.returnValue(Promise.resolve(true));
  router.navigate.and.returnValue(Promise.resolve(true));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {provide: OAuthService, useValue: oauthService},
        {provide: OAuthStorage, useValue: oauthStorage},
        {provide: UserService, useValue: userService},
        {provide: PageService, useValue: pageService},
        {provide: Router, useValue: router},
      ],
    });

    authService = new AuthService(oauthService, router, userService, pageService, oauthStorage);
    oauthStorage.removeItem.calls.reset();
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  it('logs in with oauthService', () => {
    authService.login();
    expect(oauthService.initCodeFlow).toHaveBeenCalled();
  });

  it('logs out with oauthService', () => {
    authService.logout();
    expect(oauthService.logOut).toHaveBeenCalled();
    expect(userService.setToUnauthenticatedUser).toHaveBeenCalled();
    expect(pageService.resetPages).toHaveBeenCalled();
  });

  it('removes access token from storage if not valid', fakeAsync(() => {
    oauthService.hasValidAccessToken.and.returnValue(false);

    new AuthService(oauthService, router, userService, pageService, oauthStorage);
    tick(1000);

    expect(oauthStorage.removeItem).toHaveBeenCalled();
  }));

  it('does not remove access token from storage if valid', fakeAsync(() => {
    oauthService.hasValidAccessToken.and.returnValue(true);

    new AuthService(oauthService, router, userService, pageService, oauthStorage);
    tick(1000);

    expect(oauthStorage.removeItem).not.toHaveBeenCalled();
  }));

});
