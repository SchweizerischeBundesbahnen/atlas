import {TestBed} from '@angular/core/testing';
import {AuthService} from './auth.service';
import {OAuthService, OAuthSuccessEvent} from 'angular-oauth2-oidc';
import {Subject} from 'rxjs';
import {Component} from '@angular/core';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterModule} from "@angular/router";

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
  return oauthServiceSpy;
}

const oauthService = createOauthServiceSpy();

@Component({
  selector: 'mock-component',
  template: '<h1>Mock Component</h1>',
})
class MockComponent {}

describe('AuthService', () => {
  sessionStorage.setItem('requested_route', 'mock');

  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterModule.forRoot([{ path: 'mock', component: MockComponent }]),
      ],
      providers: [
        AuthService,
        {provide: OAuthService, useValue: oauthService},
      ],
    });
    authService = TestBed.inject(AuthService);
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
  });

});
