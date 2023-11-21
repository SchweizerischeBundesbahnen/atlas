import { Component, Input, ViewChild } from '@angular/core';
import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from '../form/stop-point-detail-form-group';
import { FormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { PrmMeanOfTransportHelper } from '../../prm-mean-of-transport-helper';
import { MeanOfTransport } from '../../../../api';

@Component({
  selector: 'app-create-stop-point',
  templateUrl: './create-stop-point.component.html',
  styleUrls: ['./create-stop-point.component.scss'],
})
export class CreateStopPointComponent {
  @ViewChild('stepper') stepper!: MatStepper;
  selectedMeansOfTransport!: MeanOfTransport[];

  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  isReduced = false;
  isDataEditable = false;

  formMeanOfTransport = StopPointFormGroupBuilder.buildMeansOfTransportForm();
  checkSelection() {
    const selectedMeansOfTransport = this.formMeanOfTransport.controls['meansOfTransport'].value;
    if (selectedMeansOfTransport && selectedMeansOfTransport.length > 0) {
      this.selectedMeansOfTransport = selectedMeansOfTransport;
      this.isReduced = PrmMeanOfTransportHelper.isReduced(selectedMeansOfTransport);
      this.isDataEditable = true;
      this.stepper.next();
      this.form.enable();
    } else {
      this.formMeanOfTransport.controls['meansOfTransport'].setErrors({ required: '' });
    }
  }
  backSelection() {
    //todo: prune form
    this.isDataEditable = false;
    this.stepper.previous();
  }
}
