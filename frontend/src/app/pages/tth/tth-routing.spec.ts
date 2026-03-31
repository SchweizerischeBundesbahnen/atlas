import { TestBed } from '@angular/core/testing';
import {
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  type Mocked,
  vi,
} from 'vitest';
import { provideRouter, Router, Routes } from '@angular/router';
import {
  loadDossierDetailRoute,
  loadStatementDetailRoute,
  routes,
} from './tth-routing';
import { provideHttpClient } from '@angular/common/http';
import { PermissionService } from '../../core/auth/permission/permission.service';
import { Location } from '@angular/common';
import { DossierDetailResolver } from './dossier/detail/dossier-detail-resolver.service';
import { of } from 'rxjs';
import { UserService } from '../../core/auth/user/user.service';
import { provideHttpClientTesting } from '@angular/common/http/testing';

const testRoutes: Routes = [
  {
    path: 'timetable-hearing',
    children: routes,
  },
];

describe('TTH Routing', () => {
  let permissionServiceSpy: Mocked<
    Pick<PermissionService, 'getTthApplicationUserType'>
  >;
  let userServiceSpy: Mocked<Pick<UserService, 'onPermissionsLoaded'>>;

  beforeAll(async () => {
    await Promise.all([
      import('./overview-tab/overview-tab.component'),
      import('./tth-canton-routing'),
      import('./tth-bo-routing'),
      import('./tth-overview-base/tth-overview-base.component'),
      import('./overview-detail/overview-detail.component'),
    ]);
  }, 20_000);

  beforeEach(() => {
    permissionServiceSpy = {
      getTthApplicationUserType: vi.fn(),
    };
    userServiceSpy = {
      onPermissionsLoaded: vi.fn(),
    };
    userServiceSpy.onPermissionsLoaded.mockReturnValue(of(undefined));
  });

  it('should construct router with tth routes', () => {
    TestBed.configureTestingModule({
      providers: [provideRouter(routes)],
    });
    const router = TestBed.inject(Router);
    expect(router).toBeTruthy();
    const tthRoutes = router.config;
    expect(tthRoutes.length).toBe(8);
  });

  it('should load CantonStatementDetailComponent', () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue(
      'CANTON_TTH'
    );

    TestBed.configureTestingModule({
      providers: [
        provideRouter(routes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
      ],
    }).runInInjectionContext(async () => {
      const router = TestBed.inject(Router);
      expect(router).toBeTruthy();
      const result = loadStatementDetailRoute().then((component) => {
        expect(component).toBeDefined();
        expect(component.name).toBe('_CantonStatementDetailComponent');
      });
      expect(result).toBeDefined();
    });
  });

  it('should load BoStatementDetailComponent', () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue('BO_TTH');

    TestBed.configureTestingModule({
      providers: [
        provideRouter(routes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
      ],
    }).runInInjectionContext(async () => {
      const router = TestBed.inject(Router);
      expect(router).toBeTruthy();
      const result = loadStatementDetailRoute().then((component) => {
        expect(component).toBeDefined();
        expect(component.name).toBe('_BoStatementDetailComponent');
      });
      expect(result).toBeDefined();
    });
  });

  it('should load BoDossierDetailComponent', () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue('BO_TTH');

    TestBed.configureTestingModule({
      providers: [
        provideRouter(routes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
      ],
    }).runInInjectionContext(async () => {
      const router = TestBed.inject(Router);
      expect(router).toBeTruthy();
      const result = loadDossierDetailRoute().then((component) => {
        expect(component).toBeDefined();
        expect(component.name).toBe('_BoDossierDetailComponent');
      });
      expect(result).toBeDefined();
    });
  });

  it('should load CantonDossierDetailComponent', () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue(
      'CANTON_TTH'
    );

    TestBed.configureTestingModule({
      providers: [
        provideRouter(routes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
      ],
    }).runInInjectionContext(async () => {
      const router = TestBed.inject(Router);
      expect(router).toBeTruthy();
      const result = loadDossierDetailRoute().then((component) => {
        expect(component).toBeDefined();
        expect(component.name).toBe('_CantonDossierDetailComponent');
      });
      expect(result).toBeDefined();
    });
  });

  it('should redirect active to statements', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue(
      'CANTON_TTH'
    );

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/active');
    expect(location.path()).toBe('/timetable-hearing/zh/active/statements');
  });

  it('should redirect archived to statements', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue(
      'CANTON_TTH'
    );

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/archived');
    expect(location.path()).toBe('/timetable-hearing/zh/archived/statements');
  });

  it('should resolve archived dossier route', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue(
      'CANTON_TTH'
    );

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/archived/dossiers');
    expect(location.path()).toBe('/timetable-hearing/zh/archived/dossiers');
  });

  it('should resolve active dossier route', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue(
      'CANTON_TTH'
    );

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/active/dossiers');
    expect(location.path()).toBe('/timetable-hearing/zh/active/dossiers');
  });

  it('should redirect BO active to dossiers', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue('BO_TTH');

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/active');
    expect(location.path()).toBe('/timetable-hearing/zh/active/dossiers');
  });

  it('should redirect BO archived dossiers to active dossiers', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue('BO_TTH');

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/archived/dossiers/1000');
    expect(location.path()).toBe('/timetable-hearing/ch/active/dossiers');
  });

  it('should not redirect Canton archived dossiers', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue(
      'CANTON_TTH'
    );

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        {
          provide: DossierDetailResolver,
          useValue: { resolve: () => of(null) },
        },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/archived/dossiers/1000');
    expect(location.path()).toBe(
      '/timetable-hearing/zh/archived/dossiers/1000'
    );
  });

  it('should resolve BO active dossier route', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue('BO_TTH');

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/active/dossiers');
    expect(location.path()).toBe('/timetable-hearing/zh/active/dossiers');
  });

  it('should redirect unknown BO route to active', async () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue('BO_TTH');

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PermissionService, useValue: permissionServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
      ],
    });

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);

    await router.navigateByUrl('/timetable-hearing/zh/unknown-path');
    expect(location.path()).toContain('/timetable-hearing/zh/active');
  });
});
