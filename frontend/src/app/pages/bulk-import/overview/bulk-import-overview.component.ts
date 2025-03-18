import { Component, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { BulkImportFormGroup, BulkImportFormGroupBuilder } from '../detail/bulk-import-form-group';
import {
  ApplicationType,
  BulkImportService,
  BusinessObjectType,
  ImportType,
  UserAdministrationService,
} from '../../../api';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { catchError, EMPTY, finalize } from 'rxjs';
import {
  ALLOWED_FILE_TYPES_BULK_IMPORT,
  OPTIONS_APPLICATION_TYPE,
  OPTIONS_OBJECT_TYPE_BODI,
  OPTIONS_OBJECT_TYPE_LIDI,
  OPTIONS_OBJECT_TYPE_PRM,
  OPTIONS_OBJECT_TYPE_SEPODI,
  OPTIONS_OBJECT_TYPE_TIMETABLE_HEARING,
  OPTIONS_OBJECT_TYPE_TTFN,
  OPTIONS_SCENARIO,
} from '../detail/bulk-import-options';
import { NotificationService } from '../../../core/notification/notification.service';
import { FileDownloadService } from '../../../core/components/file-upload/file/file-download.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { LoadingSpinnerService } from '../../../core/components/loading-spinner/loading-spinner.service';
import { tap } from 'rxjs/operators';
import { SelectComponent } from '../../../core/form-components/select/select.component';
import { NgIf } from '@angular/common';
import { MatCheckbox } from '@angular/material/checkbox';
import { UserSelectComponent } from '../../user-administration/user/user-select/user-select.component';
import { StringListComponent } from '../../../core/form-components/string-list/string-list.component';
import { FileUploadComponent } from '../../../core/components/file-upload/file-upload.component';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

const VALID_COMBINATIONS: [ApplicationType, BusinessObjectType, ImportType][] = [
  [ApplicationType.Sepodi, BusinessObjectType.ServicePoint, ImportType.Update],
  [ApplicationType.Sepodi, BusinessObjectType.ServicePoint, ImportType.Create],
  [ApplicationType.Sepodi, BusinessObjectType.TrafficPoint, ImportType.Update],
  [ApplicationType.Sepodi, BusinessObjectType.TrafficPoint, ImportType.Create],
  [ApplicationType.Prm, BusinessObjectType.PlatformReduced, ImportType.Update],
];

@Component({
    templateUrl: './bulk-import-overview.component.html',
    imports: [ReactiveFormsModule, SelectComponent, NgIf, MatCheckbox, UserSelectComponent, StringListComponent, FileUploadComponent, AtlasButtonComponent, TranslatePipe]
})
export class BulkImportOverviewComponent implements OnInit {
  protected readonly OPTIONS_SCENARIO = OPTIONS_SCENARIO;
  protected readonly OPTIONS_APPLICATION_TYPE = OPTIONS_APPLICATION_TYPE;
  protected readonly ALLOWED_FILE_TYPES_BULK_IMPORT = ALLOWED_FILE_TYPES_BULK_IMPORT;
  isCheckForNull: boolean = false;
  isCombinationForActiveDownloadButton: boolean = false;
  isDownloadButtonDisabled: boolean = true;

  OPTIONS_OBJECTS = {
    SEPODI: OPTIONS_OBJECT_TYPE_SEPODI,
    PRM: OPTIONS_OBJECT_TYPE_PRM,
    TIMETABLE_HEARING: OPTIONS_OBJECT_TYPE_TIMETABLE_HEARING,
    BODI: OPTIONS_OBJECT_TYPE_BODI,
    LIDI: OPTIONS_OBJECT_TYPE_LIDI,
    TTFN: OPTIONS_OBJECT_TYPE_TTFN,
  };

  OPTIONS_OBJECT_TYPE!: string[];

  form!: FormGroup<BulkImportFormGroup>;
  isUserSelectEnabled = false;

  uploadedFiles: File[] = [];

  userName: string | undefined;

  isAtLeastSupervisor = false;

  isEnabledToStartImport = false;
  isFileUploaded = false;
  saving = false;

  constructor(
    private userAdministrationService: UserAdministrationService,
    private permissionService: PermissionService,
    private bulkImportService: BulkImportService,
    private readonly notificationService: NotificationService,
    private readonly dialogService: DialogService,
    private readonly loadingSpinnerService: LoadingSpinnerService,
  ) {}

  ngOnInit(): void {
    this.form = BulkImportFormGroupBuilder.initFormGroup();
    this.userAdministrationService.getCurrentUser().subscribe((user) => {
      this.userName = this.removeDepartment(user.displayName);
    });

    this.form.controls.applicationType.valueChanges.subscribe((value) => {
      if (value) {
        this.isAtLeastSupervisor = this.permissionService.isAtLeastSupervisor(value);
        this.OPTIONS_OBJECT_TYPE = this.OPTIONS_OBJECTS[value];
        this.resetConfiguration(false);
      }
    });

    this.form.valueChanges.subscribe((value) => {
      if (value.importType != null && value.applicationType != null && value.objectType != null) {
        this.isEnabledToStartImport = true;
      }
      this.updateFlags();
    });
  }

  removeDepartment(username?: string) {
    const departmentStart = '(';
    if (!username?.includes(departmentStart)) {
      return username;
    }
    return username?.substring(0, username.indexOf(departmentStart)).trim();
  }

  startBulkImport() {
    this.saving = true;
    this.loadingSpinnerService.loading.next(true);

    const bulkImportRequest = BulkImportFormGroupBuilder.buildBulkImport(this.form);

    this.bulkImportService
      .startBulkImport(bulkImportRequest, this.uploadedFiles[0])
      .pipe(
        tap(() => {
          this.notificationService.success('PAGES.BULK_IMPORT.SUCCESS');
          this.resetConfiguration(true);
          this.ngOnInit();
        }),
        catchError(() => {
          this.resetConfiguration(true);
          this.ngOnInit();
          return EMPTY;
        }),
        finalize(() => {
          this.saving = false;
          this.loadingSpinnerService.loading.next(false);
        }),
      )
      .subscribe();
  }

  enableUserSelect(isEnabled: boolean) {
    this.isUserSelectEnabled = isEnabled;
  }

  onFileChange(files: File[]) {
    this.isFileUploaded = files.length > 0;
  }

  resetConfiguration(resetAll: boolean) {
    this.isEnabledToStartImport = false;
    this.enableUserSelect(false);
    this.uploadedFiles = [];
    this.isFileUploaded = false;

    this.form.controls.userSearchForm.controls.userSearch.reset(null, {
      onlySelf: true,
      emitEvent: false,
    });
    this.form.controls.objectType.reset(null, { onlySelf: true, emitEvent: false });

    if (resetAll) {
      this.form = BulkImportFormGroupBuilder.initFormGroup();
    }
  }

  updateFlags() {
    this.isCheckForNull = this.checkForNull();
    this.isCombinationForActiveDownloadButton = this.combinationForActiveDownloadButton();
    this.isDownloadButtonDisabled = !(
      this.isCheckForNull && this.isCombinationForActiveDownloadButton
    );
  }

  checkForNull(): boolean {
    return (
      this.form.controls.applicationType.value !== null &&
      this.form.controls.objectType.value !== null &&
      this.form.controls.importType.value !== null
    );
  }

  combinationForActiveDownloadButton(): boolean {
    const applicationType = this.form.controls.applicationType.value;
    const objectType = this.form.controls.objectType.value;
    const importType = this.form.controls.importType.value;

    return VALID_COMBINATIONS.some(
      ([appType, objType, impType]) =>
        appType === applicationType && objType === objectType && impType === importType,
    );
  }

  downloadExcel() {
    const bulkImportRequest = BulkImportFormGroupBuilder.buildBulkImport(this.form);
    const filename = `${bulkImportRequest.importType.toLowerCase()}_${bulkImportRequest.objectType.toLowerCase()}.csv`;
    this.bulkImportService
      .downloadTemplate(
        bulkImportRequest.applicationType,
        bulkImportRequest.objectType,
        bulkImportRequest.importType,
      )
      .subscribe((response) => {
        FileDownloadService.downloadFile(filename, response);
        this.dialogService.showInfo({
          title: 'PAGES.BULK_IMPORT.DIALOG_TEMPLATE_TO_EXCEL',
          message: 'PAGES.BULK_IMPORT.TEMPLATE_TO_EXCEL',
        });
      });
  }
}
