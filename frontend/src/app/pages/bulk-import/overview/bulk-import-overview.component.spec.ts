import { BulkImportOverviewComponent } from './bulk-import-overview.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApplicationType, BusinessObjectType, ImportType } from '../../../api';
import { BulkImportFormGroupBuilder } from '../detail/bulk-import-form-group';
import { BehaviorSubject, EMPTY, of, throwError } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { Router } from '@angular/router';
import { FileDownloadService } from '../../../core/components/file-upload/file/file-download.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { LoadingSpinnerService } from '../../../core/components/loading-spinner/loading-spinner.service';
import { BulkImportService } from '../../../api/service/bulk/bulk-import.service';
import { UserAdministrationService } from '../../../api/service/user-administration/user-administration.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('BulkImportOverviewComponent', () => {
  let component: BulkImportOverviewComponent;
  let fixture: ComponentFixture<BulkImportOverviewComponent>;

  let bulkImportService: Mocked<Pick<BulkImportService, 'startBulkImport' | 'downloadTemplate'>>;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;
  let router: Mocked<Pick<Router, 'navigate'>>;
  let dialogService: Mocked<Pick<DialogService, 'showInfo'>>;
  let userAdminService: Mocked<Pick<UserAdministrationService, 'getCurrentUser'>>;

  beforeEach(() => {
    bulkImportService = {
      startBulkImport: vi.fn(),
      downloadTemplate: vi.fn(),
    };

    notificationService = {
      success: vi.fn(),
    };

    router = {
      navigate: vi.fn(),
    };
    router.navigate.mockResolvedValue(true);

    dialogService = {
      showInfo: vi.fn(),
    };

    userAdminService = {
      getCurrentUser: vi.fn(),
    };
    userAdminService.getCurrentUser.mockReturnValue(EMPTY);

    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        {
          provide: LoadingSpinnerService,
          useValue: { loading: new BehaviorSubject(false) },
        },
        { provide: BulkImportService, useValue: bulkImportService },
        { provide: NotificationService, useValue: notificationService },
        { provide: Router, useValue: router },
        { provide: DialogService, useValue: dialogService },
        { provide: UserAdministrationService, useValue: userAdminService },
      ],
    });

    fixture = TestBed.createComponent(BulkImportOverviewComponent);
    component = fixture.componentInstance;
  });

  it('should remove department', () => {
    const result = component.removeDepartment('Lastname Firstname (TEST-DEP)');
    expect(result).toBe('Lastname Firstname');
  });

  it('should not remove department if not exists', () => {
    const result = component.removeDepartment('Lastname Firstname');
    expect(result).toBe('Lastname Firstname');
  });

  it('should start bulk import', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    const mockBulkImportRequest = BulkImportFormGroupBuilder.buildBulkImport(component.form);
    const mockFile = new File([''], 'test.csv', { type: 'text/csv' });

    component.uploadedFiles = [mockFile];
    bulkImportService.startBulkImport.mockReturnValue(of({}));

    component.startBulkImport();

    expect(bulkImportService.startBulkImport).toHaveBeenCalledWith(mockBulkImportRequest, mockFile);
    expect(notificationService.success).toHaveBeenCalledWith('PAGES.BULK_IMPORT.SUCCESS');
  });

  it('should enable User select', () => {
    component.enableUserSelect(true);
    expect(component.isUserSelectEnabled).toBe(true);
  });

  it('should check if file is uploaded', () => {
    const mockFile = new File([''], 'test.csv', { type: 'text/csv' });
    component.onFileChange([mockFile]);
    expect(component.isFileUploaded).toBe(true);
  });

  it('should reset configuration', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    const enableUserSelectSpy = vi.spyOn(component, 'enableUserSelect');

    component.resetConfiguration(true);

    expect(component.isEnabledToStartImport).toBe(false);
    expect(enableUserSelectSpy).toHaveBeenCalledWith(false);
    expect(component.uploadedFiles).toEqual([]);
    expect(component.form.controls.userSearchForm.controls.userSearch.value).toBeNull();
    expect(component.form.controls.objectType.value).toBeNull();
    expect(component.form.controls.applicationType.value).toBeNull();
    expect(component.form.controls.importType.value).toBeNull();
    expect(component.form.controls.emails.value).toEqual([]);
  });

  it('should reset configuration and reinitialize on error', () => {
    const errorResponse = new Error('Test error');
    component.form = BulkImportFormGroupBuilder.initFormGroup();

    bulkImportService.startBulkImport.mockReturnValue(throwError(() => errorResponse));

    const resetConfigurationSpy = vi.spyOn(component, 'resetConfiguration');
    const ngOnInitSpy = vi.spyOn(component, 'ngOnInit');

    component.startBulkImport();

    expect(resetConfigurationSpy).toHaveBeenCalledWith(true);
    expect(ngOnInitSpy).toHaveBeenCalled();
  });

  it('should set OPTIONS_OBJECT_TYPE when applicationType changes', () => {
    component.ngOnInit();
    component.form.controls.applicationType.setValue(ApplicationType.Sepodi);
    fixture.detectChanges();

    expect(component.OPTIONS_OBJECT_TYPE).toEqual(component.OPTIONS_OBJECTS[ApplicationType.Sepodi]);
  });

  it('should enable import when all conditions are met', () => {
    fixture.detectChanges();

    const form = component.form;
    form.controls.applicationType.setValue(ApplicationType.Sepodi);
    form.controls.importType.setValue(ImportType.Create);
    form.controls.objectType.setValue(BusinessObjectType.ServicePoint);

    fixture.detectChanges();
    expect(component.isEnabledToStartImport).toBe(true);
  });

  it('should return true for checkForNull when none of the form controls are null', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    component.form.controls.applicationType.setValue(ApplicationType.Sepodi);
    component.form.controls.objectType.setValue(BusinessObjectType.ServicePoint);
    component.form.controls.importType.setValue(ImportType.Create);

    expect(component.checkForNull()).toBe(true);
  });

  it('should return false for checkForNull when any form control is null', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    component.form.controls.applicationType.setValue(null);
    component.form.controls.objectType.setValue(BusinessObjectType.ServicePoint);
    component.form.controls.importType.setValue(ImportType.Create);

    expect(component.checkForNull()).toBe(false);
  });

  it('should return true for combinationForActiveDownloadButton for valid combinations', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    component.form.controls.applicationType.setValue(ApplicationType.Sepodi);
    component.form.controls.objectType.setValue(BusinessObjectType.ServicePoint);
    component.form.controls.importType.setValue(ImportType.Update);

    expect(component.combinationForActiveDownloadButton()).toBe(true);
  });

  it('should return false for combinationForActiveDownloadButton for invalid combinations', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    component.form.controls.applicationType.setValue(ApplicationType.Prm);
    component.form.controls.objectType.setValue(BusinessObjectType.TrafficPoint);
    component.form.controls.importType.setValue(ImportType.Terminate);

    expect(component.combinationForActiveDownloadButton()).toBe(false);
  });

  it('should download the Excel file', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    component.form.controls.applicationType.setValue(ApplicationType.Sepodi);
    component.form.controls.objectType.setValue(BusinessObjectType.ServicePoint);
    component.form.controls.importType.setValue(ImportType.Create);

    const blob = new Blob(['test'], { type: 'text/csv' });
    bulkImportService.downloadTemplate.mockReturnValue(of(blob));
    const fileDownloadSpy = vi.spyOn(FileDownloadService, 'downloadFile').mockImplementation(() => {});

    component.downloadExcel();

    expect(bulkImportService.downloadTemplate).toHaveBeenCalledExactlyOnceWith(
      ApplicationType.Sepodi,
      BusinessObjectType.ServicePoint,
      ImportType.Create
    );
    expect(dialogService.showInfo).toHaveBeenCalledExactlyOnceWith({
      title: 'PAGES.BULK_IMPORT.DIALOG_TEMPLATE_TO_EXCEL',
      message: 'PAGES.BULK_IMPORT.TEMPLATE_TO_EXCEL',
    });
    expect(fileDownloadSpy).toHaveBeenCalledExactlyOnceWith('create_service_point.csv', blob);
  });
});
