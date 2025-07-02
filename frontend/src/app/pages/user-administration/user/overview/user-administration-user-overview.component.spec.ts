import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { of, Subject } from 'rxjs';
import { Permission } from '../../../../api';
import { UserAdministrationUserOverviewComponent } from './user-administration-user-overview.component';
import { adminPermissionServiceMock } from '../../../../app.testing.mocks';
import { TableService } from '../../../../core/components/table/table.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';

describe('UserAdministrationUserOverviewComponent', () => {
  let component: UserAdministrationUserOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationUserOverviewComponent>;

  const userAdministrationServiceMock = jasmine.createSpyObj(
    'UserAdministrationService',
    ['getUsers']
  );
  userAdministrationServiceMock.getUsers.and.returnValue(
    of({ objects: [], totalCount: 0 })
  );

  let tableService: TableService;

  afterEach(async () => {
    await userAdministrationServiceMock.getUsers.and.returnValue(
      of({ objects: [], totalCount: 0 })
    );
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        UserAdministrationUserOverviewComponent,
        TranslateModule.forRoot(),
      ],
      providers: [
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceMock,
        },
        {
          provide: PermissionService,
          useValue: adminPermissionServiceMock,
        },
        {
          provide: ActivatedRoute,
          useValue: { paramMap: new Subject() },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationUserOverviewComponent);
    component = fixture.componentInstance;
    tableService = fixture.debugElement.injector.get(TableService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test loadUsers', fakeAsync(() => {
    component.userSearchForm.get('userSearch')?.setValue('test');
    component.boForm.get('boSearch')?.setValue('test');
    component.selectedApplicationOptions = ['TTFN'];
    expect(component.userSearchForm.get('userSearch')?.value).toBe('test');
    expect(component.boForm.get('boSearch')?.value).toBe('test');

    userAdministrationServiceMock.getUsers.calls.reset();
    userAdministrationServiceMock.getUsers.and.returnValue(
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
    tick();
    expect(userAdministrationServiceMock.getUsers).toHaveBeenCalledOnceWith(
      5,
      5
    );
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
  }));

  it('test checkIfUserExists with undefined user', () => {
    spyOn(component, 'loadUsers');
    tableService.pageSize = 10;
    component.checkIfUserExists(undefined!);
    expect(component.loadUsers).toHaveBeenCalledOnceWith({ page: 0, size: 10 });
  });

  it('test checkIfUserExists normal', () => {
    tableService.pageIndex = 10;

    userAdministrationServiceMock.hasUserPermissions = jasmine
      .createSpy()
      .and.returnValue(of(true));
    component.checkIfUserExists({
      sbbUserId: 'u123456',
      permissions: new Set<Permission>(),
    });
    expect(component.userPageResult).toEqual({
      users: [
        {
          sbbUserId: 'u123456',
          permissions: new Set<Permission>(),
        },
      ],
      totalCount: 1,
    });
    expect(tableService.pageIndex).toBe(0);
  });

  it('test selectedSearchChanged', () => {
    spyOn(component, 'loadUsers');
    component.selectedSearchChanged();
    expect(component.loadUsers).toHaveBeenCalledOnceWith({ page: 0, size: 10 });
  });

  it('test filterChanged', () => {
    userAdministrationServiceMock.getUsers.calls.reset();
    userAdministrationServiceMock.getUsers.and.returnValue(
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

    expect(userAdministrationServiceMock.getUsers).toHaveBeenCalledOnceWith(
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
    spyOn(component, 'checkIfUserExists');
    tableService.pageSize = 10;
    tableService.pageIndex = 10;
    component.reloadTableWithCurrentSettings();
    expect(component.checkIfUserExists).toHaveBeenCalledOnceWith(null!, 10);
  });

  it('test reloadTableWithCurrentSettings, FILTER', () => {
    spyOn(component, 'filterChanged');
    tableService.pageSize = 10;
    tableService.pageIndex = 10;
    component.selectedSearch = 'FILTER';
    component.reloadTableWithCurrentSettings();
    expect(component.filterChanged).toHaveBeenCalledOnceWith(10);
  });
});
