import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StatusChangeData } from './status-change-data';
import { TimetableHearingService } from '../../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from 'src/app/core/components/dialog/dialog.service';

@Component({
  selector: 'app-tth-change-status-dialog',
  templateUrl: './tth-change-status-dialog.component.html',
  styleUrls: ['./tth-change-status-dialog.component.scss'],
})
export class TthChangeStatusDialogComponent {
  tthChangeStatusFormGroup = new FormGroup({
    justification: new FormControl(this.data.ths.justification, [
      AtlasFieldLengthValidator.comments,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      AtlasCharsetsValidator.iso88591,
    ]),
  });

  constructor(
    public dialogRef: MatDialogRef<TthChangeStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StatusChangeData,
    private readonly notificationService: NotificationService,
    private readonly timetableHearingService: TimetableHearingService,
    private readonly dialogService: DialogService
  ) {}

  onClick(): void {
    console.log('asdasdas');
    if (this.tthChangeStatusFormGroup.valid) {
      if (this.tthChangeStatusFormGroup.controls['justification'].value) {
        this.data.ths.justification = this.tthChangeStatusFormGroup.controls['justification'].value;
      }
      this.timetableHearingService
        .updateHearingStatement(this.data.id, this.data.ths)
        .subscribe((r) => {
          this.notificationService.success('WORKFLOW.NOTIFICATION.START.SUCCESS');
          this.dialogRef.close();
        });
    }
  }

  closeDialog() {
    if (this.tthChangeStatusFormGroup.dirty) {
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
