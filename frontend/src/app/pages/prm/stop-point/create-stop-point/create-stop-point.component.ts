import { Component, Input, ViewChild } from '@angular/core';
import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from '../form/stop-point-detail-form-group';
import { FormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { PrmMeanOfTransportHelper } from '../../prm-mean-of-transport-helper';
import { MeanOfTransport } from '../../../../api';
import { DialogService } from '../../../../core/components/dialog/dialog.service';

@Component({
  selector: 'app-create-stop-point',
  templateUrl: './create-stop-point.component.html',
  styleUrls: ['./create-stop-point.component.scss'],
})
export class CreateStopPointComponent {
  @ViewChild('stepper') stepper!: MatStepper;
  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  @Input() isAuthorizedToCreateStopPoint!: boolean;

  selectedMeansOfTransport!: MeanOfTransport[];
  isReduced = false;
  isDataEditable = false;
  isPreviousSelectionReduced: boolean | undefined;
  isMeanOfTransportSelected: boolean | undefined;
  formMeanOfTransport = StopPointFormGroupBuilder.buildMeansOfTransportForm();

  constructor(private dialogService: DialogService) {}

  backSelection() {
    this.isPreviousSelectionReduced = PrmMeanOfTransportHelper.isReduced(
      this.selectedMeansOfTransport,
    );
    this.isMeanOfTransportSelected = true;
    this.isDataEditable = false;
    this.stepper.previous();
  }

  checkSelection() {
    this.formMeanOfTransport.markAllAsTouched();
    if (this.formMeanOfTransport.valid) {
      const selectedMeansOfTransport = this.formMeanOfTransport.controls.meansOfTransport.value;
      if (selectedMeansOfTransport && selectedMeansOfTransport.length > 0) {
        this.isReduced = PrmMeanOfTransportHelper.isReduced(selectedMeansOfTransport);
        this.selectedMeansOfTransport = selectedMeansOfTransport;
        if (!this.isMeanOfTransportSelected || this.isReduced === this.isPreviousSelectionReduced) {
          this.initForm();
        } else if (this.isReduced !== this.isPreviousSelectionReduced) {
          this.confirmChangingRecodingVariant();
        }
      }
    }
  }

  initForm() {
    this.isDataEditable = true;
    this.form.enable();
    this.stepper.next();
  }

  private confirmChangingRecodingVariant() {
    return this.dialogService
      .confirm({
        title: 'PRM.STOP_POINTS.DIALOG.RECORDING_VARIANT_CHANGES',
        message: 'PRM.STOP_POINTS.DIALOG.CONFIRM_RECORDING_VARIANT_CHANGES',
      })
      .subscribe((isConfirmed) => {
        if (isConfirmed) {
          this.resetDataForm();
          if (!this.isReduced) {
            StopPointFormGroupBuilder.addCompleteRecordingValidation(this.form);
          } else {
            StopPointFormGroupBuilder.removeCompleteRecordingValidation(this.form);
          }
          this.initForm();
        }
      });
  }

  resetDataForm() {
    const alreadyStoredNumber = this.form.controls.number.value;
    const alreadyStoredSloid = this.form.controls.sloid.value;
    this.form.reset();
    this.form.controls.meansOfTransport.setValue(this.selectedMeansOfTransport);
    this.form.controls.number.setValue(alreadyStoredNumber);
    this.form.controls.sloid.setValue(alreadyStoredSloid);
  }
}
