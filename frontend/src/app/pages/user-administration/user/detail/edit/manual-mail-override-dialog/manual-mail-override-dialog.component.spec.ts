import {ComponentFixture, TestBed} from '@angular/core/testing';
import {beforeEach, describe, expect, it, type Mocked, vi} from 'vitest';
import {ManualMailOverrideDialogComponent, ManualMailOverrideDialogData,} from './manual-mail-override-dialog.component';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {TranslatePipe} from '@ngx-translate/core';
import {of} from 'rxjs';
import {AppTestingModule} from '../../../../../../app.testing.module';
import {Permission, User} from '../../../../../../api';
import {UserAdministrationService} from '../../../../../../api/service/user-administration/user-administration.service';
import {NotificationService} from '../../../../../../core/notification/notification.service';
import {DialogService} from '../../../../../../core/components/dialog/dialog.service';

const userWithManualMail: User = {
  sbbUserId: 'u123456',
  mail: 'azure@sbb.ch',
  manualMailOverride: 'manual@sbb.ch',
  permissions: new Set<Permission>(),
};

const dialogRefSpy: Mocked<Pick<MatDialogRef<ManualMailOverrideDialogComponent, User | undefined>, 'close'>> = {
  close: vi.fn(),
};

const userAdministrationService: Mocked<Pick<UserAdministrationService, 'updateManualMail' | 'deleteManualMail'>> = {
  updateManualMail: vi.fn(),
  deleteManualMail: vi.fn(),
};

const notificationService: Mocked<Pick<NotificationService, 'success'>> = {
  success: vi.fn(),
};

const dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>> = {
  openDialogDataWithConfirmationResult: vi.fn(),
};

function createComponent(
  dialogData: ManualMailOverrideDialogData
): ComponentFixture<ManualMailOverrideDialogComponent> {
  TestBed.configureTestingModule({
    imports: [AppTestingModule, ManualMailOverrideDialogComponent],
    providers: [
      { provide: MatDialogRef, useValue: dialogRefSpy },
      { provide: MAT_DIALOG_DATA, useValue: dialogData },
      { provide: UserAdministrationService, useValue: userAdministrationService },
      { provide: NotificationService, useValue: notificationService },
      { provide: DialogService, useValue: dialogService },
      { provide: TranslatePipe },
    ],
  });

  const fixture = TestBed.createComponent(ManualMailOverrideDialogComponent);
  fixture.detectChanges();
  return fixture;
}

describe('ManualMailOverrideDialogComponent', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should prefill the input with the existing manual mail', () => {
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: userWithManualMail,
    });

    expect(fixture.componentInstance.form.controls.manualMailOverride.value).toBe('manual@sbb.ch');
    expect(fixture.componentInstance.azureMail).toBe('azure@sbb.ch');
  });

  it('should disable save when the entered mail is invalid', () => {
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: userWithManualMail,
    });

    fixture.componentInstance.form.controls.manualMailOverride.setValue('not-an-email');

    expect(fixture.componentInstance.form.invalid).toBe(true);
  });

  it('should hide the delete button when no manual mail is set', () => {
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: { sbbUserId: 'u123456', mail: 'azure@sbb.ch', permissions: new Set<Permission>() },
    });

    expect(fixture.componentInstance.hasExistingOverride).toBe(false);
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('[data-cy="dialog-delete-button"]')).toBeNull();
  });

  it('should show the delete button when a manual mail exists', () => {
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: userWithManualMail,
    });

    expect(fixture.componentInstance.hasExistingOverride).toBe(true);
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('[data-cy="dialog-delete-button"]')).not.toBeNull();
  });

  it('should save the manual mail on confirm', () => {
    const updatedUser: User = { ...userWithManualMail, manualMailOverride: 'updated@sbb.ch' };
    userAdministrationService.updateManualMail.mockReturnValue(of(updatedUser));
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: userWithManualMail,
    });

    fixture.componentInstance.form.controls.manualMailOverride.setValue('updated@sbb.ch');
    fixture.componentInstance.confirm();

    expect(userAdministrationService.updateManualMail).toHaveBeenCalledExactlyOnceWith('u123456', 'updated@sbb.ch');
    expect(notificationService.success).toHaveBeenCalledExactlyOnceWith('USER_ADMIN.NOTIFICATIONS.MANUAL_MAIL_SAVED');
    expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith(updatedUser);
  });

  it('should delete the manual mail when the input is cleared and confirmed', () => {
    const updatedUser: User = { sbbUserId: 'u123456', mail: 'azure@sbb.ch', permissions: new Set<Permission>() };
    userAdministrationService.deleteManualMail.mockReturnValue(of(updatedUser));
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: userWithManualMail,
    });

    fixture.componentInstance.form.controls.manualMailOverride.setValue('');
    fixture.componentInstance.confirm();

    expect(userAdministrationService.deleteManualMail).toHaveBeenCalledExactlyOnceWith('u123456');
    expect(notificationService.success).toHaveBeenCalledExactlyOnceWith('USER_ADMIN.NOTIFICATIONS.MANUAL_MAIL_DELETED');
    expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith(updatedUser);
  });

  it('should delete the manual mail via the delete button after confirming', () => {
    const updatedUser: User = { sbbUserId: 'u123456', mail: 'azure@sbb.ch', permissions: new Set<Permission>() };
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));
    userAdministrationService.deleteManualMail.mockReturnValue(of(updatedUser));
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: userWithManualMail,
    });

    fixture.componentInstance.delete();

    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalledExactlyOnceWith({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.DELETE_CONFIRM',
    });
    expect(userAdministrationService.deleteManualMail).toHaveBeenCalledExactlyOnceWith('u123456');
    expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith(updatedUser);
  });

  it('should not delete the manual mail when the confirmation is dismissed', () => {
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(false));
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: userWithManualMail,
    });

    fixture.componentInstance.delete();

    expect(userAdministrationService.deleteManualMail).not.toHaveBeenCalled();
    expect(dialogRefSpy.close).not.toHaveBeenCalled();
  });

  it('should close the dialog without saving on cancel', () => {
    const fixture = createComponent({
      title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
      message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
      user: userWithManualMail,
    });

    fixture.componentInstance.cancel();

    expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith(undefined);
    expect(userAdministrationService.updateManualMail).not.toHaveBeenCalled();
    expect(userAdministrationService.deleteManualMail).not.toHaveBeenCalled();
  });
});
