import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationClientEditComponent } from './user-administration-client-edit.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { Component } from '@angular/core';
import { MaterialModule } from '../../../../../core/module/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../../service/user.service';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { BusinessOrganisationsService } from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { MockUserDetailInfoComponent } from '../../../../../app.testing.mocks';
import SpyObj = jasmine.SpyObj;

@Component({
  selector: 'app-dialog-close',
  template: '',
})
class MockDialogCloseComponent {}

describe('UserAdministrationClientEditComponent', () => {
  let component: UserAdministrationClientEditComponent;
  let fixture: ComponentFixture<UserAdministrationClientEditComponent>;

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
      [
        'setSbbUserId',
        'setPermissions',
        'clearPermissionRestrictionsIfNotWriter',
        'emitBoFormResetEvent',
      ],
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
    await TestBed.overrideComponent(UserAdministrationClientEditComponent, {
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
        UserAdministrationClientEditComponent,
        MockDialogCloseComponent,
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

    fixture = TestBed.createComponent(UserAdministrationClientEditComponent);
    component = fixture.componentInstance;
    component.client = {};
    userServiceSpy.getPermissionsFromUserModelAsArray.and.returnValue([]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
