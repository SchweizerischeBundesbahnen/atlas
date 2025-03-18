import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationClientEditComponent } from './user-administration-client-edit.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { Component, Input } from '@angular/core';
import { MaterialModule } from '../../../../../core/module/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../../service/user.service';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { BusinessOrganisationsService } from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { MockUserDetailInfoComponent } from '../../../../../app.testing.mocks';
import { Data } from '../../../components/read-only-data/data';
import { ReadOnlyData } from '../../../components/read-only-data/read-only-data';
import SpyObj = jasmine.SpyObj;
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import {DetailPageContentComponent} from "../../../../../core/components/detail-page-content/detail-page-content.component";

@Component({
    selector: 'app-user-administration-read-only-data',
    template: '',
    imports: [MaterialModule,
        RouterTestingModule]
})
export class MockUserAdministrationReadOnlyDataComponent<T extends Data> {
  @Input() data!: T;
  @Input() userModelConfig!: ReadOnlyData<T>[][];
}

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
    TestBed.overrideComponent(UserAdministrationClientEditComponent, {
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
    imports: [
        TranslateModule.forRoot({
            loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        MaterialModule,
        RouterTestingModule,
        UserAdministrationClientEditComponent,
        MockUserDetailInfoComponent,
        MockUserAdministrationReadOnlyDataComponent,
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
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
