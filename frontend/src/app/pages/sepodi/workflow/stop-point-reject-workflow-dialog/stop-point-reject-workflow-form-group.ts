import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { StopPointRejectWorkflow } from '../../../../api';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';

export interface StopPointRejectWorkflowFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  motivationComment: FormControl<string | null | undefined>;
  mail: FormControl<string | null | undefined>;
}

export class StopPointRejectWorkflowFormGroupBuilder {
  static initFormGroup(): FormGroup<StopPointRejectWorkflowFormGroup> {
    return new FormGroup<StopPointRejectWorkflowFormGroup>({
      firstName: new FormControl('', [
        Validators.required,
        Validators.maxLength(50),
        AtlasCharsetsValidator.iso88591,
      ]),
      lastName: new FormControl('', [
        Validators.required,
        Validators.maxLength(50),
        AtlasCharsetsValidator.iso88591,
      ]),
      organisation: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
        AtlasCharsetsValidator.iso88591,
      ]),
      motivationComment: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        AtlasFieldLengthValidator.comments,
        AtlasCharsetsValidator.iso88591,
      ]),
      mail: new FormControl('', [
        Validators.required,
        AtlasCharsetsValidator.email,
      ]),
    });
  }

  static buildStopPointRejectWorkflow(
    formGroup: FormGroup<StopPointRejectWorkflowFormGroup>
  ): StopPointRejectWorkflow {
    return {
      firstName: formGroup.controls.firstName.value!,
      lastName: formGroup.controls.lastName.value!,
      motivationComment: formGroup.controls.motivationComment.value!,
      mail: formGroup.controls.mail.value!,
      organisation: formGroup.controls.organisation.value!,
    };
  }
}
