import {Component, OnInit} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {BulkImportFormGroup, BulkImportFormGroupBuilder} from "../detail/bulk-import-form-group";
import {ApplicationType, BulkImportService, BusinessObjectType, ImportType, UserAdministrationService} from "../../../api";
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

const VALID_COMBINATIONS: [ApplicationType, BusinessObjectType, ImportType][] = [
  [ApplicationType.Sepodi, BusinessObjectType.ServicePoint, ImportType.Create],
  [ApplicationType.Sepodi, BusinessObjectType.ServicePoint, ImportType.Update],
  [ApplicationType.Sepodi, BusinessObjectType.TrafficPoint, ImportType.Create],
  [ApplicationType.Sepodi, BusinessObjectType.TrafficPoint, ImportType.Update],
  [ApplicationType.Prm, BusinessObjectType.LoadingPoint, ImportType.Terminate]
];

@Component({
  templateUrl: './bulk-import-overview.component.html'
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

  updateFlags() {
    this.isCheckForNull = this.checkForNull();
    this.isCombinationForActiveDownloadButton = this.combinationForActiveDownloadButton();
    this.isDownloadButtonDisabled = !(this.isCheckForNull && this.isCombinationForActiveDownloadButton);
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

    return VALID_COMBINATIONS.some(([appType, objType, impType]) =>
      appType === applicationType &&
      objType === objectType &&
      impType === importType
    );
  }

  downloadExcel() {
    const bulkImportRequest = BulkImportFormGroupBuilder.buildBulkImport(this.form);
    const filename = `${bulkImportRequest.importType.toLowerCase()}_${bulkImportRequest.objectType.toLowerCase()}.csv`;
    this.bulkImportService
      .downloadTemplate(bulkImportRequest.objectType, bulkImportRequest.importType)
      .subscribe((response) => FileDownloadService.downloadFile(filename, response));
  }

}
