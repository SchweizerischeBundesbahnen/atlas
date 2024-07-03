import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DecisionDetailDialogData} from './decision-detail-dialog.service';
import {DecisionOverrideComponent} from './override/decision-override.component';
import {DecisionFormGroupBuilder} from '../decision-form/decision-form-group';
import {ReadDecision, StopPointWorkflowService, WorkflowStatus} from 'src/app/api';

@Component({
  selector: 'decision-detail-dialog',
  templateUrl: './decision-detail-dialog.component.html',
})
export class DecisionDetailDialogComponent implements OnInit {

  protected readonly WorkflowStatus = WorkflowStatus;

  @ViewChild(DecisionOverrideComponent) decisionOverrideComponent!: DecisionOverrideComponent;

  existingDecision!: ReadDecision;
  decisionForm = DecisionFormGroupBuilder.buildFormGroup();

  constructor(
    private dialogRef: MatDialogRef<DecisionDetailDialogComponent>,
    private stopPointWorkflowService: StopPointWorkflowService,
    @Inject(MAT_DIALOG_DATA) protected decisionDetailDialogData: DecisionDetailDialogData,
  ) {}

  ngOnInit() {
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

  close() {
    this.dialogRef.close();
  }

  overrideDecision() {
    this.decisionOverrideComponent.saveOverride();
  }
}
