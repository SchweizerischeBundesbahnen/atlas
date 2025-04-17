import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  ControlContainer,
  FormArray,
  FormGroup,
  NgForm,
  ReactiveFormsModule,
} from '@angular/forms';
import { WorkflowStatus } from 'src/app/api';
import {
  ExaminantFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from '../detail-form/stop-point-workflow-detail-form-group';
import { ValidationService } from '../../../../../core/validation/validation.service';
import { NgFor, NgClass } from '@angular/common';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'stop-point-workflow-examinants-table',
  templateUrl: './stop-point-workflow-examinants-table.component.html',
  styleUrls: ['./stop-point-workflow-examinants-table.component.scss'],
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    ReactiveFormsModule,
    NgFor,
    TextFieldComponent,
    AtlasButtonComponent,
    NgClass,
    TranslatePipe,
  ],
})
export class StopPointWorkflowExaminantsTableComponent {
  readonly WorkflowStatus = WorkflowStatus;

  @Input() form!: FormGroup;
  @Input() currentWorkflowStatus?: WorkflowStatus;

  @Output() openDecision = new EventEmitter<number>();

  onOpenDecision(index: number) {
    this.openDecision.emit(index);
  }

  removeExaminant(index: number) {
    this.examinants.removeAt(index);
    this.form.markAsDirty();
  }

  addExaminant() {
    ValidationService.validateForm(this.examinants);
    if (this.examinants.disabled || this.examinants.valid) {
      this.examinants.push(
        StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup()
      );
    }
  }

  get examinants() {
    return this.form.controls.examinants as FormArray<
      FormGroup<ExaminantFormGroup>
    >;
  }
}
