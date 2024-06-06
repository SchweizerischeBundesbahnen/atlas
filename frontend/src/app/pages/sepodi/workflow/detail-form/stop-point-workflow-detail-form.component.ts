import {Component, Input} from '@angular/core';
import {ReadServicePointVersion} from "../../../../api";
import {ControlContainer, FormGroup, NgForm} from "@angular/forms";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {StopPointWorkflowDetailFormGroup, StopPointWorkflowDetailFormGroupBuilder} from "./stop-point-workflow-detail-form-group";
import {ValidationService} from "../../../../core/validation/validation.service";

@Component({
  selector: 'stop-point-workflow-detail-form',
  templateUrl: './stop-point-workflow-detail-form.component.html',
  styleUrls: ['./stop-point-workflow-detail-form.component.scss'],
  viewProviders: [{provide: ControlContainer, useExisting: NgForm}],
})
export class StopPointWorkflowDetailFormComponent {

  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];

  @Input() stopPoint!: ReadServicePointVersion;
  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  addExaminant() {
    const examinantsControl = this.form.controls.examinants;
    ValidationService.validateForm(examinantsControl);
    if (examinantsControl.valid) {
      examinantsControl.push(StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup());
    }
  }

  removeExaminant(index: number) {
    this.form.controls.examinants.removeAt(index);
  }
}
