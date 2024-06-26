import { Component, EventEmitter, NgZone, ViewChild } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { TranslateModule } from '@ngx-translate/core';
import { MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { AtlasCharsetsValidator } from '../../../../../core/validation/charsets/atlas-charsets-validator';
import { MatIconModule } from '@angular/material/icon';
import { FormModule } from '../../../../../core/module/form.module';
import { CoreModule } from '../../../../../core/module/core.module';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { take } from 'rxjs';
import { AtlasFieldLengthValidator } from '../../../../../core/validation/field-lengths/atlas-field-length-validator';
import { StopPointPerson } from '../../../../../api';

@Component({
  selector: 'sepodi-wf-decision-dialog',
  standalone: true,
  imports: [
    MatButtonModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogClose,
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    FormModule,
    CoreModule,
  ],
  templateUrl: './decision-dialog.component.html',
  styleUrl: './decision-dialog.component.scss',
})
export class DecisionDialogComponent {
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
    mail: AbstractControl;
    pin: AbstractControl;
    decision: FormGroup;
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

  readonly decision = this._formBuilder.group(
    {
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      organisation: ['', Validators.required],
      function: ['', Validators.required],
      decision: [null, Validators.required],
      comment: [null, [AtlasFieldLengthValidator.comments]],
    },
    {
      validators: DecisionDialogComponent.decisionCommentValidator,
    },
  );

  private static decisionCommentValidator(control: AbstractControl): ValidationErrors | null {
    if (
      control.value.decision === false &&
      (!control.value.comment || control.value.comment.length === 0)
    ) {
      control.get('comment')?.setErrors({ decision_comment_required: true });
    } else {
      const errors: ValidationErrors | null = control.get('comment')!.errors;
      delete errors?.decision_comment_required;
      control.get('comment')?.setErrors(errors);
    }
    return null;
  }

  loading = false;
  private _swapLoading() {
    this.loading = !this.loading;
  }

  resendMailActive = true;
  private _verifiedExaminant?: StopPointPerson;

  constructor(
    private readonly _formBuilder: FormBuilder,
    private readonly _dialogService: DialogService,
    private readonly _dialogRef: MatDialogRef<DecisionDialogComponent>,
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
          this.decision.controls.function.setValue(examinant.personFunction ?? null);
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
        mail: this.mail.controls.mail,
        pin: this.pin.controls.pin,
        decision: this.decision,
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
