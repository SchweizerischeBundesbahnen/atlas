import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationEditComponent } from './user-administration-edit.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { Component } from '@angular/core';
import { MaterialModule } from '../../../../core/module/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { EditTitlePipe } from '../../pipes/edit-title.pipe';
import SpyObj = jasmine.SpyObj;
import { UserService } from '../../service/user.service';
import { UserPermissionManager } from '../../service/user-permission-manager';
import { Observable, of } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { BusinessOrganisationsService } from '../../../../api';
import { User } from '../../../../api';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { MockUserDetailInfoComponent } from '../../../../app.testing.mocks';

@Component({
  selector: 'app-dialog-close',
  template: '',
})
class MockDialogCloseComponent {}

describe('UserAdministrationEditComponent', () => {
  let component: UserAdministrationEditComponent;
  let fixture: ComponentFixture<UserAdministrationEditComponent>;

  const dialogMock = {
    closeCalled: false,
    close: () => {
      // Mock implementation
      dialogMock.closeCalled = true;
    },
  };

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
      ['setSbbUserId', 'setPermissions', 'clearSboidsIfNotWriter', 'emitBoFormResetEvent'],
      {
        userPermission: {
          sbbUserId: 'u123456',
          permissions: [],
        },
      }
    );
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['success']);
    boServiceSpy = jasmine.createSpyObj('BusinessOrganisationService', [
      'getAllBusinessOrganisations',
    ]);
    dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirmLeave']);
    dialogMock.closeCalled = false;
    await TestBed.overrideComponent(UserAdministrationEditComponent, {
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
      declarations: [
        UserAdministrationEditComponent,
        MockDialogCloseComponent,
        EditTitlePipe,
        MockUserDetailInfoComponent,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        MaterialModule,
        RouterTestingModule,
      ],
      providers: [
        TranslatePipe,
        { provide: MatDialogRef, useValue: dialogMock },
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
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationEditComponent);
    component = fixture.componentInstance;
    component.user = {};
    userServiceSpy.getPermissionsFromUserModelAsArray.and.returnValue([]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test ngOnInit', () => {
    expect(userServiceSpy.getPermissionsFromUserModelAsArray).toHaveBeenCalledOnceWith({});
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

    expect(userPermissionManagerSpy.clearSboidsIfNotWriter).toHaveBeenCalledOnceWith();
    expect(userPermissionManagerSpy.emitBoFormResetEvent).toHaveBeenCalledOnceWith();
    expect(userServiceSpy.updateUserPermission).toHaveBeenCalledOnceWith({
      permissions: [],
      sbbUserId: 'u123456',
    });
    expect(component.user).toEqual({ sbbUserId: 'u123456' });
    expect(component.editMode).toBeFalse();
    expect(component.saveEnabled).toBeFalse();
    expect(userPermissionManagerSpy.setPermissions).toHaveBeenCalledOnceWith([]);
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
    expect(dialogMock.closeCalled).toBeTrue();
  });

  it('test cancelEdit showDialog=true,confirmLeaveResult=true', () => {
    component.editMode = true;
    dialogServiceSpy.confirmLeave.and.returnValue(of(true));
    component.cancelEdit();
    expect(dialogMock.closeCalled).toBeFalse();
    expect(component.editMode).toBeFalse();
    expect(userPermissionManagerSpy.setPermissions).toHaveBeenCalledOnceWith([]);
    expect(userPermissionManagerSpy.emitBoFormResetEvent).toHaveBeenCalledOnceWith();
  });

  it('test cancelEdit showDialog=true,confirmLeaveResult=false', () => {
    component.editMode = true;
    dialogServiceSpy.confirmLeave.and.returnValue(of(false));
    component.cancelEdit();
    expect(component.editMode).toBeTrue();
    expect(userPermissionManagerSpy.setPermissions).not.toHaveBeenCalled();
  });
});
