import {Component, EventEmitter, Inject, ViewChild} from '@angular/core';
import {AbstractControl, FormBuilder, FormsModule, ReactiveFormsModule, ValidationErrors, Validators,} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatStepper, MatStepperModule} from '@angular/material/stepper';
import {TranslateModule} from '@ngx-translate/core';
import {MAT_DIALOG_DATA, MatDialogClose, MatDialogRef} from '@angular/material/dialog';
import {AtlasCharsetsValidator} from '../../../../../core/validation/charsets/atlas-charsets-validator';
import {MatIconModule} from '@angular/material/icon';
import {FormModule} from '../../../../../core/module/form.module';
import {CoreModule} from '../../../../../core/module/core.module';
import {DialogService} from '../../../../../core/components/dialog/dialog.service';
import {take} from 'rxjs';
import {AtlasFieldLengthValidator} from '../../../../../core/validation/field-lengths/atlas-field-length-validator';
import {Decision, JudgementType, ReadStopPointWorkflow, StopPointPerson, StopPointWorkflowService} from "../../../../../api";

@Component({
  selector: 'sepodi-wf-decision-dialog',
  standalone: true,
  imports: [
    MatButtonModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    TranslateModule,
    MatDialogClose,
    MatIconModule,
    FormModule,
    CoreModule,
  ],
  templateUrl: './decision-dialog.component.html',
  styleUrl: './decision-dialog.component.scss',
})
export class DecisionDialogComponent {
  @ViewChild('stepper') readonly stepper?: MatStepper;

  readonly obtainOtp = new EventEmitter<AbstractControl>();

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
      comment: ['', [AtlasFieldLengthValidator.comments]],
    },
    {
      validators: DecisionDialogComponent.decisionCommentValidator,
    },
  );

  private static decisionCommentValidator(control: AbstractControl): ValidationErrors | null {
    if (control.value.decision === false && control.value.comment.length === 0) {
      control.get('comment')?.setErrors({ decisionCommentRequired: true }); // todo: define translated error message
    } else {
      const errors: ValidationErrors | null = control.get('comment')!.errors;
      delete errors?.decisionCommentRequired;
      control.get('comment')?.setErrors(errors);
    }
    return null;
  }

  resendMailActive = true;
  verifiedExaminant?: StopPointPerson;

  constructor(
    private readonly _formBuilder: FormBuilder,
    private readonly _dialogService: DialogService,
    private readonly _dialogRef: MatDialogRef<DecisionDialogComponent>,
    private readonly stopPointWorkflowService: StopPointWorkflowService,
    @Inject(MAT_DIALOG_DATA) private data: { workflow: ReadStopPointWorkflow },
  ) {}

  nextStep() {}

  completeObtainOtpStep() {
    if (this.mail.valid) {
      this.obtainOtp.emit(this.mail.controls.mail);
      this.verifiedExaminant = undefined;
    }
  }

  resendMail() {
    // todo: trigger request on /getOtp
    this.resendMailActive = false;
    setTimeout(() => {
      this.resendMailActive = true;
    }, 10_000);
  }

  cancel() {
    this._dialogService
      .confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      })
      .pipe(take(1))
      .subscribe((closeConfirmed) => {
        if (closeConfirmed) {
          this._dialogRef.close();
        }
      });
  }

  sendDecision() {
    this.decision.markAllAsTouched();
    if (this.decision.valid) {
      console.log(this.decision.value);
      const decision: Decision = {
        examinantMail: this.mail.controls.mail.value!,
        pinCode: this.pin.controls.pin.value!,
        judgement: this.decision.controls.decision.value! ? JudgementType.Yes : JudgementType.No,
        motivation: this.decision.controls.comment.value!,
        firstName: this.decision.controls.firstName.value!,
        lastName: this.decision.controls.lastName.value!,
        organisation: this.decision.controls.organisation.value!,
        personFunction: this.decision.controls.function.value!,
      }
      this.stopPointWorkflowService.voteWorkflow(this.data.workflow.id!, this.verifiedExaminant!.id!, decision).subscribe(() => {
        console.log("Vote successful!");
      });
    }
  }

  verifyPin() {
    this.stopPointWorkflowService.verifyOtp(this.data.workflow.id!, {
      examinantMail: this.mail.controls.mail.value!,
      pinCode: this.pin.controls.pin.value!
    }).subscribe(examinant => {
      this.verifiedExaminant = examinant;
    });
  }
}
