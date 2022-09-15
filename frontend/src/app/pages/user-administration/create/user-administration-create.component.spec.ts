import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { UserAdministrationCreateComponent } from './user-administration-create.component';
import { UserService } from '../service/user.service';
import SpyObj = jasmine.SpyObj;
import { BusinessOrganisationsService, UserModel } from '../../../api';
import { NotificationService } from '../../../core/notification/notification.service';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MaterialModule } from '../../../core/module/material.module';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-user-administration-detail',
  template: '',
})
class MockUserAdministrationDetailComponent {
  @Input() userLoaded = undefined;
  @Input() userHasAlreadyPermissions = false;
  @Input() applicationConfigManager = undefined;
}

@Component({
  selector: 'app-user-select',
  template: '',
})
class MockUserSelectComponent {}

describe('UserAdministrationCreateComponent', () => {
  let component: UserAdministrationCreateComponent;
  let fixture: ComponentFixture<UserAdministrationCreateComponent>;

  let userServiceSpy: SpyObj<UserService>;
  let notificationServiceSpy: SpyObj<NotificationService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', [
      'getUser',
      'getPermissionsFromUserModelAsArray',
      'createUserPermission',
    ]);
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['success']);
    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationCreateComponent,
        MockUserAdministrationDetailComponent,
        MockUserSelectComponent,
      ],
      imports: [
        RouterTestingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        MaterialModule,
      ],
      providers: [
        {
          provide: UserService,
          useValue: userServiceSpy,
        },
        {
          provide: BusinessOrganisationsService,
          useValue: jasmine.createSpyObj('BusinessOrganisationsService', [
            'getAllBusinessOrganisations',
          ]),
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
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
    expect(component.userPermissionManager).toBeDefined();
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
    spyOn(component.userPermissionManager, 'setSbbUserId');
    spyOn(component.userPermissionManager, 'clearSboidsIfNotWriter');
    component.createUser();
    expect(component.userPermissionManager.setSbbUserId).toHaveBeenCalledOnceWith('u236171');
    expect(component.userPermissionManager.clearSboidsIfNotWriter).toHaveBeenCalledOnceWith();
    expect(userServiceSpy.createUserPermission).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    tick();
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'
    );
  }));
});
