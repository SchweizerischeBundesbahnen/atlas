import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { Subject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from '../../../../core/notification/notification.service';
import { TimetableHearingService } from '../../../../api';
import { TthChangeCantonFormGroup } from './model/tth-change-canton-form-group';
import { ChangeCantonData } from './model/change-canton-data';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-tth-change-canton-dialog',
  templateUrl: './tth-change-canton-dialog.component.html',
})
export class TthChangeCantonDialogComponent {
  formGroup = new FormGroup<TthChangeCantonFormGroup>({
    comment: new FormControl('', [
      AtlasFieldLengthValidator.length_280,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      AtlasCharsetsValidator.iso88591,
    ]),
  });

  private ngUnsubscribe = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<TthChangeCantonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ChangeCantonData,
    private readonly notificationService: NotificationService,
    private readonly timetableHearingService: TimetableHearingService
  ) {}

  onClick() {
    let comment: string | undefined;
    if (this.formGroup.valid) {
      if (this.formGroup.controls['comment'].value) {
        comment = this.formGroup.controls['comment'].value;
      }
      this.timetableHearingService
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
