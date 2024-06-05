import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";

export interface StopPointWorkflowDetailFormGroup {
  ccEmails: FormControl<Array<string> | null | undefined>;
  workflowComment: FormControl<string | null | undefined>;
  examinants: FormArray<FormGroup<ExaminantFormGroup>>;
}

export interface ExaminantFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  personFunction: FormControl<string | null | undefined>;
  mail: FormControl<string | null | undefined>;
}

export class StopPointWorkflowDetailFormGroupBuilder {

  static buildFormGroup(): FormGroup<StopPointWorkflowDetailFormGroup> {
    return new FormGroup<StopPointWorkflowDetailFormGroup>(
      {
        ccEmails: new FormControl([], [Validators.maxLength(10)]),
        workflowComment: new FormControl('', [Validators.required, AtlasFieldLengthValidator.comments]),
        examinants: new FormArray<FormGroup<ExaminantFormGroup>>([this.buildExaminantFormGroup()]),
      }
    );
  }

  static buildExaminantFormGroup(): FormGroup<ExaminantFormGroup> {
    return new FormGroup<ExaminantFormGroup>(
      {
        firstName: new FormControl('', [Validators.required]),
        lastName: new FormControl('', [Validators.required]),
        personFunction: new FormControl('', [Validators.required]),
        mail: new FormControl(undefined, [Validators.required, AtlasCharsetsValidator.email])
      }
    );
  }

}
