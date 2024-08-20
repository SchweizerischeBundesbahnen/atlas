import { FormControl, FormGroup, Validators} from "@angular/forms";
import {AtlasCharsetsValidator} from "../../../core/validation/charsets/atlas-charsets-validator";
import {AtlasFieldLengthValidator} from "../../../core/validation/field-lengths/atlas-field-length-validator";

export interface BulkImportFormGroup {
  applicationType: FormControl<string | null | undefined>;
  objectType: FormControl<string | null | undefined>;
  importType: FormControl<string | null | undefined>;
  inNameOf: FormControl<string | null | undefined>;
  emails: FormControl<Array<string> | null | undefined>;
}

export class BulkImportFormGroupBuilder {
  static initFormGroup(): FormGroup<BulkImportFormGroup> {
    return new FormGroup<BulkImportFormGroup>({
      applicationType: new FormControl('', [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.iso88591]),
      objectType: new FormControl('', [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.iso88591]),
      importType: new FormControl('', [Validators.required, Validators.maxLength(255), AtlasCharsetsValidator.iso88591]),
      inNameOf: new FormControl('', [Validators.required, Validators.minLength(2), AtlasFieldLengthValidator.comments, AtlasCharsetsValidator.iso88591]),
      emails: new FormControl([]),
    })
  }

  static buildBulkImport(formGroup: FormGroup<BulkImportFormGroup>) {
    return {
      applicationType: formGroup.controls.applicationType.value!,
      objectType: formGroup.controls.objectType.value!,
      importType: formGroup.controls.importType.value!,
      inNameOf: formGroup.controls.inNameOf.value!,
      emails: formGroup.controls.emails.value!,
    }
  }
}
