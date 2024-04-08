import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { OAuthService } from 'angular-oauth2-oidc';
import {of, Subject} from 'rxjs';
import { Role } from './role';
import { Component } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  ApplicationRole,
  ApplicationType,
  CantonPermissionRestrictionModel,
  PermissionRestrictionType,
  User,
  UserAdministrationService,
} from '../../api';
import {RouterModule} from "@angular/router";
import {Pages} from "../../pages/pages";
import {delay} from "rxjs/operators";

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

const userAdministrationService = jasmine.createSpyObj('userAdministrationService', [
  'getCurrentUser']);

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
        {provide: UserAdministrationService, useValue: userAdministrationService},
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
    let result = authService.containsAnyRole([Role.AtlasAdmin], [Role.AtlasAdmin]);
    expect(result).toBeTrue();

    result = authService.containsAnyRole([Role.AtlasAdmin], []);
    expect(result).toBeFalse();
  });

  describe('Permissions for create Button', () => {
    it('Permissions for create Button BODI are set up correctly', () => {
      let result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [],
        true
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [
          {
            application: ApplicationType.Bodi,
            role: ApplicationRole.SuperUser,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [
          {
            application: ApplicationType.Bodi,
            role: ApplicationRole.Supervisor,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [
          {
            application: ApplicationType.Bodi,
            role: ApplicationRole.Writer,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [
          {
            application: ApplicationType.Bodi,
            role: ApplicationRole.Reader,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();
    });

    it('Permissions for create Button LIDI are set up correctly', () => {
      let result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [],
        true
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.SuperUser,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Supervisor,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Writer,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Reader,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();
    });

    it('Permissions for create Button TTFN are set up correctly', () => {
      let result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [],
        true
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [
          {
            application: ApplicationType.Ttfn,
            role: ApplicationRole.SuperUser,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [
          {
            application: ApplicationType.Ttfn,
            role: ApplicationRole.Supervisor,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [
          {
            application: ApplicationType.Ttfn,
            role: ApplicationRole.Writer,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [
          {
            application: ApplicationType.Ttfn,
            role: ApplicationRole.Reader,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();
    });
  });

  describe('Permissions for edit Button', () => {
    it('LIDI setup correctly', () => {
      let result = AuthService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [],
        true
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Supervisor,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.SuperUser,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = AuthService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Writer,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();

      result = AuthService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Writer,
            permissionRestrictions: [
              {
                valueAsString: 'ch:1:slnid:1000004',
                type: PermissionRestrictionType.BusinessOrganisation,
              },
            ],
          },
        ],
        false
      );
      expect(result).toBeTrue();
    });
  });

  describe('Permission for edit TTH Canton', () => {
    it('should be able to edit Canton if user is for canton enabled', () => {
      const cantonRestriction: CantonPermissionRestrictionModel[] = [];
      cantonRestriction.push({ type: 'CANTON', valueAsString: 'BERN' });
      const result = AuthService.hasPermissionToWriteOnCanton(
        ApplicationType.TimetableHearing,
        'be',
        [
          {
            application: ApplicationType.TimetableHearing,
            role: ApplicationRole.Writer,
            permissionRestrictions: cantonRestriction,
          },
        ],
        false
      );
      expect(result).toBeTrue();
    });

    it('should not be able to edit Canton if user is not for canton enabled', () => {
      const cantonRestriction: CantonPermissionRestrictionModel[] = [];
      cantonRestriction.push({ type: 'CANTON', valueAsString: 'BERN' });
      const result = AuthService.hasPermissionToWriteOnCanton(
        ApplicationType.TimetableHearing,
        'zh',
        [
          {
            application: ApplicationType.TimetableHearing,
            role: ApplicationRole.Writer,
            permissionRestrictions: cantonRestriction,
          },
        ],
        false
      );
      expect(result).toBeFalsy();
    });

    it('should be able to edit Canton if user is admin', () => {
      const result = AuthService.hasPermissionToWriteOnCanton(
        ApplicationType.TimetableHearing,
        'be',
        [],
        true
      );
      expect(result).toBeTrue();
    });
  });

  describe('Available Pages based on permissions', () => {

    const fakeToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3MTI1Nzk0MjgsIm5iZiI6MTcxMjU3OTQyOCwiZXhwIjoxNzEyNTg0ODQ2LCJhaW8iOiJBV1FBbS84V0FBQUFlL1pLbDYrdGpFSmF6TVh6ZE9uYkNqa3F6M3I0Y1poTjdISHFXeEZjSmNUS2xtdHFUZVpoWmdmOEtkd3NLMTZxK2o2T2JObFNBaUl3OGFIZUNjYWs2aWwzNTgwRFBJZTZTRHlXMWVSbndscUxqSlZTWFFYSWlESjdjYVZpd3gwSyIsImF6cCI6IjE4NzQ2ZjMwLTc5NzgtNDhiNS1iMTliLTBmODcxZmIxMmU2NyIsImF6cGFjciI6IjAiLCJuYW1lIjoiTWUiLCJvaWQiOiI4MjMxYzY0Yi1iY2I0LTQ5N2UtOTNiZi0wMjEzZWJkYWM5ZTQiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJtZUBzYmIuY2giLCJyaCI6IjAuQVlJQUVWM2FMS3p3czBhV2ZhOGJMaHZRR2pUbTVvZWhhM3BPaHAwelNMVEQ2dnlDQUVNLiIsInJvbGVzIjpbImFwaW0tZGVmYXVsdC1yb2xlIl0sInNjcCI6ImFwaW0iLCJzdWIiOiJUWDExMFJvQXl2b3QyQlRISVU2M0hMS1llQU03WWV6c21JWVRTdVNjVDJrIiwidGlkIjoiMmNkYTVkMTEtZjBhYy00NmIzLTk2N2QtYWYxYjJlMWJkMDFhIiwidXRpIjoiT01xS2VheTFua3UzemhVZEgwYkxBQSIsInZlciI6IjIuMCIsInNiYnVpZCI6ImU1MjQzODEifQ.O5CzmblVS7EVuBRgzMF00_UVkTaS-47euK_ZvdkEUuM';

    it('should show TTFN if at least supervisor', () => {
      oauthService.getIdentityClaims.and.returnValue({ name: 'me', email: 'me@sbb.ch', roles: [] });
      oauthService.getAccessToken.and.returnValue(fakeToken);

      const user: User = {
        sbbUserId: 'e132456',
        permissions: new Set([{
          application: ApplicationType.Ttfn,
          role: ApplicationRole.Supervisor,
          permissionRestrictions:[]
        }])
      };
      userAdministrationService.getCurrentUser.and.returnValue(of(user))

      authService.permissionsLoaded
        .pipe(delay(300))
        .subscribe(loaded => {
        if (loaded) {
          expect(Pages.viewablePages.filter(i => i.path === Pages.TTFN.path).length).toBe(1);
        }
      })
      expect(authService.loggedIn).toBeTrue();
    });

    it('should not show TTFN if reader', () => {
      oauthService.getIdentityClaims.and.returnValue({ name: 'me', email: 'me@sbb.ch', roles: [] });
      oauthService.getAccessToken.and.returnValue(fakeToken);

      const user: User = {
        sbbUserId: 'e132456',
        permissions: new Set([{
          application: ApplicationType.Ttfn,
          role: ApplicationRole.Reader,
          permissionRestrictions:[]
        }])
      };
      userAdministrationService.getCurrentUser.and.returnValue(of(user))

      authService.permissionsLoaded
        .pipe(delay(300))
        .subscribe(loaded => {
          if (loaded) {
            expect(Pages.viewablePages.filter(i => i.path === Pages.TTFN.path).length).toBe(0);
          }
        })
      expect(authService.loggedIn).toBeTrue();
    });

  });
});
