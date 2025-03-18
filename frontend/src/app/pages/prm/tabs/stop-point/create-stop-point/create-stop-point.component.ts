import { Component, Input, ViewChild } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatStepper, MatStep, MatStepLabel, MatStepperIcon } from '@angular/material/stepper';
import { PrmMeanOfTransportHelper } from '../../../util/prm-mean-of-transport-helper';
import { MeanOfTransport } from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from '../form/stop-point-detail-form-group';
import { prmMeansOfTransport } from '../prm-variant-info.service';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { NgIf } from '@angular/common';
import { MeansOfTransportPickerComponent } from '../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { StopPointReducedFormComponent } from '../form/stop-point-reduced-form/stop-point-reduced-form.component';
import { StopPointCompleteFormComponent } from '../form/stop-point-complete-form/stop-point-complete-form.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-create-stop-point',
    templateUrl: './create-stop-point.component.html',
    styleUrls: ['./create-stop-point.component.scss'],
    imports: [NgIf, MatStepper, MatStep, MatStepLabel, MeansOfTransportPickerComponent, ReactiveFormsModule, AtlasButtonComponent, StopPointReducedFormComponent, StopPointCompleteFormComponent, MatStepperIcon, TranslatePipe]
})
export class CreateStopPointComponent implements DetailFormComponent {
  @ViewChild('stepper') stepper!: MatStepper;
  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  @Input() isAuthorizedToCreateStopPoint!: boolean;

  selectedMeansOfTransport!: MeanOfTransport[];
  isReduced = false;
  isDataEditable = false;
  isPreviousSelectionReduced?: boolean;
  isMeanOfTransportSelected?: boolean;
  formMeanOfTransport = StopPointFormGroupBuilder.buildMeansOfTransportForm();
  meansOfTransportToShow = prmMeansOfTransport;

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
    const previousSelectedNumber = this.form.controls.number.value;
    const previousSelectedSloid = this.form.controls.sloid.value;
    this.form.reset();
    this.form.controls.meansOfTransport.setValue(this.selectedMeansOfTransport);
    this.form.controls.number.setValue(previousSelectedNumber);
    this.form.controls.sloid.setValue(previousSelectedSloid);
  }
}
