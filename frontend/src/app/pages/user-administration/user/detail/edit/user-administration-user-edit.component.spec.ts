import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationUserEditComponent } from './user-administration-user-edit.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { NotificationService } from '../../../../../core/notification/notification.service';
import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisationsService,
  Permission,
  User,
  UserDisplayName,
} from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';
import { UserPermissionGivenUserService } from './user-permission-given-user.service';
import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import SpyObj = jasmine.SpyObj;

describe('UserAdministrationUserEditComponent', () => {
  let component: UserAdministrationUserEditComponent;
  let fixture: ComponentFixture<UserAdministrationUserEditComponent>;

  let userAdministrationServiceSpy: SpyObj<UserAdministrationService>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let boServiceSpy: SpyObj<BusinessOrganisationsService>;
  let dialogServiceSpy: SpyObj<DialogService>;

  beforeEach(async () => {
    userAdministrationServiceSpy =
      jasmine.createSpyObj<UserAdministrationService>(
        'UserAdministrationService',
        ['updateUserPermission', 'getUserDisplayName']
      );
    const userDisplayName: UserDisplayName = {
      sbbUserId: 'u123456',
      displayName: 'UserDisplayName',
    };
    userAdministrationServiceSpy.getUserDisplayName.and.returnValue(
      of(userDisplayName)
    );
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'success',
    ]);
    boServiceSpy = jasmine.createSpyObj('BusinessOrganisationService', [
      'getAllBusinessOrganisations',
    ]);
    dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirmLeave']);
    TestBed.overrideComponent(UserAdministrationUserEditComponent, {
      set: {
        viewProviders: [
          {
            provide: BusinessOrganisationsService,
            useValue: boServiceSpy,
          },
        ],
      },
    });
    await TestBed.configureTestingModule({
      imports: [UserAdministrationUserEditComponent, TranslateModule.forRoot()],
      providers: [
        TranslatePipe,
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceSpy,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
        },
        {
          provide: DialogService,
          useValue: dialogServiceSpy,
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
    }).compileComponents();

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
    userAdministrationServiceSpy.updateUserPermission.and.returnValue(
      of({
        sbbUserId: 'u123456',
        permissions: new Set<Permission>(),
      })
    );

    component.saveUser();

    expect(
      userAdministrationServiceSpy.updateUserPermission
    ).toHaveBeenCalledOnceWith('u123456', ApplicationType.Ttfn, {
      role: ApplicationRole.Reader,
      application: ApplicationType.Ttfn,
      permissionRestrictions: [],
    });
    expect(component.editMode).toBeFalse();
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS'
    );

    userAdministrationServiceSpy.updateUserPermission.and.returnValue(
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
});
