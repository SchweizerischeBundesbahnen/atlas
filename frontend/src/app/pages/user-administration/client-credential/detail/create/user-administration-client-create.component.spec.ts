import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { UserAdministrationClientCreateComponent } from './user-administration-client-create.component';
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
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../../../core/module/material.module';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import SpyObj = jasmine.SpyObj;
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';

describe('UserAdministrationClientCreateComponent', () => {
  let component: UserAdministrationClientCreateComponent;
  let fixture: ComponentFixture<UserAdministrationClientCreateComponent>;

  let userServiceSpy: SpyObj<UserService>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let userPermissionManagerSpy: SpyObj<UserPermissionManager>;
  let boServiceSpy: SpyObj<BusinessOrganisationsService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['createClientCredentialPermission']);
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['success']);
    userPermissionManagerSpy = jasmine.createSpyObj<UserPermissionManager>(
      'UserPermissionManager',
      ['clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser'],
      {
        userPermission: {
          sbbUserId: '',
          permissions: [],
        },
      },
    );
    boServiceSpy = jasmine.createSpyObj<BusinessOrganisationsService>(
      'BusinessOrganisationsService',
      ['getAllBusinessOrganisations'],
    );
    await TestBed.overrideComponent(UserAdministrationClientCreateComponent, {
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
        UserAdministrationClientCreateComponent,
        DetailPageContainerComponent,
        DetailFooterComponent,
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

    fixture = TestBed.createComponent(UserAdministrationClientCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.userPermissionManager).toBe(userPermissionManagerSpy);
  });

  it('should create client', fakeAsync(() => {
    const router = TestBed.inject(Router);
    component.form.controls.clientCredentialId.setValue('client-id');
    userServiceSpy.createClientCredentialPermission.and.returnValue(
      of({
        clientCredentialId: 'client-id',
      }),
    );
    spyOn(router, 'navigate').and.resolveTo(true);
    component.create();
    expect(
      userPermissionManagerSpy.clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser,
    ).toHaveBeenCalledOnceWith();
    expect(userServiceSpy.createClientCredentialPermission).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    tick();
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS',
    );
  }));
});
