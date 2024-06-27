import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatStepperModule } from '@angular/material/stepper';
import { TranslateModule } from '@ngx-translate/core';
import { MAT_DIALOG_DATA, MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { DecisionDetailDialogData } from './decision-detail-dialog.service';
import { DecisionOverrideComponent } from './override/decision-override.component';
import { DecisionFormComponent } from '../decision-form/decision-form.component';
import { DecisionFormGroupBuilder } from '../decision-form/decision-form-group';
import { FormModule } from 'src/app/core/module/form.module';
import { CoreModule } from 'src/app/core/module/core.module';
import { ReadDecision, StopPointWorkflowService } from 'src/app/api';

@Component({
  selector: 'decision-detail-dialog',
  standalone: true,
  imports: [
    MatButtonModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    TranslateModule,
    MatDialogClose,
    MatIconModule,
    FormModule,
    CoreModule,
    DecisionOverrideComponent,
    DecisionFormComponent,
  ],
  templateUrl: './decision-detail-dialog.component.html',
})
export class DecisionDetailDialogComponent implements OnInit {
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
