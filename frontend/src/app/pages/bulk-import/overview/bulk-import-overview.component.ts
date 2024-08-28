import {Component, OnInit} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {BulkImportFormGroup, BulkImportFormGroupBuilder} from "../detail/bulk-import-form-group";
import {
  ApplicationType, BulkImportRequest,
  BulkImportService,
  BusinessObjectType,
  ImportType, InlineObject9,
  UserAdministrationService
} from "../../../api";
import {PermissionService} from "../../../core/auth/permission/permission.service";

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
    const bulkImportRequest: BulkImportRequest = BulkImportFormGroupBuilder.buildBulkImport(this.form);
    const formData: InlineObject9 = {
      ...bulkImportRequest,
      file: this.uploadedFiles[0]
    };
    this.bulkImportService.startServicePointImportBatch1(
      formData
    ).subscribe(test  => {
      console.log("test ", test)
    });
  }

  enableUserSelect(isEnabled: boolean) {
    this.isUserSelectEnabled = isEnabled;
  }

  back(){

  }

  //TODO: Get user
}
