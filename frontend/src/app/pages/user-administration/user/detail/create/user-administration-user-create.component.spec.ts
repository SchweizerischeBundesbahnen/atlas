import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';

import { UserAdministrationUserCreateComponent } from './user-administration-user-create.component';
import { BusinessOrganisationsService, Permission } from '../../../../../api';
import { NotificationService } from '../../../../../core/notification/notification.service';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { of } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { Component, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';
import SpyObj = jasmine.SpyObj;

@Component({
  selector: 'app-user-select',
  template: '',
})
class MockUserSelectComponent {
  @Input() form?: FormGroup;
}

describe('UserAdministrationUserCreateComponent', () => {
  let component: UserAdministrationUserCreateComponent;
  let fixture: ComponentFixture<UserAdministrationUserCreateComponent>;

  let userAdministrationServiceSpy: SpyObj<UserAdministrationService>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let boServiceSpy: SpyObj<BusinessOrganisationsService>;

  beforeEach(async () => {
    userAdministrationServiceSpy = jasmine.createSpyObj('UserService', [
      'getUser',
      'getPermissionsFromUserModelAsArray',
      'createUserPermission',
    ]);
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'success',
    ]);
    boServiceSpy = jasmine.createSpyObj<BusinessOrganisationsService>(
      'BusinessOrganisationsService',
      ['getAllBusinessOrganisations']
    );
    TestBed.overrideComponent(UserAdministrationUserCreateComponent, {
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
      imports: [
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        UserAdministrationUserCreateComponent,
        MockUserSelectComponent,
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
      ],
      providers: [
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceSpy,
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
    expect(component.selectedUser).toBeUndefined();
    expect(component.userHasAlreadyPermissions).toBe(false);
    expect(component.selectedUserHasNoUserId).toBe(false);
  });

  it('test selectUser without userId', () => {
    component.selectUser({
      sbbUserId: 'uid',
      lastName: 'test',
      permissions: new Set(),
    });
    expect(component.selectedUserHasNoUserId).toBe(true);
    expect(component.userHasAlreadyPermissions).toBe(false);
    expect(component.selectedUser).toBeUndefined();
    expect(userAdministrationServiceSpy.getUser).not.toHaveBeenCalled();
  });

  it('test selectUser with valid user', () => {
    userAdministrationServiceSpy.getUser.and.callFake((userId) =>
      of({
        sbbUserId: userId,
        permissions: new Set<Permission>(),
      })
    );
    component.selectUser({
      sbbUserId: '***REMOVED***',
      permissions: new Set(),
    });
    expect(component.selectedUserHasNoUserId).toBe(false);
    expect(component.userHasAlreadyPermissions).toBe(false);
    expect(component.selectedUser).toEqual({
      sbbUserId: '***REMOVED***',
      permissions: new Set(),
    });
    expect(userAdministrationServiceSpy.getUser).toHaveBeenCalledOnceWith(
      '***REMOVED***'
    );
  });

  it('test createUser', fakeAsync(() => {
    const router = TestBed.inject(Router);
    component.selectedUser = {
      sbbUserId: '***REMOVED***',
      permissions: new Set(),
    };
    userAdministrationServiceSpy.createUserPermission.and.returnValue(
      of({
        sbbUserId: '***REMOVED***',
        permissions: new Set<Permission>(),
      })
    );
    spyOn(router, 'navigate').and.resolveTo(true);
    component.createUser();
    expect(
      userAdministrationServiceSpy.createUserPermission
    ).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    tick();
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'
    );
  }));
});
