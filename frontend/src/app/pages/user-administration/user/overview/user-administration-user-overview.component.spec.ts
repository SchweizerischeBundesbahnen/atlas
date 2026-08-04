import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRole, ApplicationType, Permission } from '../../../../api';
import { UserAdministrationUserOverviewComponent } from './user-administration-user-overview.component';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { TableService } from '../../../../core/components/table/table.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { FormatPipe } from '../../../../core/components/table/pipe/format.pipe';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

describe('UserAdministrationUserOverviewComponent', () => {
  let component: UserAdministrationUserOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationUserOverviewComponent>;

  let userAdministrationService: Mocked<Pick<UserAdministrationService, 'getUsers' | 'getUser' | 'getUserEmails'>>;
  let notificationService: Mocked<Pick<NotificationService, 'success' | 'info' | 'error'>>;
  let tableService: TableService;

  beforeEach(() => {
    userAdministrationService = {
      getUsers: vi.fn().mockReturnValue(of({ objects: [], totalCount: 0 })),
      getUser: vi.fn(),
      getUserEmails: vi.fn().mockReturnValue(of([])),
    };
    notificationService = {
      success: vi.fn(),
      info: vi.fn(),
      error: vi.fn(),
    };
    TestBed.configureTestingModule({
      providers: [
        {
          provide: UserAdministrationService,
          useValue: userAdministrationService,
        },
        {
          provide: NotificationService,
          useValue: notificationService,
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

  it('should pass the mail already resolved by the backend through unchanged', () => {
    userAdministrationService.getUsers.mockClear();
    userAdministrationService.getUsers.mockReturnValue(
      of({
        objects: [
          {
            sbbUserId: 'u123456',
            mail: 'override.mail@sbb.ch',
            originalMail: 'azure.mail@sbb.ch',
            permissions: new Set<Permission>(),
          },
          {
            sbbUserId: 'e654321',
            mail: 'azure-only.mail@sbb.ch',
            originalMail: 'azure-only.mail@sbb.ch',
            permissions: new Set<Permission>(),
          },
        ],
        totalCount: 2,
      })
    );

    component.loadUsers({ page: 0, size: 10 });

    expect(component.userPageResult.users.map((user) => user.mail)).toEqual([
      'override.mail@sbb.ch',
      'azure-only.mail@sbb.ch',
    ]);
  });

  it('should pass the mail for a single user fetched via onUserFilterChanged through unchanged', () => {
    userAdministrationService.getUser.mockReturnValue(
      of({
        sbbUserId: 'u123456',
        mail: 'override.mail@sbb.ch',
        originalMail: 'azure.mail@sbb.ch',
        permissions: new Set<Permission>([
          {
            role: ApplicationRole.Reader,
            application: ApplicationType.Ttfn,
            permissionRestrictions: [],
          },
        ]),
      })
    );

    component.onUserFilterChanged({
      sbbUserId: 'u123456',
      permissions: new Set<Permission>(),
    });

    expect(component.userPageResult.users[0].mail).toBe('override.mail@sbb.ch');
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

  it('should not render the copy button in USER search mode', () => {
    component.selectedSearch = 'USER';
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button).toBeNull();
  });

  it('should render the copy button in FILTER search mode', () => {
    component.selectedSearch = 'FILTER';
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button).not.toBeNull();
  });

  it('should render the copy button in FILTER_CANTON search mode', () => {
    component.selectedSearch = 'FILTER_CANTON';
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button).not.toBeNull();
  });

  it('should disable the copy button when there are no results', () => {
    component.selectedSearch = 'FILTER';
    component.userPageResult = { users: [], totalCount: 0 };
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button.disabled).toBe(true);
  });

  it('should send the same filter params as filterChanged for FILTER mode', () => {
    component.selectedSearch = 'FILTER';
    component.boForm.get('boSearch')?.setValue('ch:1:sboid:100000');
    component.applicationChanged([ApplicationType.Lidi]);

    component.filterChanged();
    const filterChangedArgs = userAdministrationService.getUsers.mock.calls.at(-1);

    userAdministrationService.getUserEmails.mockClear();
    component.copyFilteredEmails();

    expect(userAdministrationService.getUserEmails).toHaveBeenCalledExactlyOnceWith(
      filterChangedArgs![2],
      filterChangedArgs![3],
      filterChangedArgs![4]
    );
  });

  it('should send the same filter params as filterChanged for FILTER_CANTON mode', () => {
    component.selectedSearch = 'FILTER_CANTON';
    component.cantonChanged(['BERN']);
    component.applicationChanged([ApplicationType.TimetableHearing]);

    component.filterChanged();
    const filterChangedArgs = userAdministrationService.getUsers.mock.calls.at(-1);

    userAdministrationService.getUserEmails.mockClear();
    component.copyFilteredEmails();

    expect(userAdministrationService.getUserEmails).toHaveBeenCalledExactlyOnceWith(
      filterChangedArgs![2],
      filterChangedArgs![3],
      filterChangedArgs![4]
    );
  });

  it('should copy the joined emails to the clipboard and show a success notification', async () => {
    const writeText = vi.fn().mockResolvedValue(undefined);
    Object.assign(navigator, { clipboard: { writeText } });
    userAdministrationService.getUserEmails.mockReturnValue(of(['a@sbb.ch', 'b@sbb.ch']));

    component.copyFilteredEmails();
    await Promise.resolve();
    await Promise.resolve();

    expect(writeText).toHaveBeenCalledExactlyOnceWith('a@sbb.ch; b@sbb.ch');
    expect(notificationService.success).toHaveBeenCalledExactlyOnceWith('COMMON.COPY_CLIPBOARD_SUCCESS');
    expect(component.copyingEmails).toBe(false);
  });

  it('should show an info notification and not touch the clipboard when there are no emails', () => {
    const writeText = vi.fn().mockResolvedValue(undefined);
    Object.assign(navigator, { clipboard: { writeText } });
    userAdministrationService.getUserEmails.mockReturnValue(of([]));

    component.copyFilteredEmails();

    expect(writeText).not.toHaveBeenCalled();
    expect(notificationService.info).toHaveBeenCalledExactlyOnceWith('USER_ADMIN.COPY_EMAILS_EMPTY');
    expect(component.copyingEmails).toBe(false);
  });

  it('should show an error notification and reset the loading flag when the request fails', () => {
    const error = new HttpErrorResponse({ status: 400 });
    userAdministrationService.getUserEmails.mockReturnValue(throwError(() => error));

    component.copyFilteredEmails();

    expect(notificationService.error).toHaveBeenCalledExactlyOnceWith(error);
    expect(component.copyingEmails).toBe(false);
  });

  it('should set the loading flag while the request is in flight', () => {
    const subject = new Subject<string[]>();
    userAdministrationService.getUserEmails.mockReturnValue(subject);

    component.copyFilteredEmails();
    expect(component.copyingEmails).toBe(true);

    subject.next([]);
    subject.complete();
    expect(component.copyingEmails).toBe(false);
  });
});
