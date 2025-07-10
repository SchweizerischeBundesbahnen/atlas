import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TerminationDecisionDetailDialogData } from './termination-decision-detail-dialog.service';
import { ReadDecision, WorkflowStatus } from 'src/app/api';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import {
  StopPointTerminationWorkflowDetailFormGroupBuilder,
  TerminationDecisionFormGroup,
} from '../../stop-point-termination-workflow-detail-form-group';
import { TerminationDecision } from '../../../../../../api/model/terminationDecision';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'termination-decision-detail-dialog',
  templateUrl: './termination-decision-detail-dialog.component.html',
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    DialogFooterComponent,
    TranslatePipe,
  ],
})
export class TerminationDecisionDetailDialogComponent implements OnInit {
  protected readonly WorkflowStatus = WorkflowStatus;

  existingDecision!: ReadDecision;
  decisionForm =
    StopPointTerminationWorkflowDetailFormGroupBuilder.buildTerminationDecisionFormGroup();
  title = 'WORKFLOW.PERSON.JUDGEMENT';
  specialDecision = false;

  decisionFormGroup!: FormGroup<TerminationDecisionFormGroup>;
  person!: TerminationDecisionPersonEnum;

  private readonly dialogRef = inject(
    MatDialogRef<TerminationDecisionDetailDialogComponent>
  );
  private readonly decisionDetailDialogData: TerminationDecisionDetailDialogData =
    inject(MAT_DIALOG_DATA);

  ngOnInit() {
    this.decisionFormGroup = this.decisionDetailDialogData.decision;
    this.person = this.decisionFormGroup.controls.person.value!;
    // if (
    //   SPECIAL_DECISION_TYPES.includes(
    //     this.decisionDetailDialogData.examinant.value.decisionType!
    //   )
    // ) {
    //   this.specialDecision = true;
    //   this.title =
    //     'WORKFLOW.STATUS.' + this.decisionDetailDialogData.workflowStatus;
    // }
    // this.decisionForm.patchValue(this.decisionDetailDialogData.examinant.value);
    // if (this.decisionDetailDialogData.examinant.controls.judgement.value) {
    //   this.stopPointWorkflowService
    //     .getDecision(this.decisionDetailDialogData.examinant.controls.id.value!)
    //     .subscribe((decision) => {
    //       this.existingDecision = decision;
    //       this.decisionForm.controls.judgement.setValue(decision.judgement);
    //       this.decisionForm.controls.motivation.setValue(decision.motivation);
    //     });
    // }
    this.decisionForm.disable();
  }

  close() {
    this.dialogRef.close();
  }
}
