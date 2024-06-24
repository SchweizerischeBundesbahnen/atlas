import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {StopPointRejectWorkflow} from "../../../../api";

export interface StopPointRejectWorkflowFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  motivationComment: FormControl<string | null | undefined>;
}

export class StopPointRejectWorkflowFormGroupBuilder {

  static initFormGroup(): FormGroup<StopPointRejectWorkflowFormGroup> {
    return new FormGroup<StopPointRejectWorkflowFormGroup>({
      firstName: new FormControl('', [Validators.required, Validators.maxLength(50)]),
      lastName: new FormControl('', [Validators.required, Validators.maxLength(50)]),
      organisation: new FormControl('', [Validators.required, Validators.maxLength(255)]),
      motivationComment: new FormControl('', [Validators.required, Validators.minLength(2), AtlasFieldLengthValidator.comments])
    })
  }

  static buildStopPointRejectWorkflow(formGroup: FormGroup<StopPointRejectWorkflowFormGroup>, email: string | undefined): StopPointRejectWorkflow {
    return {
      firstName: formGroup.controls["firstName"].value!,
      lastName: formGroup.controls["lastName"].value!,
      motivationComment: formGroup.controls["motivationComment"].value!,
      mail: email!,
      organisation: formGroup.controls["organisation"].value!,
    }
  }

}
