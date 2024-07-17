import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import { StopPointRestartWorkflow} from "../../../../api";

export interface StopPointRestartWorkflowFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  motivationComment: FormControl<string | null | undefined>;
  function: FormControl<string | null | undefined>;
  designationOfficial: FormControl<string | null | undefined>
}
export class StopPointRestartWorkflowFormGroupBuilder {

  static initFormGroup(): FormGroup<StopPointRestartWorkflowFormGroup> {
    return new FormGroup<StopPointRestartWorkflowFormGroup>({
      firstName: new FormControl('', [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.iso88591]),
      lastName: new FormControl('', [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.iso88591]),
      organisation: new FormControl('', [Validators.required, Validators.maxLength(255), AtlasCharsetsValidator.iso88591]),
      motivationComment: new FormControl('', [Validators.required, Validators.minLength(2), AtlasFieldLengthValidator.comments, AtlasCharsetsValidator.iso88591]),
      function: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(30), AtlasCharsetsValidator.iso88591]),
      designationOfficial: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(30)])
    })
  }

  static buildStopPointRestartWorkflow(formGroup: FormGroup<StopPointRestartWorkflowFormGroup>): StopPointRestartWorkflow {
    return {
      firstName: formGroup.controls.firstName.value!,
      lastName: formGroup.controls.lastName.value!,
      motivationComment: formGroup.controls.motivationComment.value!,
      function: formGroup.controls.function.value!,
      organisation: formGroup.controls.organisation.value!,
      designationOfficial: formGroup.controls.designationOfficial.value!,
    }
  }
}
