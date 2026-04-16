import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { UserAdministrationClientCreateComponent } from './user-administration-client-create.component';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { of } from 'rxjs';
import { provideRouter, Router } from '@angular/router';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClientCredentialAdministrationService } from '../../../../../api/service/user-administration/client-credential-administration.service';
import { translateServiceProvider } from '../../../../../app.testing.mocks';

describe('UserAdministrationClientCreateComponent', () => {
  let component: UserAdministrationClientCreateComponent;
  let fixture: ComponentFixture<UserAdministrationClientCreateComponent>;

  let clientCredentialAdministrationService: Mocked<
    Pick<ClientCredentialAdministrationService, 'createClientCredential'>
  >;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;

  beforeEach(() => {
    clientCredentialAdministrationService = {
      createClientCredential: vi.fn(),
    };
    notificationService = {
      success: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        provideRouter([]),
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationService,
        },
        {
          provide: NotificationService,
          useValue: notificationService,
        },
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
    });

    fixture = TestBed.createComponent(UserAdministrationClientCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create client', async () => {
    const router = TestBed.inject(Router);
    component.form.controls.clientCredentialId.setValue('client-id');
    component.form.controls.alias.setValue('alias');

    clientCredentialAdministrationService.createClientCredential.mockReturnValue(
      of({
        clientCredentialId: 'client-id',
      })
    );
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.create();

    expect(clientCredentialAdministrationService.createClientCredential).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    await fixture.whenStable();
    expect(notificationService.success).toHaveBeenCalledExactlyOnceWith('USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS');
  });
});
