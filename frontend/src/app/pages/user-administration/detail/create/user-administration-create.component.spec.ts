import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { UserAdministrationCreateComponent } from './user-administration-create.component';
import { UserService } from '../../service/user.service';
import SpyObj = jasmine.SpyObj;
import { BusinessOrganisationsService } from '../../../../api';
import { NotificationService } from '../../../../core/notification/notification.service';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { Component, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserModel } from '../../../../api/model/userModel';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../../core/module/material.module';
import { FormGroup } from '@angular/forms';
import { UserPermissionManager } from '../../user-permission-manager';

@Component({
  selector: 'app-user-select',
  template: '',
})
class MockUserSelectComponent {
  @Input() form?: FormGroup;
}

@Component({
  selector: 'app-dialog-close',
  template: '',
})
class MockDialogCloseComponent {}

describe('UserAdministrationCreateComponent', () => {
  let component: UserAdministrationCreateComponent;
  let fixture: ComponentFixture<UserAdministrationCreateComponent>;

  let userServiceSpy: SpyObj<UserService>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let userPermissionManagerSpy: SpyObj<UserPermissionManager>;
  let boServiceSpy: SpyObj<BusinessOrganisationsService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', [
      'getUser',
      'getPermissionsFromUserModelAsArray',
      'createUserPermission',
    ]);
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['success']);
    userPermissionManagerSpy = jasmine.createSpyObj<UserPermissionManager>(
      'UserPermissionManager',
      ['setSbbUserId', 'clearSboidsIfNotWriter', 'getUserPermission', 'getSbbUserId']
    );
    boServiceSpy = jasmine.createSpyObj<BusinessOrganisationsService>(
      'BusinessOrganisationsService',
      ['getAllBusinessOrganisations']
    );
    await TestBed.overrideComponent(UserAdministrationCreateComponent, {
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
      declarations: [
        UserAdministrationCreateComponent,
        MockUserSelectComponent,
        MockDialogCloseComponent,
      ],
      imports: [
        RouterTestingModule,
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        {
          provide: UserService,
          useValue: userServiceSpy,
        },
        {
          provide: UserPermissionManager,
          useValue: userPermissionManagerSpy,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
        },
        TranslatePipe,
        {
          provide: MAT_DIALOG_DATA,
          useValue: { user: undefined },
        },
        {
          provide: MatDialogRef,
          useValue: {
            close: () => {
              // mock implementation
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.userLoaded).toBeUndefined();
    expect(component.userHasAlreadyPermissions).toBe(false);
    expect(component.selectedUserHasNoUserId).toBe(false);
    expect(component.userPermissionManager).toBe(userPermissionManagerSpy);
  });

  it('test selectUser without userId', () => {
    component.selectUser({
      lastName: 'test',
    });
    expect(component.selectedUserHasNoUserId).toBe(true);
    expect(component.userHasAlreadyPermissions).toBe(false);
    expect(component.userLoaded).toBeUndefined();
    expect(userServiceSpy.getUser).not.toHaveBeenCalled();
  });

  it('test selectUser with valid user', () => {
    userServiceSpy.getUser.and.callFake((userId) =>
      of({
        sbbUserId: userId,
      })
    );
    userServiceSpy.getPermissionsFromUserModelAsArray.and.callFake((user: UserModel) =>
      Array.from(user.permissions ?? [])
    );
    component.selectUser({
      sbbUserId: 'u236171',
    });
    expect(component.selectedUserHasNoUserId).toBe(false);
    expect(component.userHasAlreadyPermissions).toBe(false);
    expect(component.userLoaded).toEqual({
      sbbUserId: 'u236171',
    });
    expect(userServiceSpy.getUser).toHaveBeenCalledOnceWith('u236171');
    expect(userServiceSpy.getPermissionsFromUserModelAsArray).toHaveBeenCalledOnceWith({
      sbbUserId: 'u236171',
    });
  });

  it('test createUser', fakeAsync(() => {
    const router = TestBed.inject(Router);
    component.userLoaded = {
      sbbUserId: 'u236171',
    };
    userServiceSpy.createUserPermission.and.returnValue(
      of({
        sbbUserId: 'u236171',
      })
    );
    spyOn(router, 'navigate').and.resolveTo(true);
    component.createUser();
    expect(userPermissionManagerSpy.setSbbUserId).toHaveBeenCalledOnceWith('u236171');
    expect(userPermissionManagerSpy.clearSboidsIfNotWriter).toHaveBeenCalledOnceWith();
    expect(userServiceSpy.createUserPermission).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    tick();
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'
    );
  }));
});
