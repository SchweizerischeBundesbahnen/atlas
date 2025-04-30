import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationUserEditComponent } from './user-administration-user-edit.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { UserService } from '../../../service/user.service';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import { Observable, of } from 'rxjs';
import { NotificationService } from '../../../../../core/notification/notification.service';
import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisationsService,
  User,
} from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import SpyObj = jasmine.SpyObj;

describe('UserAdministrationUserEditComponent', () => {
  let component: UserAdministrationUserEditComponent;
  let fixture: ComponentFixture<UserAdministrationUserEditComponent>;

  let userServiceSpy: SpyObj<UserService>;
  let userPermissionManagerSpy: SpyObj<UserPermissionManager>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let boServiceSpy: SpyObj<BusinessOrganisationsService>;
  let dialogServiceSpy: SpyObj<DialogService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj<UserService>('UserService', [
      'getPermissionsFromUserModelAsArray',
      'updateUserPermission',
    ]);
    userPermissionManagerSpy = jasmine.createSpyObj(
      'UserPermissionManager',
      [
        'setSbbUserId',
        'setPermissions',
        'clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser',
        'emitBoFormResetEvent',
      ],
      {
        userPermission: {
          sbbUserId: 'u123456',
          permissions: [],
        },
      }
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
            provide: UserPermissionManager,
            useValue: userPermissionManagerSpy,
          },
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
          provide: UserService,
          useValue: userServiceSpy,
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
          provide: ActivatedRoute,
          useValue: { snapshot: { data: { user: {} } } },
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationUserEditComponent);
    component = fixture.componentInstance;
    component.user = {};
    userServiceSpy.getPermissionsFromUserModelAsArray.and.returnValue([]);
    component.userRecord = {};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test ngOnInit', () => {
    expect(
      userServiceSpy.getPermissionsFromUserModelAsArray
    ).toHaveBeenCalledOnceWith({});
    expect(component.user).toBeUndefined();
    expect(userPermissionManagerSpy.setSbbUserId).not.toHaveBeenCalled();
    expect(userPermissionManagerSpy.setPermissions).not.toHaveBeenCalled();
  });

  it('test saveEdits', () => {
    userServiceSpy.updateUserPermission.and.returnValue(
      of({
        sbbUserId: 'u123456',
      })
    );

    component.saveEdits();

    expect(
      userPermissionManagerSpy.clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser
    ).toHaveBeenCalledOnceWith();
    expect(
      userPermissionManagerSpy.emitBoFormResetEvent
    ).toHaveBeenCalledOnceWith();
    expect(userServiceSpy.updateUserPermission).toHaveBeenCalledOnceWith({
      permissions: [],
      sbbUserId: 'u123456',
    });
    expect(component.user).toEqual({ sbbUserId: 'u123456' });
    expect(component.editMode).toBeFalse();
    expect(component.saveEnabled).toBeFalse();
    expect(userPermissionManagerSpy.setPermissions).toHaveBeenCalledOnceWith(
      []
    );
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS'
    );

    userServiceSpy.updateUserPermission.and.returnValue(
      new Observable<User>((subscriber) => subscriber.error('error'))
    );
    component.saveEdits();
    expect(component.saveEnabled).toBeTrue();
  });

  it('test cancelEdit showDialog=false', () => {
    component.cancelEdit(false);
    expect(dialogServiceSpy.confirmLeave).not.toHaveBeenCalled();
  });

  it('test cancelEdit showDialog=true,confirmLeaveResult=true', () => {
    component.editMode = true;
    dialogServiceSpy.confirmLeave.and.returnValue(of(true));
    component.cancelEdit();
    expect(component.editMode).toBeFalse();
    expect(userPermissionManagerSpy.setPermissions).toHaveBeenCalledOnceWith(
      []
    );
    expect(
      userPermissionManagerSpy.emitBoFormResetEvent
    ).toHaveBeenCalledOnceWith();
  });

  it('test cancelEdit showDialog=true,confirmLeaveResult=false', () => {
    component.editMode = true;
    dialogServiceSpy.confirmLeave.and.returnValue(of(false));
    component.cancelEdit();
    expect(component.editMode).toBeTrue();
    expect(userPermissionManagerSpy.setPermissions).not.toHaveBeenCalled();
  });

  it('shows first creation and last edition', () => {
    userServiceSpy.getPermissionsFromUserModelAsArray.and.returnValue([
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
    ]);
    component.editMode = true;
    component.user = { sbbUserId: 'yb56789' };

    fixture.detectChanges();
    component.ngOnInit();

    expect(component.userRecord).toBeTruthy();
    expect(component.userRecord!.creationDate).toBe('2020-01-01');
    expect(component.userRecord!.creator).toBe('me');
    expect(component.userRecord!.editionDate).toBe('2020-01-06');
    expect(component.userRecord!.editor).toBe('sumotherdude');
  });
});
