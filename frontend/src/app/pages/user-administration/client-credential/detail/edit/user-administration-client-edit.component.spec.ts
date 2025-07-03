import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationClientEditComponent } from './user-administration-client-edit.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from '../../../../../core/notification/notification.service';
import {
  BusinessOrganisationsService,
  ClientCredential,
} from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ClientCredentialAdministrationService } from '../../../../../api/service/user-administration/client-credential-administration.service';
import { UserPermissionGivenClientService } from './user-permission-given-client.service';
import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import SpyObj = jasmine.SpyObj;

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

  let clientCredentialAdministrationServiceSpy: SpyObj<ClientCredentialAdministrationService>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let boServiceSpy: SpyObj<BusinessOrganisationsService>;
  let dialogServiceSpy: SpyObj<DialogService>;

  beforeEach(async () => {
    clientCredentialAdministrationServiceSpy =
      jasmine.createSpyObj<ClientCredentialAdministrationService>(
        'ClientCredentialAdministrationService',
        ['updateClientCredentialPermissions']
      );
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'success',
    ]);
    boServiceSpy = jasmine.createSpyObj('BusinessOrganisationService', [
      'getAllBusinessOrganisations',
    ]);
    dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirmLeave']);
    dialogMock.closeCalled = false;
    TestBed.overrideComponent(UserAdministrationClientEditComponent, {
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
        UserAdministrationClientEditComponent,
        TranslateModule.forRoot(),
      ],
      providers: [
        TranslatePipe,
        { provide: MatDialogRef, useValue: dialogMock },
        {
          provide: UserPermissionGivenClientService,
        },
        {
          provide: UserPermissionProviderService,
          useClass: UserPermissionGivenClientService,
        },
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationServiceSpy,
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
          useValue: { paramMap: new Subject() },
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationClientEditComponent);
    component = fixture.componentInstance;
    const clientCredential: ClientCredential = {
      clientCredentialId: 'clientCredentialId',
      permissions: new Set(),
      alias: 'alias',
      comment: 'comment',
    };
    fixture.componentRef.setInput('client', clientCredential);
    component.record = {};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
