import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { UserAdministrationUserCreateComponent } from './user-administration-user-create.component';
import { UserService } from '../../../service/user.service';
import { BusinessOrganisationsService } from '../../../../../api';
import { NotificationService } from '../../../../../core/notification/notification.service';
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
import { User } from '../../../../../api';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../../../core/module/material.module';
import { FormGroup } from '@angular/forms';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import SpyObj = jasmine.SpyObj;

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

describe('UserAdministrationUserCreateComponent', () => {
  let component: UserAdministrationUserCreateComponent;
  let fixture: ComponentFixture<UserAdministrationUserCreateComponent>;

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
      ['setSbbUserId', 'clearPermissionRestrictionsIfNotWriter', 'getSbbUserId']
    );
    boServiceSpy = jasmine.createSpyObj<BusinessOrganisationsService>(
      'BusinessOrganisationsService',
      ['getAllBusinessOrganisations']
    );
    await TestBed.overrideComponent(UserAdministrationUserCreateComponent, {
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
        UserAdministrationUserCreateComponent,
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

    fixture = TestBed.createComponent(UserAdministrationUserCreateComponent);
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
    userServiceSpy.getPermissionsFromUserModelAsArray.and.callFake((user: User) =>
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
    expect(
      userPermissionManagerSpy.clearPermissionRestrictionsIfNotWriter
    ).toHaveBeenCalledOnceWith();
    expect(userServiceSpy.createUserPermission).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    tick();
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'
    );
  }));
});
