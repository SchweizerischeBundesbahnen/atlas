import { FormControl, FormGroup, Validators} from "@angular/forms";
import {AtlasCharsetsValidator} from "../../../core/validation/charsets/atlas-charsets-validator";
import {ApplicationType, BusinessObjectType, ImportType} from "../../../api";

export interface BulkImportFormGroup {
  applicationType: FormControl<ApplicationType | null | undefined>;
  objectType: FormControl<BusinessObjectType | null | undefined>;
  importType: FormControl<ImportType | null | undefined>;
  //inNameOf: FormControl<string | null | undefined>;
  emails: FormControl<Array<string> | null | undefined>;
  userSearchForm: FormGroup<{ userSearch: FormControl<string | null> }>;
}

export class BulkImportFormGroupBuilder {
  static initFormGroup(): FormGroup<BulkImportFormGroup> {
    return new FormGroup<BulkImportFormGroup>({
      applicationType: new FormControl(null, [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.iso88591]),
      objectType: new FormControl(null, [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.iso88591]),
      importType: new FormControl(null, [Validators.required, Validators.maxLength(255), AtlasCharsetsValidator.iso88591]),
      userSearchForm: new FormGroup({
        userSearch: new FormControl<string | null>(null)
      }),
      //inNameOf: new FormControl('', [Validators.required, Validators.minLength(2), AtlasFieldLengthValidator.comments, AtlasCharsetsValidator.iso88591]),
      emails: new FormControl([]),
    })
  }

  static buildBulkImport(formGroup: FormGroup<BulkImportFormGroup>) {
    return {
      applicationType: formGroup.controls.applicationType.value!,
      objectType: formGroup.controls.objectType.value!,
      importType: formGroup.controls.importType.value!,
      inNameOf: formGroup.controls.userSearchForm.controls.userSearch.value!,
      emails: formGroup.controls.emails.value!,
    }
  }
}
