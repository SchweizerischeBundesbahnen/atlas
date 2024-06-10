import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {Client, ReadStopPointWorkflow} from "../../../../api";

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

  static buildFormGroup(workflow?: ReadStopPointWorkflow): FormGroup<StopPointWorkflowDetailFormGroup> {
    return new FormGroup<StopPointWorkflowDetailFormGroup>(
      {
        ccEmails: new FormControl(workflow?.ccEmails, [Validators.maxLength(10)]),
        workflowComment: new FormControl(workflow?.workflowComment, [Validators.required, AtlasFieldLengthValidator.comments]),
        examinants: new FormArray<FormGroup<ExaminantFormGroup>>(workflow?.examinants?.map(examinant => this.buildExaminantFormGroup(examinant)) ?? [this.buildExaminantFormGroup()]),
      }
    );
  }

  static buildExaminantFormGroup(examinant?: Client): FormGroup<ExaminantFormGroup> {
    return new FormGroup<ExaminantFormGroup>(
      {
        firstName: new FormControl(examinant?.firstName),
        lastName: new FormControl(examinant?.lastName),
        personFunction: new FormControl(examinant?.personFunction, [Validators.required]),
        mail: new FormControl(examinant?.mail, [Validators.required, AtlasCharsetsValidator.email])
      }
    );
  }

}
