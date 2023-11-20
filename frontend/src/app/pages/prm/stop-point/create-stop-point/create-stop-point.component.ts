import { Component, ViewChild } from '@angular/core';
import {
  MeanOfTransportFormGroup,
  StopPointDetailFormGroup,
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

  form = new FormGroup<StopPointDetailFormGroup>({
    number: new FormControl(),
    sloid: new FormControl(),
    meansOfTransport: new FormControl(),
    freeText: new FormControl(),
    address: new FormControl(),
    zipCode: new FormControl(),
    city: new FormControl(),
    alternativeTransport: new FormControl(),
    alternativeTransportCondition: new FormControl(),
    assistanceAvailability: new FormControl(),
    assistanceCondition: new FormControl(),
    assistanceService: new FormControl(),
    audioTicketMachine: new FormControl(),
    additionalInformation: new FormControl(),
    dynamicAudioSystem: new FormControl(),
    dynamicOpticSystem: new FormControl(),
    infoTicketMachine: new FormControl(),
    interoperable: new FormControl(),
    url: new FormControl(),
    visualInfo: new FormControl(),
    wheelchairTicketMachine: new FormControl(),
    assistanceRequestFulfilled: new FormControl(),
    ticketMachine: new FormControl(),
    validFrom: new FormControl(),
    validTo: new FormControl(),
    etagVersion: new FormControl(),
    creationDate: new FormControl(),
    editionDate: new FormControl(),
    editor: new FormControl(),
    creator: new FormControl(),
  });

  isReduced = false;

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
      this.stepper.next();
    } else {
      this.formMeanOfTransport.controls['meansOfTransport'].setErrors({ required: '' });
    }
  }
  backSelection() {
    this.formMeanOfTransport.controls['meansOfTransport'].setValue([]);
    this.stepper.previous();
  }
}
