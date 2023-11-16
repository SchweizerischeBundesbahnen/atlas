import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StatusChangeData } from '../tth-change-status-dialog/model/status-change-data';
import { DialogService } from '../../../../core/components/dialog/dialog.service';

@Component({
  selector: 'app-base-change-dialog',
  templateUrl: './base-change-dialog.component.html',
  styleUrls: ['./base-change-dialog.component.scss'],
})
export class BaseChangeDialogComponent {
  @Input() formGroup!: FormGroup;
  @Input() controlName!: string;
  @Input() maxChars!: string;
  @Output() changeEvent = new EventEmitter();
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @Input() dialogRef!: MatDialogRef<any>;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: StatusChangeData,
    private readonly dialogService: DialogService,
  ) {}

  closeDialog() {
    if (this.formGroup.dirty) {
      this.dialogService.confirmLeave().subscribe((confirm) => {
        if (confirm) {
          this.dialogRef.close(false);
        }
      });
    } else {
      this.dialogRef.close(false);
    }
  }
}
