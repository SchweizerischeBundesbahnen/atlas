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

  let servicePointServiceSpy: Mocked<Pick<ServicePointInternalService, 'updateGlobalId' | 'deleteGlobalId'>>;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let detailHelperServiceSpy: Mocked<Pick<DetailDialogHelperService, 'confirmLeaveDirtyForm'>>;
  let dialogRefSpy: Mocked<Pick<MatDialogRef<GlobalIdEditDialogComponent>, 'close'>>;

  const dialogData: GlobalIdEditDialogData = {
    servicePointNumber: 8001653,
    country: 'GERMANY',
    globalId: 'de:05770:1282',
  };

  function setup(data: GlobalIdEditDialogData = dialogData) {
    TestBed.resetTestingModule();
    servicePointServiceSpy = {
      updateGlobalId: vi.fn().mockReturnValue(of([])),
      deleteGlobalId: vi.fn().mockReturnValue(of([])),
    };
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
    expect(servicePointServiceSpy.deleteGlobalId).not.toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalledWith('SEPODI.SERVICE_POINTS.GLOBAL_ID_EDIT.SUCCESS');
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('should not save when the global-id is empty', () => {
    component.form.controls.globalId.setValue('');

    component.save();

    expect(component.form.controls.globalId.invalid).toBeTruthy();
    expect(servicePointServiceSpy.updateGlobalId).not.toHaveBeenCalled();
    expect(servicePointServiceSpy.deleteGlobalId).not.toHaveBeenCalled();
    expect(dialogRefSpy.close).not.toHaveBeenCalled();
  });

  it('should allow deleting when an existing global-id is present', () => {
    expect(component.canDelete).toBe(true);
  });

  it('should not allow deleting when no global-id is present', () => {
    setup({ servicePointNumber: 8001653, country: 'GERMANY' });

    expect(component.canDelete).toBe(false);
  });

  it('should delete the global-id via the delete interface, notify and close with true', () => {
    component.delete();

    expect(servicePointServiceSpy.deleteGlobalId).toHaveBeenCalledWith(8001653);
    expect(servicePointServiceSpy.updateGlobalId).not.toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalledWith('SEPODI.SERVICE_POINTS.GLOBAL_ID_EDIT.DELETE_SUCCESS');
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('should close with false when the backend rejects the delete', () => {
    servicePointServiceSpy.deleteGlobalId.mockReturnValue(throwError(() => new Error('rejected')));

    component.delete();

    expect(notificationServiceSpy.success).not.toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
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
