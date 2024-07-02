import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { JudgementType, ReadStopPointWorkflow, StopPointPerson } from 'src/app/api';
import { AtlasCharsetsValidator } from 'src/app/core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from 'src/app/core/validation/field-lengths/atlas-field-length-validator';

export interface StopPointWorkflowDetailFormGroup {
  ccEmails: FormControl<Array<string> | null | undefined>;
  workflowComment: FormControl<string | null | undefined>;
  designationOfficial: FormControl<string | null | undefined>;
  examinants: FormArray<FormGroup<ExaminantFormGroup>>;
}

export interface ExaminantFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  personFunction: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  mail: FormControl<string | null | undefined>;
  judgementIcon: FormControl<string | null | undefined>;
  judgement: FormControl<JudgementType | null | undefined>;
  id: FormControl<number | null | undefined>;
}

export class StopPointWorkflowDetailFormGroupBuilder {
  static buildFormGroup(
    workflow?: ReadStopPointWorkflow,
  ): FormGroup<StopPointWorkflowDetailFormGroup> {
    return new FormGroup<StopPointWorkflowDetailFormGroup>({
      ccEmails: new FormControl(workflow?.ccEmails, [Validators.maxLength(10)]),
      workflowComment: new FormControl(workflow?.workflowComment, [
        Validators.required,
        Validators.minLength(2),
        AtlasFieldLengthValidator.comments,
        AtlasCharsetsValidator.iso88591,
      ]),
      designationOfficial: new FormControl(workflow?.designationOfficial, [Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        AtlasCharsetsValidator.iso88591]),
      examinants: new FormArray<FormGroup<ExaminantFormGroup>>(
        workflow?.examinants?.map((examinant) => this.buildExaminantFormGroup(examinant)) ?? [
          this.buildExaminantFormGroup(),
        ],
      ),
    });
  }

  static buildExaminantFormGroup(examinant?: StopPointPerson): FormGroup<ExaminantFormGroup> {
    return new FormGroup<ExaminantFormGroup>({
      id: new FormControl(examinant?.id),
      firstName: new FormControl(examinant?.firstName),
      lastName: new FormControl(examinant?.lastName),
      organisation: new FormControl(examinant?.organisation, [Validators.required]),
      personFunction: new FormControl(examinant?.personFunction),
      mail: new FormControl(examinant?.mail, [Validators.required, AtlasCharsetsValidator.email]),
      judgementIcon: new FormControl(this.buildJudgementIcon(examinant?.judgement)),
      judgement: new FormControl(examinant?.judgement),
    });
  }

  static buildJudgementIcon(judgement?: JudgementType): string {
    switch (judgement) {
      case JudgementType.Yes:
        return 'bi-check-lg';
      case JudgementType.No:
        return 'bi-x-lg';
      default:
        return 'bi-hourglass-split';
    }
  }
}
