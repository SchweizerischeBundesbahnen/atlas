import { Component, Input, ViewChild } from '@angular/core';
import {
  MeanOfTransportFormGroup,
  StopPointFormGroupBuilder,
} from '../form/stop-point-detail-form-group';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { PrmMeanOfTransportValidator } from './prm-mean-of-transport-validator';
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

  @Input() form = StopPointFormGroupBuilder.buildEmptyCompleteFormGroup();
  isReduced = false;
  isDataEditable = false;

  formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
    meansOfTransport: new FormControl(
      [],
      [Validators.required, PrmMeanOfTransportValidator.isReducedOrComplete],
    ),
  });

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
    this.isDataEditable = false;
    this.stepper.previous();
  }
}
