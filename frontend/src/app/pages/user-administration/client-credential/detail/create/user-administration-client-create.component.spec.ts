import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { UserAdministrationClientCreateComponent } from './user-administration-client-create.component';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { of, Subject } from 'rxjs';
import { provideRouter, Router } from '@angular/router';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClientCredentialAdministrationService } from '../../../../../api/service/user-administration/client-credential-administration.service';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { provideZonelessChangeDetection } from '@angular/core';

describe('UserAdministrationClientCreateComponent', () => {
  let component: UserAdministrationClientCreateComponent;
  let fixture: ComponentFixture<UserAdministrationClientCreateComponent>;

  let clientCredentialAdministrationService: Mocked<
    Pick<ClientCredentialAdministrationService, 'createClientCredential'>
  >;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;
  let dialogService: Mocked<Pick<DialogService, 'confirmLeave'>>;

  beforeEach(() => {
    clientCredentialAdministrationService = {
      createClientCredential: vi.fn(),
    };
    notificationService = {
      success: vi.fn(),
    };
    dialogService = {
      confirmLeave: vi.fn().mockReturnValue(of(true)),
    };

    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
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
          provide: DialogService,
          useValue: dialogService,
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
    component.form.clientCredentialId().value.set('client-id');
    component.form.alias().value.set('alias');

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

  it('rejects invalid input and displays validation errors', () => {
    component.form.clientCredentialId().value.set(' leading-space');
    component.form.alias().value.set('alias');

    component.create();

    expect(component.form().touched()).toBe(true);
    expect(component.form().valid()).toBe(false);
    expect(clientCredentialAdministrationService.createClientCredential).not.toHaveBeenCalled();
    expect(
      component.form
        .clientCredentialId()
        .errors()
        .some((error) => error.kind === 'whitespaces')
    ).toBe(true);
  });

  it('enforces the existing maximum lengths and character set', () => {
    component.form.clientCredentialId().value.set('a'.repeat(51));
    component.form.alias().value.set('a'.repeat(101));
    component.form.comment().value.set('€');

    component.create();

    expect(
      component.form
        .clientCredentialId()
        .errors()
        .some((error) => error.kind === 'maxlength')
    ).toBe(true);
    expect(
      component.form
        .alias()
        .errors()
        .some((error) => error.kind === 'maxlength')
    ).toBe(true);
    expect(
      component.form
        .comment()
        .errors()
        .some((error) => error.kind === 'pattern')
    ).toBe(true);
    expect(clientCredentialAdministrationService.createClientCredential).not.toHaveBeenCalled();
  });

  it('disables saving during a request and re-enables it on failure', () => {
    const request = new Subject<never>();
    clientCredentialAdministrationService.createClientCredential.mockReturnValue(request);
    component.form.clientCredentialId().value.set('client-id');
    component.form.alias().value.set('alias');

    component.create();
    component.create();

    expect(component.saveEnabled()).toBe(false);
    expect(clientCredentialAdministrationService.createClientCredential).toHaveBeenCalledOnce();
    request.error(new Error('Request failed'));
    expect(component.saveEnabled()).toBe(true);
  });

  it('asks before cancelling after the form was touched', () => {
    component.form.alias().markAsTouched();
    component.cancelCreation();

    expect(dialogService.confirmLeave).toHaveBeenCalledOnce();
  });
});
