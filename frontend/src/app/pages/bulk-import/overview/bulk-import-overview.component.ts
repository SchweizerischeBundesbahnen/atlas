import {Component, OnInit} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {BulkImportFormGroup, BulkImportFormGroupBuilder} from "../detail/bulk-import-form-group";
import {
  BulkImportService,
  UserAdministrationService
} from "../../../api";
import {PermissionService} from "../../../core/auth/permission/permission.service";
import {catchError, EMPTY} from "rxjs";
import {
  ALLOWED_FILE_TYPES_BULK_IMPORT,
  OPTIONS_APPLICATION_TYPE,
  OPTIONS_OBJECT_TYPE,
  OPTIONS_SCENARIO
} from "../detail/bulk-import-options";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  templateUrl: './bulk-import-overview.component.html'
})
export class BulkImportOverviewComponent implements OnInit{
  form!: FormGroup<BulkImportFormGroup>;
  isUserSelectEnabled = false;
  uploadedFiles: File[] = [];

  userName: string | undefined;
  //TODO: Filter - wenn PRM ausgewählt, dann BusinessObjectTypes von PRM werden angezeigt

  isAdmin = false;

  isEnabledToStartImport = false;

  constructor(private userAdministrationService: UserAdministrationService,
              private permissionService: PermissionService,
              private bulkImportService: BulkImportService,
              private readonly router: Router,
              private readonly route: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    this.form = BulkImportFormGroupBuilder.initFormGroup();
    this.isAdmin = this.permissionService.isAdmin;
    this.userAdministrationService.getCurrentUser().subscribe((user) => {
      this.userName = this.removeDepartment(user.displayName);
    });

    this.form.valueChanges.subscribe(value => {
      console.log("value ", value)
      if(value.importType != null && value.applicationType != null && value.objectType != null && this.uploadedFiles[0] != null) {
        this.isEnabledToStartImport = true
        console.log("isEnabled ", this.isEnabledToStartImport)
      }
    })
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

    const controlsAlreadyDisabled = Object.keys(this.form.controls).filter(
      (key) => this.form.get(key)?.disabled,
    );

    this.bulkImportService.startServicePointImportBatch(bulkImportRequest, this.uploadedFiles[0])
      .pipe(catchError(() => this.handleError(controlsAlreadyDisabled)))
      .subscribe((bulkImport) => {
        //TODO disable again start button
      });
  }

  enableUserSelect(isEnabled: boolean) {
    this.isUserSelectEnabled = isEnabled;
  }

  back(){
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }

  private readonly handleError = (excludedControls: string[]) => {
    Object.keys(this.form.controls).forEach((key) => {
      if (!excludedControls.includes(key)) {
        this.form.get(key)?.enable({ emitEvent: false });
      }
    });
    return EMPTY;
  };
  protected readonly OPTIONS_SCENARIO = OPTIONS_SCENARIO;
  protected readonly OPTIONS_OBJECT_TYPE = OPTIONS_OBJECT_TYPE;
  protected readonly OPTIONS_APPLICATION_TYPE = OPTIONS_APPLICATION_TYPE;
  protected readonly ALLOWED_FILE_TYPES_BULK_IMPORT = ALLOWED_FILE_TYPES_BULK_IMPORT;
}
