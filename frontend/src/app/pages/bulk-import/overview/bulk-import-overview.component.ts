import {Component, OnInit} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {BulkImportFormGroup, BulkImportFormGroupBuilder} from "../detail/bulk-import-form-group";
import {
  ApplicationType,
  BulkImportService,
  BusinessObjectType,
  ImportType,
  UserAdministrationService
} from "../../../api";
import {PermissionService} from "../../../core/auth/permission/permission.service";
import {catchError, EMPTY} from "rxjs";

@Component({
  templateUrl: './bulk-import-overview.component.html'
})
export class BulkImportOverviewComponent implements OnInit{
  form!: FormGroup<BulkImportFormGroup>;
  isUserSelectEnabled = false;
  uploadedFiles: File[] = [];

  fileTypes=["text/csv", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]

  userName: string | undefined;
  optionsApplication: string[] = Object.values([ApplicationType.Sepodi, ApplicationType.Prm]);
  optionsObject: string[] = Object.values([BusinessObjectType.StopPoint, BusinessObjectType.LoadingPoint]);
  optionsScenario: string[] = Object.values([ImportType.Create, ImportType.Terminate]);
  isAdmin = false;

  constructor(private userAdministrationService: UserAdministrationService,
              private permissionService: PermissionService,
              private bulkImportService: BulkImportService) {
  }

  ngOnInit(): void {
    this.form = BulkImportFormGroupBuilder.initFormGroup();
    this.isAdmin = this.permissionService.isAdmin;
    this.userAdministrationService.getCurrentUser().subscribe((user) => {
      this.userName = this.removeDepartment(user.displayName);
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

    const controlsAlreadyDisabled = Object.keys(this.form.controls).filter(
      (key) => this.form.get(key)?.disabled,
    );
    console.log("this.form ", this.form)
    this.bulkImportService.startServicePointImportBatch(
      this.form.controls.applicationType.value!,
      this.form.controls.objectType.value!,
      this.form.controls.importType.value!,
      bulkImportRequest,
      this.uploadedFiles[0]
    )
      .pipe(catchError(() => this.handleError(controlsAlreadyDisabled)))
      .subscribe((bulkImport) => {
        console.log("bulkImport ", bulkImport)
      });
  }

  enableUserSelect(isEnabled: boolean) {
    this.isUserSelectEnabled = isEnabled;
  }

  back(){

  }

  private readonly handleError = (excludedControls: string[]) => {
    Object.keys(this.form.controls).forEach((key) => {
      if (!excludedControls.includes(key)) {
        this.form.get(key)?.enable({ emitEvent: false });
      }
    });
    return EMPTY;
  };

  //TODO: Get user
}
