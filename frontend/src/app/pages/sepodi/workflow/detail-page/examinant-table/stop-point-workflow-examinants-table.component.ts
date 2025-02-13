import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';
import { ReadStopPointWorkflow, WorkflowStatus } from 'src/app/api';
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder
} from '../detail-form/stop-point-workflow-detail-form-group';
import {ValidationService} from "../../../../../core/validation/validation.service";

@Component({
  selector: 'stop-point-workflow-examinants-table',
  templateUrl: './stop-point-workflow-examinants-table.component.html',
  styleUrls: ['./stop-point-workflow-examinants-table.component.scss'],
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class StopPointWorkflowExaminantsTableComponent {
  readonly WorkflowStatus = WorkflowStatus;

  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  @Input() currentWorkflow?: ReadStopPointWorkflow;
  @Input() isDeleteButtonInvisible = false;

  @Output() onOpenDecision = new EventEmitter<number>();
  @Output() onRemoveExaminant = new EventEmitter<number>();


  openDecision(index: number) {
    this.onOpenDecision.emit(index);
  }

  removeExaminant(index: number) {
    this.onRemoveExaminant.emit(index);
  }

  addExaminant() {
    const examinantsControl = this.form.controls.examinants;
    ValidationService.validateForm(examinantsControl);
    if (examinantsControl.disabled || examinantsControl.valid) {
      examinantsControl.push(StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup());
    }
  }

}
