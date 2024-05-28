import {AuthService} from '../auth.service';
import {TestBed} from '@angular/core/testing';
import {OAuthLogger, OAuthService, UrlHelperService} from 'angular-oauth2-oidc';
import {AuthGuard} from './auth-guard';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterModule} from "@angular/router";
import {adminUserServiceMock, authServiceSpy} from "../../../app.testing.mocks";
import {UserService} from "../user/user.service";

describe('AuthGuard', () => {
  let authGuard: AuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([]), HttpClientTestingModule],
      providers: [
        { provide: UserService, useValue: adminUserServiceMock },
        { provide: AuthService, useValue: authServiceSpy },
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
