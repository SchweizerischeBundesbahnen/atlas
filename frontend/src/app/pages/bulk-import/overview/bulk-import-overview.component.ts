import {Component, OnInit} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {BulkImportFormGroup, BulkImportFormGroupBuilder} from "../detail/bulk-import-form-group";
import {BulkImportService, BusinessObjectType, ImportType, UserAdministrationService} from "../../../api";
import {PermissionService} from "../../../core/auth/permission/permission.service";
import {catchError, EMPTY} from "rxjs";
import {
  ALLOWED_FILE_TYPES_BULK_IMPORT,
  OPTIONS_APPLICATION_TYPE,
  OPTIONS_OBJECT_TYPE_BODI,
  OPTIONS_OBJECT_TYPE_LIDI,
  OPTIONS_OBJECT_TYPE_PRM,
  OPTIONS_OBJECT_TYPE_SEPODI,
  OPTIONS_OBJECT_TYPE_TIMETABLE_HEARING,
  OPTIONS_OBJECT_TYPE_TTFN,
  OPTIONS_SCENARIO
} from "../detail/bulk-import-options";
import {ActivatedRoute, Router} from "@angular/router";
import {NotificationService} from "../../../core/notification/notification.service";
import {FileDownloadService} from "../../../core/components/file-upload/file/file-download.service";

@Component({
  templateUrl: './bulk-import-overview.component.html'
})
export class BulkImportOverviewComponent implements OnInit {
  protected readonly OPTIONS_SCENARIO = OPTIONS_SCENARIO;
  protected readonly OPTIONS_APPLICATION_TYPE = OPTIONS_APPLICATION_TYPE;
  protected readonly ALLOWED_FILE_TYPES_BULK_IMPORT = ALLOWED_FILE_TYPES_BULK_IMPORT;

  OPTIONS_OBJECTS = {
    SEPODI: OPTIONS_OBJECT_TYPE_SEPODI,
    PRM: OPTIONS_OBJECT_TYPE_PRM,
    TIMETABLE_HEARING: OPTIONS_OBJECT_TYPE_TIMETABLE_HEARING,
    BODI: OPTIONS_OBJECT_TYPE_BODI,
    LIDI: OPTIONS_OBJECT_TYPE_LIDI,
    TTFN: OPTIONS_OBJECT_TYPE_TTFN
  };

  OPTIONS_OBJECT_TYPE!: string[];

  form!: FormGroup<BulkImportFormGroup>;
  isUserSelectEnabled = false;

  uploadedFiles: File[] = [];

  userName: string | undefined;

  isAdmin = false;
  isApplicationSelected = false;

  isEnabledToStartImport = false;
  isFileUploaded = false;

  constructor(private userAdministrationService: UserAdministrationService,
              private permissionService: PermissionService,
              private bulkImportService: BulkImportService,
              private readonly router: Router,
              private readonly route: ActivatedRoute,
              private readonly notificationService: NotificationService,
  ) {
  }

  ngOnInit(): void {
    this.form = BulkImportFormGroupBuilder.initFormGroup();
    this.isAdmin = this.permissionService.isAdmin;
    this.userAdministrationService.getCurrentUser().subscribe((user) => {
      this.userName = this.removeDepartment(user.displayName);
    });

    this.form.controls.applicationType.valueChanges.subscribe(value => {
      if (value) {
        this.OPTIONS_OBJECT_TYPE = this.OPTIONS_OBJECTS[value]
        this.resetConfiguration(false)
        this.isApplicationSelected = true;
      }
    });

    this.form.valueChanges.subscribe(value => {
      if (value.importType != null && value.applicationType != null && value.objectType != null) {
        this.isEnabledToStartImport = true
      }
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
    const bulkImportRequest = BulkImportFormGroupBuilder.buildBulkImport(this.form);

    this.bulkImportService.startServicePointImportBatch(bulkImportRequest, this.uploadedFiles[0])
      .pipe(catchError(() => {
        this.resetConfiguration(true);
        return EMPTY
      }))
      .subscribe(() => {
        this.notificationService.success('PAGES.BULK_IMPORT.SUCCESS')
        this.resetConfiguration(true);
      });
  }

  enableUserSelect(isEnabled: boolean) {
    this.isUserSelectEnabled = isEnabled;
  }

  back() {
    this.router.navigate(['..'], {relativeTo: this.route}).then();
  }

  onFileChange(files: File[]) {
    this.isFileUploaded = files.length > 0;
  }

  resetConfiguration(resetAll: boolean) {
    this.isEnabledToStartImport = false;
    this.enableUserSelect(false);
    this.uploadedFiles = [];
    this.isApplicationSelected = false;


    this.form.controls.userSearchForm.controls.userSearch.setValue(null, {emitEvent: false});
    this.form.controls.objectType.setValue(null, {emitEvent: false});

    if (resetAll) {
      this.form.controls.applicationType.setValue(null, {emitEvent: false});
      this.form.controls.importType.setValue(null, {emitEvent: false});
      this.form.controls.emails.setValue([], {emitEvent: false});
    }
  }

  get isDownloadButtonVisible(): boolean {
    return (
      this.checkForNull && this.sepodiCombination
    );
  }

  get checkForNull(): boolean {
    return (
      this.form.controls.applicationType.value !== null &&
      this.form.controls.objectType.value !== null &&
      this.form.controls.importType.value !== null
    );
  }

  get sepodiCombination(): boolean {
    return (
      (this.form.controls.objectType.value == BusinessObjectType.ServicePoint || this.form.controls.objectType.value == BusinessObjectType.TrafficPoint) &&
      (this.form.controls.importType.value == ImportType.Create || this.form.controls.importType.value == ImportType.Update)
    );
  }

  get isDownloadButtonDisabled(): boolean {
    return !this.isDownloadButtonVisible;
  }

  downloadExcel() {
    const bulkImportRequest = BulkImportFormGroupBuilder.buildBulkImport(this.form);
    if (bulkImportRequest.objectType && bulkImportRequest.importType) {
      const filename = `${bulkImportRequest.importType.toLowerCase()}_${bulkImportRequest.objectType.toLowerCase()}.xlsx`;
      this.bulkImportService
        .downloadTemplate(bulkImportRequest.objectType, bulkImportRequest.importType)
        .subscribe((response) => FileDownloadService.downloadFile(filename, response));
    } else {
      console.error('Please select both a Business Object Type and an Import Type.');
    }
  }

}
