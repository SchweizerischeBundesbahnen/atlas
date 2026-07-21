import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

import { GlobalIdEditDialogComponent, GlobalIdEditDialogData } from './global-id-edit-dialog.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { ServicePointInternalService } from '../../../../api/service/sepodi/service-point-internal.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DetailDialogHelperService } from '../../../../core/detail/detail-dialog-helper.service';

describe('GlobalIdEditDialogComponent', () => {
  let component: GlobalIdEditDialogComponent;
  let fixture: ComponentFixture<GlobalIdEditDialogComponent>;

  let servicePointServiceSpy: Mocked<Pick<ServicePointInternalService, 'updateGlobalId'>>;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let detailHelperServiceSpy: Mocked<Pick<DetailDialogHelperService, 'confirmLeaveDirtyForm'>>;
  let dialogRefSpy: Mocked<Pick<MatDialogRef<GlobalIdEditDialogComponent>, 'close'>>;

  const dialogData: GlobalIdEditDialogData = {
    servicePointNumber: 8001653,
    country: 'GERMANY',
    globalId: 'de:05770:1282',
  };

  function setup(data: GlobalIdEditDialogData = dialogData) {
    servicePointServiceSpy = { updateGlobalId: vi.fn().mockReturnValue(of([])) };
    notificationServiceSpy = { success: vi.fn() };
    detailHelperServiceSpy = { confirmLeaveDirtyForm: vi.fn().mockReturnValue(of(true)) };
    dialogRefSpy = { close: vi.fn() };

    TestBed.configureTestingModule({
      imports: [AppTestingModule, GlobalIdEditDialogComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: data },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: ServicePointInternalService, useValue: servicePointServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: DetailDialogHelperService, useValue: detailHelperServiceSpy },
        { provide: TranslatePipe },
      ],
    });

    fixture = TestBed.createComponent(GlobalIdEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(() => setup());

  it('should create and prefill the form with the given global-id', () => {
    expect(component).toBeTruthy();
    expect(component.form.controls.globalId.value).toEqual('de:05770:1282');
    expect(component.form.controls.country.value).toEqual('GERMANY');
  });

  it('should save a valid global-id, notify and close with true', () => {
    component.save();

    expect(servicePointServiceSpy.updateGlobalId).toHaveBeenCalledWith(8001653, { globalId: 'de:05770:1282' });
    expect(notificationServiceSpy.success).toHaveBeenCalledWith('SEPODI.SERVICE_POINTS.GLOBAL_ID_EDIT.SUCCESS');
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('should send undefined when clearing the global-id', () => {
    component.form.controls.globalId.setValue('');

    component.save();

    expect(servicePointServiceSpy.updateGlobalId).toHaveBeenCalledWith(8001653, { globalId: undefined });
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('should not save when the prefix does not match the country', () => {
    component.form.controls.globalId.setValue('at:42:9379');

    component.save();

    expect(component.form.controls.globalId.invalid).toBeTruthy();
    expect(servicePointServiceSpy.updateGlobalId).not.toHaveBeenCalled();
    expect(dialogRefSpy.close).not.toHaveBeenCalled();
  });

  it('should close with false when the backend rejects the update', () => {
    servicePointServiceSpy.updateGlobalId.mockReturnValue(throwError(() => new Error('rejected')));

    component.save();

    expect(notificationServiceSpy.success).not.toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });

  it('should close with false when leaving is confirmed on cancel', () => {
    component.cancel();

    expect(detailHelperServiceSpy.confirmLeaveDirtyForm).toHaveBeenCalledWith(component.form);
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });
});
