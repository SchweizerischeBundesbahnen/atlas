import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserAdministrationClientEditComponent } from './user-administration-client-edit.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import {
  ApplicationRole,
  ApplicationType,
  ClientCredential,
  PermissionRestrictionType,
} from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';
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

  let clientCredentialAdministrationService: SpyObj<ClientCredentialAdministrationService>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let dialogServiceSpy: SpyObj<DialogService>;

  beforeEach(async () => {
    clientCredentialAdministrationService =
      jasmine.createSpyObj<ClientCredentialAdministrationService>(
        'ClientCredentialAdministrationService',
        ['updateClientCredentialPermissions']
      );
    clientCredentialAdministrationService.updateClientCredentialPermissions.and.returnValue(
      of()
    );
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'success',
    ]);
    dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirmLeave']);
    dialogServiceSpy.confirmLeave.and.returnValue(of(true));
    await TestBed.configureTestingModule({
      imports: [
        UserAdministrationClientEditComponent,
        TranslateModule.forRoot(),
      ],
      providers: [
        TranslatePipe,
        {
          provide: UserPermissionGivenClientService,
        },
        {
          provide: UserPermissionProviderService,
          useExisting: UserPermissionGivenClientService,
        },
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationService,
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
      permissions: new Set([
        {
          role: ApplicationRole.Writer,
          application: ApplicationType.Ttfn,
          permissionRestrictions: [
            {
              type: PermissionRestrictionType.BusinessOrganisation,
              valueAsString: 'ch:1:sboid:12312',
            },
          ],
          editionDate: '',
        },
      ]),
      alias: 'alias',
      comment: 'comment',
    };
    fixture.componentRef.setInput('client', clientCredential);
    component.record = {};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();

    expect(component.record.editionDate).toBe('');
  });

  it('should save permissions', () => {
    component.saveClientCredential();
    expect(
      clientCredentialAdministrationService.updateClientCredentialPermissions
    ).toHaveBeenCalledTimes(1);
  });

  it('should toggleEdit', () => {
    expect(component.editMode).toBeFalse();

    component.toggleEdit();
    expect(component.editMode).toBeTrue();

    component.toggleEdit();
    expect(component.editMode).toBeFalse();
  });
});
