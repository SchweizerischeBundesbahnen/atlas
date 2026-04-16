import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { ApplicationRole, ApplicationType, Permission } from '../../../../api';
import { UserAdministrationUserOverviewComponent } from './user-administration-user-overview.component';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { TableService } from '../../../../core/components/table/table.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { FormatPipe } from '../../../../core/components/table/pipe/format.pipe';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

describe('UserAdministrationUserOverviewComponent', () => {
  let component: UserAdministrationUserOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationUserOverviewComponent>;

  let userAdministrationService: Mocked<Pick<UserAdministrationService, 'getUsers' | 'getUser'>>;
  let tableService: TableService;

  beforeEach(() => {
    userAdministrationService = {
      getUsers: vi.fn().mockReturnValue(of({ objects: [], totalCount: 0 })),
      getUser: vi.fn(),
    };
    TestBed.configureTestingModule({
      providers: [
        {
          provide: UserAdministrationService,
          useValue: userAdministrationService,
        },
        {
          provide: PermissionService,
          useValue: adminPermissionServiceMock,
        },
        {
          provide: ActivatedRoute,
          useValue: { paramMap: new Subject() },
        },
        FormatPipe,
        translateServiceProvider,
      ],
    });

    fixture = TestBed.createComponent(UserAdministrationUserOverviewComponent);
    component = fixture.componentInstance;
    tableService = fixture.debugElement.injector.get(TableService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test loadUsers', async () => {
    component.userSearchForm.get('userSearch')?.setValue('test');
    component.boForm.get('boSearch')?.setValue('test');
    component.selectedApplicationOptions = ['TTFN'];
    expect(component.userSearchForm.get('userSearch')?.value).toBe('test');
    expect(component.boForm.get('boSearch')?.value).toBe('test');

    userAdministrationService.getUsers.mockClear();
    userAdministrationService.getUsers.mockReturnValue(
      of({
        objects: [
          {
            sbbUserId: 'u123456',
            permissions: new Set<Permission>(),
          },
          {
            sbbUserId: 'e654321',
            permissions: new Set<Permission>(),
          },
        ],
        totalCount: 50,
      })
    );
    tableService.pageSize = 10;
    tableService.pageIndex = 10;

    component.loadUsers({ page: 5, size: 5 });

    expect(userAdministrationService.getUsers).toHaveBeenCalledExactlyOnceWith(5, 5);
    expect(component.userSearchForm.get('userSearch')?.value).toBeNull();
    expect(component.boForm.get('boSearch')?.value).toBeNull();
    expect(component.selectedApplicationOptions).toEqual([]);
    expect(component.userPageResult).toEqual({
      users: [
        {
          sbbUserId: 'u123456',
          permissions: new Set<Permission>(),
        },
        {
          sbbUserId: 'e654321',
          permissions: new Set<Permission>(),
        },
      ],
      totalCount: 50,
    });
    expect(tableService.pageIndex).toBe(5);
    expect(tableService.pageSize).toBe(5);
  });

  it('test checkIfUserExists with undefined user', () => {
    vi.spyOn(component, 'loadUsers');
    tableService.pageSize = 10;
    component.onUserFilterChanged(undefined!);
    expect(component.loadUsers).toHaveBeenCalledExactlyOnceWith({
      page: 0,
      size: 10,
    });
  });

  it('test checkIfUserExists normal', () => {
    userAdministrationService.getUser.mockReturnValue(
      of({
        sbbUserId: 'u123456',
        permissions: new Set<Permission>([
          {
            role: ApplicationRole.Reader,
            application: ApplicationType.Ttfn,
            permissionRestrictions: [],
          },
        ]),
      })
    );
    tableService.pageIndex = 10;

    component.onUserFilterChanged({
      sbbUserId: 'u123456',
      permissions: new Set<Permission>(),
    });
    expect(component.userPageResult).toEqual({
      users: [
        {
          sbbUserId: 'u123456',
          permissions: new Set<Permission>([
            {
              role: ApplicationRole.Reader,
              application: ApplicationType.Ttfn,
              permissionRestrictions: [],
            },
          ]),
        },
      ],
      totalCount: 1,
    });
    expect(tableService.pageIndex).toBe(0);
  });

  it('test selectedSearchChanged', () => {
    vi.spyOn(component, 'loadUsers');
    component.selectedSearchChanged();
    expect(component.loadUsers).toHaveBeenCalledExactlyOnceWith({
      page: 0,
      size: 10,
    });
  });

  it('test filterChanged', () => {
    userAdministrationService.getUsers.mockClear();
    userAdministrationService.getUsers.mockReturnValue(
      of({
        totalCount: 1,
        objects: [
          {
            sbbUserId: 'u123456',
            permissions: new Set<Permission>(),
          },
        ],
      })
    );

    tableService.pageSize = 10;
    tableService.pageIndex = 10;

    component.filterChanged();

    expect(userAdministrationService.getUsers).toHaveBeenCalledExactlyOnceWith(
      0,
      10,
      new Set([null]),
      'CANTON',
      new Set([])
    );
    expect(component.userPageResult).toEqual({
      totalCount: 1,
      users: [
        {
          sbbUserId: 'u123456',
          permissions: new Set<Permission>(),
        },
      ],
    });
    expect(tableService.pageIndex).toBe(0);
    expect(tableService.pageSize).toBe(10);
  });

  it('test reloadTableWithCurrentSettings, USER', () => {
    vi.spyOn(component, 'onUserFilterChanged');
    tableService.pageSize = 10;
    tableService.pageIndex = 10;
    component.reloadTableWithCurrentSettings();
    expect(component.onUserFilterChanged).toHaveBeenCalledExactlyOnceWith(null!, 10);
  });

  it('test reloadTableWithCurrentSettings, FILTER', () => {
    vi.spyOn(component, 'filterChanged');
    tableService.pageSize = 10;
    tableService.pageIndex = 10;
    component.selectedSearch = 'FILTER';
    component.reloadTableWithCurrentSettings();
    expect(component.filterChanged).toHaveBeenCalledExactlyOnceWith(10);
  });
});
