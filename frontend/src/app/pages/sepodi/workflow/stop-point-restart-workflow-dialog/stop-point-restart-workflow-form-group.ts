import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {StopPointRejectWorkflow} from "../../../../api";
import {
  StopPointRejectWorkflowFormGroup
} from "../stop-point-reject-workflow-dialog/stop-point-reject-workflow-form-group";

export interface StopPointRestartWorkflowFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  motivationComment: FormControl<string | null | undefined>;
  mail: FormControl<string | null | undefined>;
  designationOfficial: FormControl<string | null | undefined>
}
export class StopPointRestartWorkflowFormGroup {

  static initFormGroup(): FormGroup<StopPointRestartWorkflowFormGroup> {
    return new FormGroup<StopPointRestartWorkflowFormGroup>({
      firstName: new FormControl('', [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.iso88591]),
      lastName: new FormControl('', [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.iso88591]),
      organisation: new FormControl('', [Validators.required, Validators.maxLength(255), AtlasCharsetsValidator.iso88591]),
      motivationComment: new FormControl('', [Validators.required, Validators.minLength(2), AtlasFieldLengthValidator.comments, AtlasCharsetsValidator.iso88591]),
      mail: new FormControl('', [Validators.required, AtlasCharsetsValidator.email]),
      designationOfficial: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(30)])
    })
  }

  static buildStopPointRejectWorkflow(formGroup: FormGroup<StopPointRestartWorkflowFormGroup>): StopPointRejectWorkflow {
    return {
      firstName: formGroup.controls.firstName.value!,
      lastName: formGroup.controls.lastName.value!,
      motivationComment: formGroup.controls.motivationComment.value!,
      mail: formGroup.controls.mail.value!,
      organisation: formGroup.controls.organisation.value!,
    }
  }
}
