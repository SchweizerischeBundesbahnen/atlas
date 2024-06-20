import { Component } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatStepperModule } from '@angular/material/stepper';
import { TranslateModule } from '@ngx-translate/core';
import { MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { AtlasCharsetsValidator } from '../../../../../core/validation/charsets/atlas-charsets-validator';
import { MatIconModule } from '@angular/material/icon';
import { FormModule } from '../../../../../core/module/form.module';
import { CoreModule } from '../../../../../core/module/core.module';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { take } from 'rxjs';

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
      decision: [undefined, Validators.required],
      comment: [''],
    },
    {
      validators: [], // todo: validate comment(radio-value)
    },
  );

  resendMailActive = true;

  constructor(
    private readonly _formBuilder: FormBuilder,
    private readonly _dialogService: DialogService,
    private readonly _dialogRef: MatDialogRef<DecisionDialogComponent>,
  ) {}

  nextStep() {
    console.log('next');
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
    console.log(this.decision.value);
  }
}
