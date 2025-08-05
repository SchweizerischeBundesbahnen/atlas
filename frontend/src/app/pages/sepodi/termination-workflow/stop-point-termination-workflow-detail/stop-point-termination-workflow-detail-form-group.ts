import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { JudgementType } from '../../../../api';
import { TerminationDecision } from '../../../../api/model/terminationDecision';
import { DateService } from '../../../../core/date/date.service';
import { StopPointWorkflowDetailFormGroupBuilder } from '../../workflow/detail-page/detail-form/stop-point-workflow-detail-form-group';
import moment, { Moment } from 'moment/moment';
import { DecisionFormGroupBuilder } from '../../workflow/detail-page/decision/decision-form/decision-form-group';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { TerminationStopPointWorkflowModel } from '../../../../api/model/terminationStopPointWorkflowModel';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

export interface StopPointTerminationWorkflowDetailFormGroup {
  boTerminationDate: FormControl<string | null | undefined>;
  infoPlusTerminationDate: FormControl<string | null | undefined>;
  novaTerminationDate: FormControl<string | null | undefined>;
  workflowComment: FormControl<string | null | undefined>;
  abortComment: FormControl<string | null | undefined>;
  examinants: FormArray<FormGroup<TerminationDecisionFormGroup>>;
  infoPlus: FormGroup<TerminationDecisionFormGroup>;
  nova: FormGroup<TerminationDecisionFormGroup>;
}

export interface TerminationDecisionFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  examinantMail: FormControl<string | null | undefined>;
  judgement: FormControl<JudgementType | null | undefined>;
  motivation: FormControl<string | null | undefined>;
  judgementIcon: FormControl<string | null | undefined>;
  terminationDate: FormControl<Moment | null | undefined>;
  terminationDecisionPerson: FormControl<
    TerminationDecisionPersonEnum | null | undefined
  >;
}

export interface TerminationAbortFormGroup {
  abortComment: FormControl<string | null | undefined>;
}

export class StopPointTerminationWorkflowDetailFormGroupBuilder {
  static buildFormGroup(
    workflow: TerminationStopPointWorkflowModel
  ): FormGroup<StopPointTerminationWorkflowDetailFormGroup> {
    const terminationDecisions: TerminationDecision[] = [];
    terminationDecisions.push(<TerminationDecision>workflow.infoPlusDecision);
    terminationDecisions.push(<TerminationDecision>workflow.novaDecision);
    return new FormGroup<StopPointTerminationWorkflowDetailFormGroup>({
      boTerminationDate: new FormControl(
        workflow.boTerminationDate
          ? DateService.getDateFormatted(workflow?.boTerminationDate)
          : workflow?.boTerminationDate
      ),
      infoPlusTerminationDate: new FormControl(
        workflow.infoPlusTerminationDate
          ? DateService.getDateFormatted(workflow?.infoPlusTerminationDate)
          : workflow?.infoPlusTerminationDate
      ),
      novaTerminationDate: new FormControl(
        workflow.novaTerminationDate
          ? DateService.getDateFormatted(workflow?.novaTerminationDate)
          : workflow?.novaTerminationDate
      ),
      workflowComment: new FormControl(workflow.workflowComment),
      abortComment: new FormControl(workflow.abortComment),
      infoPlus: this.buildTerminationDecisionFormGroup(
        workflow.infoPlusDecision
      ),
      nova: this.buildTerminationDecisionFormGroup(workflow.novaDecision),
      examinants: new FormArray<FormGroup<TerminationDecisionFormGroup>>(
        terminationDecisions.map(
          (terminationDecision) =>
            this.buildTerminationDecisionFormGroup(terminationDecision) ?? []
        )
      ),
    });
  }

  static buildCancelTermination(): FormGroup<TerminationAbortFormGroup> {
    return new FormGroup<TerminationAbortFormGroup>({
      abortComment: new FormControl('', [
        Validators.required,
        AtlasFieldLengthValidator.comments,
      ]),
    });
  }

  static buildTerminationDecisionFormGroup(
    terminationDecision?: TerminationDecision
  ): FormGroup<TerminationDecisionFormGroup> {
    return new FormGroup<TerminationDecisionFormGroup>(
      {
        firstName: new FormControl(terminationDecision?.firstName),
        lastName: new FormControl(terminationDecision?.lastName),
        organisation: new FormControl(terminationDecision?.organisation, []),
        examinantMail: new FormControl(terminationDecision?.examinantMail, []),
        judgement: new FormControl(terminationDecision?.judgement, [
          Validators.required,
        ]),
        judgementIcon: new FormControl(
          StopPointWorkflowDetailFormGroupBuilder.buildJudgementIcon(
            terminationDecision?.judgement
          )
        ),
        terminationDate: new FormControl(
          terminationDecision?.terminationDate
            ? moment(terminationDecision.terminationDate)
            : null
        ),
        terminationDecisionPerson: new FormControl(
          terminationDecision?.terminationDecisionPerson
        ),
        motivation: new FormControl(terminationDecision?.motivation, [
          AtlasFieldLengthValidator.comments,
        ]),
      },
      {
        validators: DecisionFormGroupBuilder.conditionallyRequired(
          'judgement',
          'motivation'
        ),
      }
    );
  }

  static getTerminationDecision(
    form: FormGroup<TerminationDecisionFormGroup>
  ): TerminationDecision {
    return {
      judgement: form.controls.judgement.value!,
      motivation: form.controls.motivation.value!,
      terminationDecisionPerson: form.controls.terminationDecisionPerson.value!,
      terminationDate:
        form.controls.judgement.value === JudgementType.Yes
          ? form.controls.terminationDate.value!.toDate()
          : undefined,
    };
  }
}
