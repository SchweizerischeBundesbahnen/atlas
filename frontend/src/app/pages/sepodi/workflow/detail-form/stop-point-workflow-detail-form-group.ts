import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {JudgementType, ReadStopPointWorkflow, StopPointPerson} from "../../../../api";

export interface StopPointWorkflowDetailFormGroup {
  ccEmails: FormControl<Array<string> | null | undefined>;
  workflowComment: FormControl<string | null | undefined>;
  examinants: FormArray<FormGroup<ExaminantFormGroup>>;
}

export interface ExaminantFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  personFunction: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  mail: FormControl<string | null | undefined>;
  judgementIcon: FormControl<string | null | undefined>;
  id: FormControl<number| null| undefined>;
}

export class StopPointWorkflowDetailFormGroupBuilder {

  static buildFormGroup(workflow?: ReadStopPointWorkflow): FormGroup<StopPointWorkflowDetailFormGroup> {
    return new FormGroup<StopPointWorkflowDetailFormGroup>(
      {
        ccEmails: new FormControl(workflow?.ccEmails, [Validators.maxLength(10)]),
        workflowComment: new FormControl(workflow?.workflowComment, [Validators.required, Validators.minLength(2), AtlasFieldLengthValidator.comments, AtlasCharsetsValidator.iso88591]),
        examinants: new FormArray<FormGroup<ExaminantFormGroup>>(workflow?.examinants?.map(examinant => this.buildExaminantFormGroup(examinant)) ?? [this.buildExaminantFormGroup()]),
      }
    );
  }

  static buildExaminantFormGroup(examinant?: StopPointPerson): FormGroup<ExaminantFormGroup> {
    return new FormGroup<ExaminantFormGroup>(
      {
        id: new FormControl(examinant?.id),
        firstName: new FormControl(examinant?.firstName),
        lastName: new FormControl(examinant?.lastName),
        organisation: new FormControl(examinant?.organisation, [Validators.required]),
        personFunction: new FormControl(examinant?.personFunction),
        mail: new FormControl(examinant?.mail, [Validators.required, AtlasCharsetsValidator.email]),
        judgementIcon: new FormControl(this.buildJudgementIcon(examinant?.judgement)),
      }
    );
  }

  static buildJudgementIcon(judgement?: JudgementType):string {
    switch (judgement){
      case JudgementType.Yes:
        return 'bi-check-lg';
      case JudgementType.No:
        return 'bi-x-lg';
      default:
        return 'bi-hourglass-split';
    }
  }

}
