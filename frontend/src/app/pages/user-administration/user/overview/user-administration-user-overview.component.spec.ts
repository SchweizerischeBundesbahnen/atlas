import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { provideZonelessChangeDetection } from '@angular/core';
import { ApplicationRole, ApplicationType, Permission, SwissCanton, User } from '../../../../api';
import { UserAdministrationUserOverviewComponent } from './user-administration-user-overview.component';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { TableService } from '../../../../core/components/table/table.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { BusinessOrganisationService } from '../../../../api/service/bodi/business-organisation.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { FormatPipe } from '../../../../core/components/table/pipe/format.pipe';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

describe('UserAdministrationUserOverviewComponent', () => {
  let component: UserAdministrationUserOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationUserOverviewComponent>;

  let userAdministrationService: Mocked<Pick<UserAdministrationService, 'getUsers' | 'getUser' | 'getUserEmails'>>;
  let businessOrganisationService: Mocked<Pick<BusinessOrganisationService, 'getAllBusinessOrganisations'>>;
  let notificationService: Mocked<Pick<NotificationService, 'success' | 'info' | 'error'>>;
  let tableService: TableService;

  beforeEach(() => {
    userAdministrationService = {
      getUsers: vi.fn().mockReturnValue(of({ objects: [], totalCount: 0 })),
      getUser: vi.fn(),
      getUserEmails: vi.fn().mockReturnValue(of([])),
    };
    businessOrganisationService = {
      getAllBusinessOrganisations: vi.fn().mockReturnValue(of({ objects: [] })),
    };
    notificationService = {
      success: vi.fn(),
      info: vi.fn(),
      error: vi.fn(),
    };
    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        {
          provide: UserAdministrationService,
          useValue: userAdministrationService,
        },
        {
          provide: BusinessOrganisationService,
          useValue: businessOrganisationService,
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
    component.userSearchForm.userSearch().value.set({ sbbUserId: 'test', permissions: new Set<Permission>() });
    component.boSearchForm.boSearch().value.set('test');
    component.selectedApplicationOptions.set([ApplicationType.Ttfn]);
    expect(component.userSearchForm.userSearch().value()?.sbbUserId).toBe('test');
    expect(component.boSearchForm.boSearch().value()).toBe('test');

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
    expect(component.userSearchForm.userSearch().value()).toBeNull();
    expect(component.userSearchForm().value()).toEqual({ userSearch: null });
    expect(component.boSearchForm().value()).toEqual({ boSearch: null });
    expect(component.selectedApplicationOptions()).toEqual([]);
    expect(component.userPageResult()).toEqual({
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

    expect(component.userPageResult().users.map((user) => user.mail)).toEqual([
      'override.mail@sbb.ch',
      'azure-only.mail@sbb.ch',
    ]);
  });

  it('renders an asynchronous page response without manual change detection', async () => {
    const response = new Subject<{ objects: User[]; totalCount: number }>();
    userAdministrationService.getUsers.mockReturnValue(response);

    component.loadUsers({ page: 0, size: 5 });
    response.next({
      objects: [{ sbbUserId: 'u123456', lastName: 'Zoneless', permissions: new Set<Permission>() }],
      totalCount: 1,
    });
    await fixture.whenStable();

    expect(fixture.nativeElement.querySelector('table').textContent).toContain('Zoneless');
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

    expect(component.userPageResult().users[0].mail).toBe('override.mail@sbb.ch');
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
    expect(component.userPageResult()).toEqual({
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
    component.selectedSearchChanged('FILTER');
    expect(component.loadUsers).toHaveBeenCalledExactlyOnceWith({
      page: 0,
      size: 10,
    });
    expect(component.selectedSearch()).toBe('FILTER');
  });

  it('clears canton and application selections when changing search mode', () => {
    component.selectedSearch.set('FILTER_CANTON');
    component.cantonChanged([SwissCanton.Bern]);
    component.applicationChanged([ApplicationType.TimetableHearing]);

    component.selectedSearchChanged('FILTER');

    expect(component.selectedCantonOptions()).toEqual([]);
    expect(component.selectedApplicationOptions()).toEqual([]);
    expect(component.userSearchForm().value()).toEqual({ userSearch: null });
    expect(component.boSearchForm().value()).toEqual({ boSearch: null });
  });

  it('keeps only the latest page response when requests overlap', () => {
    const firstPage = new Subject<{ objects: User[]; totalCount: number }>();
    const secondPage = new Subject<{ objects: User[]; totalCount: number }>();
    userAdministrationService.getUsers.mockReturnValueOnce(firstPage).mockReturnValueOnce(secondPage);

    component.loadUsers({ page: 0, size: 5 });
    component.loadUsers({ page: 2, size: 5 });
    firstPage.next({ objects: [{ sbbUserId: 'old', permissions: new Set<Permission>() }], totalCount: 1 });
    secondPage.next({ objects: [{ sbbUserId: 'new', permissions: new Set<Permission>() }], totalCount: 15 });

    expect(component.userPageResult().users[0].sbbUserId).toBe('new');
    expect(component.userPageResult().totalCount).toBe(15);
    expect(tableService.pageIndex).toBe(2);
    expect(tableService.pageSize).toBe(5);
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
      new Set([]),
      'CANTON',
      new Set([])
    );
    expect(component.userPageResult()).toEqual({
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
    component.selectedSearch.set('FILTER');
    component.reloadTableWithCurrentSettings();
    expect(component.filterChanged).toHaveBeenCalledExactlyOnceWith(10);
  });

  it('should not render the copy button in USER search mode', () => {
    component.selectedSearch.set('USER');
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button).toBeNull();
  });

  it('should render the copy button in FILTER search mode', () => {
    component.selectedSearch.set('FILTER');
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button).not.toBeNull();
  });

  it('switches search mode through the radio group without ngModel', async () => {
    const radios = fixture.nativeElement.querySelectorAll('mat-radio-button input[type="radio"]');
    radios[1].click();
    await fixture.whenStable();

    expect(component.selectedSearch()).toBe('FILTER');
    expect(fixture.nativeElement.querySelector('button.atlas-primary-btn')).not.toBeNull();
  });

  it('should render the copy button in FILTER_CANTON search mode', () => {
    component.selectedSearch.set('FILTER_CANTON');
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button).not.toBeNull();
  });

  it('should disable the copy button when there are no results', () => {
    component.selectedSearch.set('FILTER');
    component.userPageResult.set({ users: [], totalCount: 0 });
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button.disabled).toBe(true);
  });

  it('should disable the copy button when no filter criteria are set', () => {
    // Given: results exist, but neither application, business organisation nor canton is selected
    component.selectedSearch.set('FILTER');
    component.userPageResult.set({ users: [], totalCount: 42 });

    // When
    fixture.detectChanges();

    // Then
    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button.disabled).toBe(true);
  });

  it('should enable the copy button when an application type is selected', () => {
    component.selectedSearch.set('FILTER');
    component.userPageResult.set({ users: [], totalCount: 42 });
    component.applicationChanged([ApplicationType.Lidi]);

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button.disabled).toBe(false);
  });

  it('should enable the copy button when a business organisation is selected', async () => {
    component.selectedSearch.set('FILTER');
    component.userPageResult.set({ users: [], totalCount: 42 });
    component.boSearchForm.boSearch().value.set('ch:1:sboid:100000');
    expect(component.boSearchForm.boSearch().value()).toBe('ch:1:sboid:100000');

    await fixture.whenStable();

    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button.disabled).toBe(false);
  });

  it('updates the copy button without manual change detection when results arrive', async () => {
    component.selectedSearch.set('FILTER');
    component.applicationChanged([ApplicationType.Lidi]);
    await fixture.whenStable();

    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button.disabled).toBe(true);

    component.userPageResult.set({ users: [], totalCount: 42 });
    await fixture.whenStable();
    expect(button.disabled).toBe(false);

    const emails = new Subject<string[]>();
    userAdministrationService.getUserEmails.mockReturnValue(emails);
    component.copyFilteredEmails();
    await fixture.whenStable();
    expect(button.disabled).toBe(true);

    emails.next([]);
    emails.complete();
    await fixture.whenStable();
    expect(button.disabled).toBe(false);
  });

  it('should enable the copy button when a canton is selected', () => {
    component.selectedSearch.set('FILTER_CANTON');
    component.userPageResult.set({ users: [], totalCount: 42 });
    component.cantonChanged([SwissCanton.Bern]);

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button.atlas-primary-btn');
    expect(button.disabled).toBe(false);
  });

  it('should send the same filter params as filterChanged for FILTER mode', () => {
    component.selectedSearch.set('FILTER');
    component.boSearchForm.boSearch().value.set('ch:1:sboid:100000');
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
    component.selectedSearch.set('FILTER_CANTON');
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
    expect(component.copyingEmails()).toBe(false);
  });

  it('should show an info notification and not touch the clipboard when there are no emails', () => {
    const writeText = vi.fn().mockResolvedValue(undefined);
    Object.assign(navigator, { clipboard: { writeText } });
    userAdministrationService.getUserEmails.mockReturnValue(of([]));

    component.copyFilteredEmails();

    expect(writeText).not.toHaveBeenCalled();
    expect(notificationService.info).toHaveBeenCalledExactlyOnceWith('USER_ADMIN.COPY_EMAILS_EMPTY');
    expect(component.copyingEmails()).toBe(false);
  });

  it('should show an error notification and reset the loading flag when the request fails', () => {
    const error = new HttpErrorResponse({ status: 400 });
    userAdministrationService.getUserEmails.mockReturnValue(throwError(() => error));

    component.copyFilteredEmails();

    expect(notificationService.error).toHaveBeenCalledExactlyOnceWith(error);
    expect(component.copyingEmails()).toBe(false);
  });

  it('should set the loading flag while the request is in flight', () => {
    const subject = new Subject<string[]>();
    userAdministrationService.getUserEmails.mockReturnValue(subject);

    component.copyFilteredEmails();
    expect(component.copyingEmails()).toBe(true);

    subject.next([]);
    subject.complete();
    expect(component.copyingEmails()).toBe(false);
  });
});
