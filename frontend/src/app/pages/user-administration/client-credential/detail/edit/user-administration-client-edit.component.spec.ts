import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { UserAdministrationClientEditComponent } from './user-administration-client-edit.component';
import { TranslatePipe } from '@ngx-translate/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ApplicationRole, ApplicationType, ClientCredential, PermissionRestrictionType } from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ClientCredentialAdministrationService } from '../../../../../api/service/user-administration/client-credential-administration.service';
import { UserPermissionGivenClientService } from './user-permission-given-client.service';
import { UserPermissionProviderService } from '../../../../../core/components/permissions/application-permission/user-permission-provider-service';
import { translateServiceProvider } from '../../../../../app.testing.mocks';

describe('UserAdministrationClientEditComponent', () => {
  let component: UserAdministrationClientEditComponent;
  let fixture: ComponentFixture<UserAdministrationClientEditComponent>;

  let clientCredentialAdministrationService: Mocked<
    Pick<ClientCredentialAdministrationService, 'updateClientCredentialPermissions'>
  >;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;
  let dialogService: Mocked<Pick<DialogService, 'confirmLeave'>>;

  beforeEach(() => {
    clientCredentialAdministrationService = {
      updateClientCredentialPermissions: vi.fn().mockReturnValue(of()),
    };
    notificationService = {
      success: vi.fn(),
    };
    dialogService = {
      confirmLeave: vi.fn().mockReturnValue(of(true)),
    };
    TestBed.configureTestingModule({
      imports: [UserAdministrationClientEditComponent],
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
          useValue: notificationService,
        },
        {
          provide: DialogService,
          useValue: dialogService,
        },
        {
          provide: ActivatedRoute,
          useValue: { paramMap: new Subject() },
        },
        provideHttpClient(),
        provideHttpClientTesting(),
        translateServiceProvider,
      ],
    });

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
    expect(clientCredentialAdministrationService.updateClientCredentialPermissions).toHaveBeenCalledTimes(1);
  });

  it('should toggleEdit', () => {
    expect(component.editMode).toBe(false);

    component.toggleEdit();
    expect(component.editMode).toBe(true);

    component.toggleEdit();
    expect(component.editMode).toBe(false);
  });
});
