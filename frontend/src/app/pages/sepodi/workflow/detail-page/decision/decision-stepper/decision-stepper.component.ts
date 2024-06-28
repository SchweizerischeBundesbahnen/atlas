import { Component, EventEmitter, ViewChild } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { MatDialogRef } from '@angular/material/dialog';
import { take } from 'rxjs';
import { DecisionFormGroupBuilder } from '../decision-form/decision-form-group';
import { DecisionFormComponent } from '../decision-form/decision-form.component';
import { FormModule } from 'src/app/core/module/form.module';
import { CoreModule } from 'src/app/core/module/core.module';
import { Decision, StopPointPerson } from 'src/app/api';
import { AtlasCharsetsValidator } from 'src/app/core/validation/charsets/atlas-charsets-validator';
import { DialogService } from 'src/app/core/components/dialog/dialog.service';

@Component({
  selector: 'sepodi-wf-decision-stepper',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, FormModule, CoreModule, DecisionFormComponent],
  templateUrl: './decision-stepper.component.html',
})
export class DecisionStepperComponent {
  @ViewChild('stepper') readonly stepper?: MatStepper;

  readonly obtainOtp = new EventEmitter<{
    mail: AbstractControl;
    continue: () => void;
    swapLoading: () => void;
  }>();

  readonly verifyPin = new EventEmitter<{
    mail: AbstractControl;
    pin: AbstractControl;
    continue: (examinant: StopPointPerson) => void;
    swapLoading: () => void;
  }>();

  readonly sendDecision = new EventEmitter<{
    decision: Decision;
    verifiedExaminant: StopPointPerson;
    swapLoading: () => void;
  }>();

  readonly mail = this._formBuilder.group({
    mail: ['', [Validators.required, AtlasCharsetsValidator.email]],
  });

  readonly pin = this._formBuilder.group({
    pin: [
      '',
      [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(6),
        AtlasCharsetsValidator.numeric,
      ],
    ],
  });

  readonly decision = DecisionFormGroupBuilder.buildFormGroup();

  loading = false;
  private _swapLoading() {
    this.loading = !this.loading;
  }

  resendMailActive = true;
  private _verifiedExaminant?: StopPointPerson;

  constructor(
    private readonly _formBuilder: FormBuilder,
    private readonly _dialogService: DialogService,
    private readonly _dialogRef: MatDialogRef<DecisionStepperComponent>,
  ) {}

  completeObtainOtpStep() {
    this.mail.markAllAsTouched();
    if (this.mail.valid) {
      this.obtainOtp.emit({
        mail: this.mail.controls.mail,
        continue: () => {
          this._stepNext();
        },
        swapLoading: () => this._swapLoading(),
      });
      this._verifiedExaminant = undefined;
    }
  }

  completeVerifyPinStep() {
    this.pin.markAllAsTouched();
    if (this.pin.valid) {
      this.verifyPin.emit({
        mail: this.mail.controls.mail,
        pin: this.pin.controls.pin,
        continue: (examinant) => {
          this._verifiedExaminant = examinant;
          this.decision.controls.firstName.setValue(examinant.firstName ?? null);
          this.decision.controls.lastName.setValue(examinant.lastName ?? null);
          this.decision.controls.organisation.setValue(examinant.organisation);
          this.decision.controls.personFunction.setValue(examinant.personFunction ?? null);
          this._stepNext();
        },
        swapLoading: () => this._swapLoading(),
      });
    }
  }

  private _stepNext() {
    if (this.stepper?.selected) {
      this.stepper.selected.completed = true;
      this.stepper.next();
    } else {
      throw 'Step must be selected at this stage';
    }
  }

  completeDecision() {
    this.decision.markAllAsTouched();
    if (this.decision.valid) {
      this.sendDecision.emit({
        decision: {
          examinantMail: this.mail.controls.mail.value!,
          pinCode: this.pin.controls.pin.value!,
          judgement: this.decision.controls.judgement.value!,
          motivation:
            this.decision.controls.motivation.value?.length === 0
              ? undefined
              : this.decision.controls.motivation.value!,
          firstName: this.decision.controls.firstName.value!,
          lastName: this.decision.controls.lastName.value!,
          organisation: this.decision.controls.organisation.value!,
          personFunction: this.decision.controls.personFunction.value!,
        },
        verifiedExaminant: this._verifiedExaminant!,
        swapLoading: () => this._swapLoading(),
      });
    }
  }

  resendMail() {
    this.obtainOtp.emit({
      mail: this.mail.controls.mail,
      continue: () => {
        this.resendMailActive = false;
        setTimeout(() => {
          this.resendMailActive = true;
        }, 10_000);
      },
      swapLoading: () => this._swapLoading(),
    });
  }

  cancel() {
    if (this.stepper?.selectedIndex === 0) {
      this._dialogRef.close();
      return;
    }
    this._dialogService
      .confirm({
        title: 'DIALOG.CANCEL_DECISION_TITLE',
        message: 'DIALOG.CANCEL_DECISION',
      })
      .pipe(take(1))
      .subscribe((closeConfirmed) => {
        if (closeConfirmed) {
          this._dialogRef.close();
        }
      });
  }
}
