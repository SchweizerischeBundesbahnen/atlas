import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  ApplicationType,
  BusinessObjectType,
  ImportType,
  User,
} from '../../../api';

export interface BulkImportFormGroup {
  applicationType: FormControl<ApplicationType | null | undefined>;
  objectType: FormControl<BusinessObjectType | null | undefined>;
  importType: FormControl<ImportType | null | undefined>;
  emails: FormControl<Array<string> | null | undefined>;
  userSearchForm: FormGroup<{ userSearch: FormControl<User | null> }>;
}

export class BulkImportFormGroupBuilder {
  static initFormGroup(): FormGroup<BulkImportFormGroup> {
    return new FormGroup<BulkImportFormGroup>({
      applicationType: new FormControl(null, [Validators.required]),
      objectType: new FormControl(null, [Validators.required]),
      importType: new FormControl(null, [Validators.required]),
      userSearchForm: new FormGroup({
        userSearch: new FormControl<User | null>(null),
      }),
      emails: new FormControl([]),
    });
  }

  static buildBulkImport(formGroup: FormGroup<BulkImportFormGroup>) {
    return {
      applicationType: formGroup.controls.applicationType.value!,
      objectType: formGroup.controls.objectType.value!,
      importType: formGroup.controls.importType.value!,
      inNameOf:
        formGroup.controls.userSearchForm.controls.userSearch.value?.userId,
      emails: formGroup.controls.emails.value!,
    };
  }
}
