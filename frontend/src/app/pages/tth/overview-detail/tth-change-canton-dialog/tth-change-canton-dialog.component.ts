import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { Subject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from '../../../../core/notification/notification.service';
import { TthChangeCantonFormGroup } from './model/tth-change-canton-form-group';
import { ChangeCantonData } from './model/change-canton-data';
import { takeUntil } from 'rxjs/operators';
import { ValidationService } from 'src/app/core/validation/validation.service';
import { TimetableHearingStatementsService } from '../../../../api';
import { BaseChangeDialogComponent } from '../base-change-dialog/base-change-dialog.component';

@Component({
    selector: 'app-tth-change-canton-dialog',
    templateUrl: './tth-change-canton-dialog.component.html',
    imports: [BaseChangeDialogComponent, ReactiveFormsModule]
})
export class TthChangeCantonDialogComponent {
  formGroup = new FormGroup<TthChangeCantonFormGroup>({
    comment: new FormControl('', [AtlasFieldLengthValidator.length_280]),
  });

  private ngUnsubscribe = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<TthChangeCantonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ChangeCantonData,
    private readonly notificationService: NotificationService,
    private readonly timetableHearingStatementsServiceV2: TimetableHearingStatementsService,
  ) {}

  onClick() {
    let comment: string | undefined;
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      if (this.formGroup.controls['comment'].value) {
        comment = this.formGroup.controls['comment'].value;
      }
      this.timetableHearingStatementsServiceV2
        .updateHearingCanton({
          ids: this.data.tths.map((value) => Number(value.id)),
          comment: comment,
          swissCanton: this.data.swissCanton,
        })
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(() => {
          this.notificationService.success('TTH.NOTIFICATION.CANTON_CHANGE.SUCCESS');
          this.dialogRef.close(true);
        });
    }
  }
}
