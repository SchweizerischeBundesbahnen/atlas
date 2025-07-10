import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { JudgementType } from '../../../../api';
import { TerminationStopPointAddWorkflow } from '../../../../api/model/terminationStopPointAddWorkflow';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { TerminationDecision } from '../../../../api/model/terminationDecision';
import { DateService } from '../../../../core/date/date.service';
import { StopPointWorkflowDetailFormGroupBuilder } from '../../workflow/detail-page/detail-form/stop-point-workflow-detail-form-group';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

export interface StopPointTerminationWorkflowDetailFormGroup {
  boTerminationDate: FormControl<string | null | undefined>;
  infoPlusTerminationDate: FormControl<string | null | undefined>;
  novaTerminationDate: FormControl<string | null | undefined>;
  workflowComment: FormControl<string | null | undefined>;
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
  judgementIcon: FormControl<string | null | undefined>;
  terminationDate: FormControl<string | Date | null | undefined>;
  person: FormControl<TerminationDecisionPersonEnum | null | undefined>;
}

export class StopPointTerminationWorkflowDetailFormGroupBuilder {
  static buildFormGroup(
    workflow: TerminationStopPointAddWorkflow
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

  static buildTerminationDecisionFormGroup(
    terminationDecision?: TerminationDecision
  ): FormGroup<TerminationDecisionFormGroup> {
    return new FormGroup<TerminationDecisionFormGroup>({
      firstName: new FormControl(terminationDecision?.firstName),
      lastName: new FormControl(terminationDecision?.lastName),
      organisation: new FormControl(terminationDecision?.organisation, [
        Validators.required,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
      ]),
      examinantMail: new FormControl(terminationDecision?.examinantMail, [
        Validators.required,
        AtlasCharsetsValidator.email,
      ]),
      judgement: new FormControl(terminationDecision?.judgement),
      judgementIcon: new FormControl(
        StopPointWorkflowDetailFormGroupBuilder.buildJudgementIcon(
          terminationDecision?.judgement
        )
      ),
      terminationDate: new FormControl(
        terminationDecision?.terminationDate
          ? DateService.getDateFormatted(terminationDecision?.terminationDate)
          : terminationDecision?.terminationDate
      ),
      person: new FormControl(terminationDecision?.terminationDecisionPerson),
    });
  }
}
