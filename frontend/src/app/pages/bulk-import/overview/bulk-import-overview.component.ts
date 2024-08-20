import {Component, OnInit} from "@angular/core";
import {FormControl, FormGroup} from "@angular/forms";
import {BulkImportFormGroup, BulkImportFormGroupBuilder} from "../detail/bulk-import-form-group";
import {
  ApplicationType,
  BulkImportService,
  BusinessObjectType,
  ImportType, InlineObject9,
  UserAdministrationService
} from "../../../api";

@Component({
  templateUrl: './bulk-import-overview.component.html'
})
export class BulkImportOverviewComponent implements OnInit{
  form!: FormGroup<BulkImportFormGroup>;

  uploadedFiles: File[] = [];
  userName: string | undefined;
  optionsApplication: string[] = [ApplicationType.Sepodi, ApplicationType.Prm];
  optionsObject: string[] = [BusinessObjectType.StopPoint, BusinessObjectType.LoadingPoint];
  optionsScenario: string[] = [ImportType.Create, ImportType.Terminate];

  constructor(private userAdministrationService: UserAdministrationService,
              private bulkImportService: BulkImportService) {
  }

  ngOnInit(): void {
    this.form = BulkImportFormGroupBuilder.initFormGroup();
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


  private startBulkImport(){
    this.bulkImportService.startServicePointImportBatch1(
      ApplicationType.Sepodi,
      BusinessObjectType.LoadingPoint,
      ImportType.Create,
      { file: this.uploadedFiles[0] } as InlineObject9
    );

  }
  //TODO: Method to start workflow


  //TODO: Get user
}
