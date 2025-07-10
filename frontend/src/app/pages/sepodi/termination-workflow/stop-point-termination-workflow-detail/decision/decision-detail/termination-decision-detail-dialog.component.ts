import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DecisionDetailDialogData } from './termination-decision-detail-dialog.service';
import {
  DecisionType,
  ReadDecision,
  StopPointWorkflowService,
  WorkflowStatus,
} from 'src/app/api';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { AtlasButtonComponent } from '../../../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'termination-decision-detail-dialog',
  templateUrl: './termination-decision-detail-dialog.component.html',
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    DialogFooterComponent,
    AtlasButtonComponent,
    TranslatePipe,
  ],
})
export class TerminationDecisionDetailDialogComponent implements OnInit {
  protected readonly WorkflowStatus = WorkflowStatus;

  existingDecision!: ReadDecision;
  decisionForm = DecisionFormGroupBuilder.buildFormGroup();
  title = 'WORKFLOW.PERSON.JUDGEMENT';
  specialDecision = false;

  constructor(
    private dialogRef: MatDialogRef<TerminationDecisionDetailDialogComponent>,
    private stopPointWorkflowService: StopPointWorkflowService,
    @Inject(MAT_DIALOG_DATA)
    protected decisionDetailDialogData: DecisionDetailDialogData
  ) {}

  ngOnInit() {
    if (
      SPECIAL_DECISION_TYPES.includes(
        this.decisionDetailDialogData.examinant.value.decisionType!
      )
    ) {
      this.specialDecision = true;
      this.title =
        'WORKFLOW.STATUS.' + this.decisionDetailDialogData.workflowStatus;
    }
    this.decisionForm.patchValue(this.decisionDetailDialogData.examinant.value);
    if (this.decisionDetailDialogData.examinant.controls.judgement.value) {
      this.stopPointWorkflowService
        .getDecision(this.decisionDetailDialogData.examinant.controls.id.value!)
        .subscribe((decision) => {
          this.existingDecision = decision;
          this.decisionForm.controls.judgement.setValue(decision.judgement);
          this.decisionForm.controls.motivation.setValue(decision.motivation);
        });
    }
    this.decisionForm.disable();
  }

  get hasOverride() {
    return !!this.existingDecision?.fotJudgement;
  }

  get hasDecisionTypeVotedExpired() {
    return this.existingDecision?.decisionType === DecisionType.VotedExpiration;
  }

  close() {
    this.dialogRef.close();
  }

  overrideDecision() {
    this.decisionOverrideComponent.saveOverride();
  }
}
