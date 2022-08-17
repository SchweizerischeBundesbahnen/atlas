import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';
import { OAuthService } from 'angular-oauth2-oidc';
import { Subject } from 'rxjs';
import { Role } from './role';
import { Component } from '@angular/core';

function createOauthServiceSpy() {
  const oauthServiceSpy = jasmine.createSpyObj<OAuthService>('OAuthService', [
    'getIdentityClaims',
    'getGrantedScopes',
    'configure',
    'setupAutomaticSilentRefresh',
    'loadDiscoveryDocumentAndLogin',
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
      imports: [RouterTestingModule.withRoutes([{ path: 'mock', component: MockComponent }])],
      providers: [
        AuthService,
        {
          provide: OAuthService,
          useValue: oauthService,
        },
      ],
    });
    authService = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  it('retrieves claims from oauthService', () => {
    oauthService.getIdentityClaims.and.returnValue({ name: 'me', email: 'me@sbb.ch', roles: [] });
    const claims = authService.claims;
    expect(claims).toBeTruthy();
    expect(oauthService.getIdentityClaims).toHaveBeenCalled();
  });

  it('retrieves scopes from oauthService', () => {
    const scopes = authService.scopes;
    expect(scopes).toBeUndefined();
    expect(oauthService.getGrantedScopes).toHaveBeenCalled();
  });

  it('provides loggedIn false on no claim', () => {
    oauthService.getIdentityClaims.and.callThrough();
    const loggedIn = authService.loggedIn;
    expect(oauthService.getIdentityClaims).toHaveBeenCalled();
    expect(loggedIn).toBeFalse();
  });

  it('provides loggedIn true on user claimed', () => {
    oauthService.getIdentityClaims.and.returnValue({ name: 'me', email: 'me@sbb.ch', roles: [] });
    const loggedIn = authService.loggedIn;
    expect(loggedIn).toBeTrue();
  });

  it('logs in with oauthService', () => {
    authService.login();
    expect(oauthService.initCodeFlow).toHaveBeenCalled();
  });

  it('logs out with oauthService', () => {
    authService.logout();
    expect(oauthService.logOut).toHaveBeenCalled();
  });

  it('checks for roles correctly', () => {
    let result = authService.containsAnyRole([Role.LidiWriter], [Role.LidiWriter, Role.LidiAdmin]);
    expect(result).toBeTrue();

    result = authService.containsAnyRole([Role.LidiWriter], [Role.LidiWriter]);
    expect(result).toBeTrue();

    result = authService.containsAnyRole([Role.LidiWriter], [Role.LidiAdmin]);
    expect(result).toBeFalse();

    result = authService.containsAnyRole([Role.LidiWriter, Role.LidiAdmin], [Role.LidiAdmin]);
    expect(result).toBeTrue();

    result = authService.containsAnyRole([Role.LidiWriter, Role.LidiAdmin], [Role.LidiWriter]);
    expect(result).toBeTrue();

    result = authService.containsAnyRole([Role.LidiWriter, Role.LidiAdmin], []);
    expect(result).toBeFalse();

    result = authService.containsAnyRole(
      [Role.LidiWriter, Role.LidiAdmin],
      [Role.BoWriter, Role.BoAdmin]
    );
    expect(result).toBeFalse();
  });
});
