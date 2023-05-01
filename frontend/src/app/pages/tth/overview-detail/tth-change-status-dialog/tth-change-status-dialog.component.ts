import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StatusChangeData } from './model/status-change-data';
import { TimetableHearingService } from '../../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from 'src/app/core/components/dialog/dialog.service';
import { Subject, takeUntil } from 'rxjs';
import { TthChangeStatusFormGroup } from './model/tth-change-status-form-group';

@Component({
  selector: 'app-tth-change-status-dialog',
  templateUrl: './tth-change-status-dialog.component.html',
  styleUrls: ['./tth-change-status-dialog.component.scss'],
})
export class TthChangeStatusDialogComponent {
  formGroup = new FormGroup<TthChangeStatusFormGroup>({
    justification: new FormControl(this.data.justification, [
      AtlasFieldLengthValidator.statement,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      AtlasCharsetsValidator.iso88591,
    ]),
  });
  private ngUnsubscribe = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<TthChangeStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StatusChangeData,
    private readonly notificationService: NotificationService,
    private readonly timetableHearingService: TimetableHearingService,
    private readonly dialogService: DialogService
  ) {}

  onClick(): void {
    let justification: string | undefined;
    if (this.formGroup.valid) {
      if (this.formGroup.controls['justification'].value) {
        justification = this.formGroup.controls['justification'].value;
      }
      this.timetableHearingService
        .updateHearingStatementStatus(this.data.statementStatus, {
          ids: this.data.tths.map((value) => Number(value.id)),
          justification: justification,
        })
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(() => {
          this.notificationService.success('TTH.NOTIFICATION.STATUS_CHANGE.SUCCESS');
          this.dialogRef.close();
        });
    }
  }

  closeDialog() {
    if (this.formGroup.dirty) {
      this.dialogService.confirmLeave().subscribe((confirm) => {
        if (confirm) {
          this.dialogRef.close();
        }
      });
    } else {
      this.dialogRef.close();
    }
  }
}
