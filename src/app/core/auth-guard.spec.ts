import { AuthService } from './auth.service';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { OAuthLogger, OAuthService, UrlHelperService } from 'angular-oauth2-oidc';
import { AuthGuard } from './auth-guard';
import { HttpClientTestingModule } from '@angular/common/http/testing';

const authService: Partial<AuthService> = {
  loggedIn: true,
};

describe('AuthGuard', () => {
  let authGuard: AuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        OAuthService,
        AuthGuard,
        UrlHelperService,
        OAuthLogger,
      ],
    });

    authGuard = TestBed.inject(AuthGuard);
  });

  it('should be created', () => {
    expect(authGuard).toBeTruthy();
  });

  it('should be able to activate', () => {
    expect(authGuard.canActivate).toBeTruthy();
  });
});
