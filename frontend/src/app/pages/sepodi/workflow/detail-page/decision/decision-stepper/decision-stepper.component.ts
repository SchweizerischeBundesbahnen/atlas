import { ChangeDetectorRef, Component, Inject, ViewChild } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { catchError, EMPTY, Observable, of, share, take } from 'rxjs';
import { DecisionFormGroupBuilder } from '../decision-form/decision-form-group';
import { StopPointPerson, StopPointWorkflowService } from 'src/app/api';
import { AtlasCharsetsValidator } from 'src/app/core/validation/charsets/atlas-charsets-validator';
import { DialogService } from 'src/app/core/components/dialog/dialog.service';
import { map } from 'rxjs/operators';
import { FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'sepodi-wf-decision-stepper',
  templateUrl: './decision-stepper.component.html',
})
export class DecisionStepperComponent {
  @ViewChild('stepper') readonly stepper?: MatStepper;

  isStepOneCompl$: Observable<boolean> = of();
  isStepTwoCompl$: Observable<boolean> = of();
  isStepThreeCompl$: Observable<boolean> = of();

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
    private readonly _spWfService: StopPointWorkflowService,
    @Inject(MAT_DIALOG_DATA) private readonly _workflowId: number,
    private readonly cd: ChangeDetectorRef,
  ) {}

  completeObtainOtpStep() {
    this.mail.markAllAsTouched();
    if (this.mail.valid) {
      this._swapLoading();
      this.isStepOneCompl$ = this._spWfService
        .obtainOtp(this._workflowId, {
          examinantMail: this.mail.controls.mail.value!,
        })
        .pipe(
          map(() => {
            return true;
          }),
          catchError(() => {
            this._swapLoading();
            return EMPTY;
          }),
          share(),
        );

      this.isStepTwoCompl$ = this.isStepOneCompl$.pipe(
        map(() => {
          this.cd.detectChanges();
          this.stepper?.next();
          this._swapLoading();
          return false;
        }),
      );
    }
  }

  completeVerifyPinStep() {
    this.pin.markAllAsTouched();
    if (this.pin.valid) {
      this._swapLoading();
      this.isStepTwoCompl$ = this._spWfService
        .verifyOtp(this._workflowId, {
          examinantMail: this.mail.controls.mail.value!,
          pinCode: this.pin.controls.pin.value!,
        })
        .pipe(
          map((examinant) => {
            this._verifiedExaminant = examinant;
            this.decision.controls.firstName.setValue(examinant.firstName ?? null);
            this.decision.controls.lastName.setValue(examinant.lastName ?? null);
            this.decision.controls.organisation.setValue(examinant.organisation);
            this.decision.controls.personFunction.setValue(examinant.personFunction ?? null);
            return true;
          }),
          catchError(() => {
            this._swapLoading();
            return EMPTY;
          }),
          share(),
        );

      this.isStepThreeCompl$ = this.isStepTwoCompl$.pipe(
        map(() => {
          this.cd.detectChanges();
          this.stepper?.next();
          this._swapLoading();
          return false;
        }),
      );
    }
  }

  completeDecision() {
    this.decision.markAllAsTouched();
    if (this.decision.valid) {
      this._swapLoading();
      this.isStepThreeCompl$ = this._spWfService
        .voteWorkflow(this._workflowId, this._verifiedExaminant!.id!, {
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
        })
        .pipe(
          map(() => {
            this._dialogRef.close(true);
            return true;
          }),
          catchError(() => {
            this._swapLoading();
            return EMPTY;
          }),
        );
    }
  }

  resendMail() {
    this._swapLoading();
    this.isStepTwoCompl$ = this._spWfService
      .obtainOtp(this._workflowId, {
        examinantMail: this.mail.controls.mail.value!,
      })
      .pipe(
        map(() => {
          this.resendMailActive = false;
          setTimeout(() => {
            this.resendMailActive = true;
          }, 10_000);
          this._swapLoading();
          return false;
        }),
        catchError(() => {
          this._swapLoading();
          return EMPTY;
        }),
      );
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
