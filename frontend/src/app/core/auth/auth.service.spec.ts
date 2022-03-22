import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';
import { OAuthService } from 'angular-oauth2-oidc';
import { Subject } from 'rxjs';

function createOauthServiceSpy() {
  const oauthServiceSpy = jasmine.createSpyObj<OAuthService>('OAuthService', [
    'getIdentityClaims',
    'getGrantedScopes',
    'configure',
    'setupAutomaticSilentRefresh',
    'loadDiscoveryDocumentAndTryLogin',
    'initLoginFlow',
    'hasValidIdToken',
    'logOut',
  ]);
  oauthServiceSpy.loadDiscoveryDocumentAndTryLogin.and.returnValue(
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

describe('AuthService', () => {
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
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
    const claims = authService.claims;
    expect(claims).toBeUndefined();
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
    expect(oauthService.initLoginFlow).toHaveBeenCalled();
  });

  it('logs out with oauthService', () => {
    authService.logout();
    expect(oauthService.logOut).toHaveBeenCalled();
  });
});
