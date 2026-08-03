import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { mock } from 'vitest-mock-extended';
import { UserAdministrationUserEditComponent } from './user-administration-user-edit.component';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ApplicationRole, ApplicationType, Permission, User, UserDisplayName } from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';
import { UserPermissionGivenUserService } from './user-permission-given-user.service';
import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../../app.testing.mocks';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';

describe('UserAdministrationUserEditComponent', () => {
  let component: UserAdministrationUserEditComponent;
  let fixture: ComponentFixture<UserAdministrationUserEditComponent>;

  let userAdministrationService: Mocked<Pick<UserAdministrationService, 'updateUserPermission' | 'getUserDisplayName'>>;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;
  let dialogService: ReturnType<typeof mock<DialogService>>;

  beforeEach(() => {
    userAdministrationService = {
      updateUserPermission: vi.fn(),
      getUserDisplayName: vi.fn(),
    };
    const userDisplayName: UserDisplayName = {
      sbbUserId: 'u123456',
      displayName: 'UserDisplayName',
    };
    userAdministrationService.getUserDisplayName.mockReturnValue(of(userDisplayName));
    notificationService = {
      success: vi.fn(),
    };
    dialogService = mock<DialogService>();
    TestBed.configureTestingModule({
      imports: [UserAdministrationUserEditComponent],
      providers: [
        translateServiceProvider,
        TranslatePipe,
        {
          provide: UserAdministrationService,
          useValue: userAdministrationService,
        },
        {
          provide: NotificationService,
          useValue: notificationService,
        },
        {
          provide: DialogService,
          useValue: dialogService,
        },
        {
          provide: PermissionService,
          useValue: adminPermissionServiceMock,
        },
        {
          provide: UserPermissionGivenUserService,
        },
        {
          provide: UserPermissionProviderService,
          useExisting: UserPermissionGivenUserService,
        },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { data: { user: {} } } },
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    fixture = TestBed.createComponent(UserAdministrationUserEditComponent);
    component = fixture.componentInstance;

    const user: User = {
      sbbUserId: 'u123456',
      permissions: new Set<Permission>([
        {
          creationDate: '2020-01-01',
          creator: 'me',
          editionDate: '2020-01-05',
          editor: 'sumotherdude',
          role: ApplicationRole.Supervisor,
          application: ApplicationType.Lidi,
          permissionRestrictions: [],
        },
        {
          creationDate: '2020-01-02',
          creator: 'me',
          editionDate: '2020-01-06',
          editor: 'sumotherdude',
          role: ApplicationRole.Reader,
          application: ApplicationType.Ttfn,
          permissionRestrictions: [],
        },
      ]),
    };
    fixture.componentRef.setInput('user', user);

    const givenUserService = TestBed.inject(UserPermissionGivenUserService);
    givenUserService.user = user;
    givenUserService.loadFormGroup(ApplicationType.Ttfn);

    component.userRecord = {};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test saveEdits', () => {
    userAdministrationService.updateUserPermission.mockReturnValue(
      of({
        sbbUserId: 'u123456',
        permissions: new Set<Permission>(),
      })
    );

    component.saveUser();

    expect(userAdministrationService.updateUserPermission).toHaveBeenCalledExactlyOnceWith(
      'u123456',
      ApplicationType.Ttfn,
      {
        role: ApplicationRole.Reader,
        application: ApplicationType.Ttfn,
        permissionRestrictions: [],
      }
    );
    expect(component.editMode).toBe(false);
    expect(notificationService.success).toHaveBeenCalledExactlyOnceWith('USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS');

    userAdministrationService.updateUserPermission.mockReturnValue(
      new Observable<User>((subscriber) => subscriber.error('error'))
    );
    component.saveUser();
  });

  it('shows first creation and last edition', () => {
    component.editMode = true;
    const user: User = {
      sbbUserId: 'yb56789',
      permissions: new Set<Permission>([
        {
          creationDate: '2020-01-01',
          creator: 'me',
          editionDate: '2020-01-05',
          editor: 'sumotherdude',
          role: ApplicationRole.Supervisor,
          application: ApplicationType.Lidi,
          permissionRestrictions: [],
        },
        {
          creationDate: '2020-01-02',
          creator: 'me',
          editionDate: '2020-01-06',
          editor: 'sumotherdude',
          role: ApplicationRole.Supervisor,
          application: ApplicationType.Ttfn,
          permissionRestrictions: [],
        },
      ]),
    };
    fixture.componentRef.setInput('user', user);

    fixture.detectChanges();
    component.ngOnInit();

    expect(component.userRecord).toBeTruthy();
    expect(component.userRecord!.creationDate).toBe('2020-01-01');
    expect(component.userRecord!.creator).toBe('me');
    expect(component.userRecord!.editionDate).toBe('2020-01-06');
    expect(component.userRecord!.editor).toBe('sumotherdude');
  });

  it('should toggleEdit', () => {
    expect(component.editMode).toBe(false);

    component.toggleEdit();
    expect(component.editMode).toBe(true);

    component.toggleEdit();
    expect(component.editMode).toBe(false);
  });

  it('should display the manual mail row below the e-mail row', () => {
    const givenUserService = TestBed.inject(UserPermissionGivenUserService);
    givenUserService.user = { ...givenUserService.user, manualMailOverride: 'manual@sbb.ch' };
    fixture.detectChanges();

    expect(component.displayUser.manualMailOverride).toBe('manual@sbb.ch');
    const compiled = fixture.nativeElement as HTMLElement;
    const emailRowIndex = Array.from(compiled.querySelectorAll('.d-inline-flex')).findIndex((el) =>
      el.textContent?.includes('PROFILE.EMAIL')
    );
    const manualMailOverrideRowIndex = Array.from(compiled.querySelectorAll('.d-inline-flex')).findIndex((el) =>
      el.textContent?.includes('USER_ADMIN.MANUAL_MAIL')
    );
    expect(manualMailOverrideRowIndex).toBe(emailRowIndex + 1);
    expect(compiled.querySelector('[data-cy="manual-mail-override-value"]')?.textContent).toContain('manual@sbb.ch');
  });

  it('should display an empty manual mail row when no override exists', () => {
    fixture.detectChanges();

    expect(component.displayUser.manualMailOverride).toBeUndefined();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('[data-cy="manual-mail-override-value"]')?.textContent?.trim()).toBe('');
  });

  it('should reload the user after the dialog saved a manual mail', () => {
    const updatedUser: User = {
      sbbUserId: 'u123456',
      permissions: new Set<Permission>(),
      manualMailOverride: 'new-manual@sbb.ch',
    };
    dialogService.openDialogDataWithCustomResult.mockReturnValue(of(updatedUser));

    component.editManualMail();

    expect(dialogService.openDialogDataWithCustomResult).toHaveBeenCalledOnce();
    expect(component.displayUser).toEqual(updatedUser);
  });

  it('should not reload the user when the dialog is cancelled', () => {
    const givenUserService = TestBed.inject(UserPermissionGivenUserService);
    const originalUser = givenUserService.user;
    dialogService.openDialogDataWithCustomResult.mockReturnValue(of(undefined));

    component.editManualMail();

    expect(component.displayUser).toEqual(originalUser);
  });
});
