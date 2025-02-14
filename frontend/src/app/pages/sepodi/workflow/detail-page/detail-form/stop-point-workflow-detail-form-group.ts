import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {DecisionType, JudgementType, ReadStopPointWorkflow, StopPointPerson} from 'src/app/api';
import {AtlasCharsetsValidator} from 'src/app/core/validation/charsets/atlas-charsets-validator';
import {AtlasFieldLengthValidator} from 'src/app/core/validation/field-lengths/atlas-field-length-validator';
import {WhitespaceValidator} from '../../../../../core/validation/whitespace/whitespace-validator';
import {UniqueEmailsValidator} from "../../../../../core/validation/unique-emails-validator/unique-emails-validator";

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
  decisionType: FormControl<DecisionType | null | undefined>;
  defaultExaminant: FormControl<boolean | null | undefined>;
}

export const SPECIAL_DECISION_TYPES = [DecisionType.Canceled, DecisionType.Rejected, DecisionType.Restarted];

export class StopPointWorkflowDetailFormGroupBuilder {
  static buildFormGroup(
    workflow?: ReadStopPointWorkflow
  ): FormGroup<StopPointWorkflowDetailFormGroup> {
    return new FormGroup<StopPointWorkflowDetailFormGroup>({
      ccEmails: new FormControl(workflow?.ccEmails ?? []),
      workflowComment: new FormControl(workflow?.workflowComment, [
        Validators.required,
        Validators.minLength(2),
        AtlasFieldLengthValidator.comments,
      ]),
      designationOfficial: new FormControl(workflow?.designationOfficial),
      examinants: new FormArray<FormGroup<ExaminantFormGroup>>(
        workflow?.examinants
          ?.filter(
            (examinant) =>
              !SPECIAL_DECISION_TYPES.includes(examinant.decisionType!)
          )
          .map((examinant) => this.buildExaminantFormGroup(examinant)) ?? [],
        {
          validators: UniqueEmailsValidator.uniqueEmails(),
        }
      ),
    });
  }

  static buildExaminantFormGroup(examinant?: StopPointPerson): FormGroup<ExaminantFormGroup> {
    const formGroup = new FormGroup<ExaminantFormGroup>({
      id: new FormControl(examinant?.id),
      firstName: new FormControl(examinant?.firstName),
      lastName: new FormControl(examinant?.lastName),
      organisation: new FormControl(examinant?.organisation, [
        Validators.required,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
      ]),
      personFunction: new FormControl(examinant?.personFunction),
      mail: new FormControl(examinant?.mail, [
        Validators.required,
        AtlasCharsetsValidator.email,
      ]),
      judgementIcon: new FormControl(
        this.buildJudgementIcon(examinant?.judgement)
      ),
      judgement: new FormControl(examinant?.judgement),
      decisionType: new FormControl(examinant?.decisionType),
      defaultExaminant: new FormControl(examinant?.defaultExaminant),
    });
    this.disableDefaultExaminants(formGroup);
    return formGroup;
  }

  static disableDefaultExaminants(form: FormGroup<ExaminantFormGroup>): void {
    if (form.controls.defaultExaminant.value) {
      form.disable();
    }
  }

  static disableDefaultExaminantsInArray(formArray: FormArray<FormGroup<ExaminantFormGroup>>): void {
    for (let i = 0; i < formArray.length; i++) {
      this.disableDefaultExaminants(formArray.at(i));
    }
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
