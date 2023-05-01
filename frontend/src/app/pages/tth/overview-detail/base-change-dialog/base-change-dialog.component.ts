import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StatusChangeData } from '../tth-change-status-dialog/model/status-change-data';
import { NotificationService } from '../../../../core/notification/notification.service';
import { TimetableHearingService } from '../../../../api';
import { DialogService } from '../../../../core/components/dialog/dialog.service';

@Component({
  selector: 'app-base-change-dialog',
  templateUrl: './base-change-dialog.component.html',
  styleUrls: ['./base-change-dialog.component.scss'],
})
export class BaseChangeDialogComponent implements OnInit {
  @Input() formGroup!: FormGroup;
  @Input() controlName!: string;
  @Input() maxChars = '5000';
  @Output() changeEvent = new EventEmitter();

  constructor(
    public dialogRef: MatDialogRef<BaseChangeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StatusChangeData,
    private readonly notificationService: NotificationService,
    private readonly timetableHearingService: TimetableHearingService,
    private readonly dialogService: DialogService
  ) {}

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

  ngOnInit(): void {}
}
