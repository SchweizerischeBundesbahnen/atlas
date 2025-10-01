import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TerminationDecisionDetailDialogData } from './termination-decision-detail-dialog.service';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import {
  StopPointTerminationWorkflowDetailFormGroupBuilder,
  TerminationDecisionFormGroup,
} from '../../stop-point-termination-workflow-detail-form-group';
import { TerminationDecision } from '../../../../../../api/model/terminationDecision';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { JudgementType } from '../../../../../../api';
import { AtlasLabelFieldComponent } from '@atlas/form/atlas-label-field/atlas-label-field.component';
import { DateIconComponent } from '../../../../../../core/form-components/date-icon/date-icon.component';
import {
  MatDatepicker,
  MatDatepickerInput,
} from '@angular/material/datepicker';
import { MIN_DATE } from '../../../../../../core/date/date.service';
import { StopPointTerminationWorkflowService } from '../../../../../../api/service/workflow/stop-point-termination-workflow.service';
import { ValidationService } from '../../../../../../core/validation/validation.service';
import { TerminationWorkflowStatus } from '../../../../../../api/model/terminationWorkflowStatus';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

@Component({
  selector: 'termination-decision-detail-dialog',
  templateUrl: './termination-decision-detail-dialog.component.html',
  styleUrls: [
    '../../../../../../core/form-components/text-field/text-field.component.scss',
  ],
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    DialogFooterComponent,
    TranslatePipe,
    AtlasFieldErrorComponent,
    CommentComponent,
    MatRadioButton,
    MatRadioGroup,
    ReactiveFormsModule,
    AtlasLabelFieldComponent,
    DateIconComponent,
    MatDatepicker,
    MatDatepickerInput,
  ],
  providers: [TranslatePipe],
})
export class TerminationDecisionDetailDialogComponent implements OnInit {
  protected readonly JudgementType = JudgementType;

  private readonly dialogRef = inject(
    MatDialogRef<TerminationDecisionDetailDialogComponent>
  );
  private readonly decisionDetailDialogData: TerminationDecisionDetailDialogData =
    inject(MAT_DIALOG_DATA);
  private readonly terminationWorkflowService = inject(
    StopPointTerminationWorkflowService
  );

  form!: FormGroup<TerminationDecisionFormGroup>;
  examinant!: TerminationDecisionPersonEnum;
  readOnly = true;
  workflowStatus!: TerminationWorkflowStatus;
  minDate: Date = MIN_DATE;
  maxDate!: Date;

  ngOnInit() {
    this.examinant = this.decisionDetailDialogData.examinant;
    this.form = this.decisionDetailDialogData.decision;
    this.readOnly = this.decisionDetailDialogData.readOnly;
    this.workflowStatus = this.decisionDetailDialogData.workflowStatus;
    this.maxDate = this.decisionDetailDialogData.versionValidTo;
    this.minDate =
      this.form.controls.terminationDate.value?.toDate() ?? MIN_DATE;
    if (this.readOnly) {
      this.form.disable();
    }
  }

  close(result?: boolean) {
    this.dialogRef.close(result);
  }

  decide() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const terminationDecision =
        StopPointTerminationWorkflowDetailFormGroupBuilder.getTerminationDecision(
          this.form
        );
      this.form.disable();
      if (this.examinant === TerminationDecisionPersonEnum.InfoPlus) {
        this.terminationWorkflowService
          .decisionInfoPlus(
            this.decisionDetailDialogData.workflowId,
            terminationDecision
          )
          .subscribe(() => {
            this.close(true);
          });
      }
      if (this.examinant === TerminationDecisionPersonEnum.Nova) {
        this.terminationWorkflowService
          .decisionNova(
            this.decisionDetailDialogData.workflowId,
            terminationDecision
          )
          .subscribe(() => {
            this.close(true);
          });
      }
    }
  }
}
