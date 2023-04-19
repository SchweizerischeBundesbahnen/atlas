import { Component, Inject, OnInit } from '@angular/core';
import { TimetableHearingService, TimetableHearingYear } from '../../../api';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { take } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { NotificationService } from '../../../core/notification/notification.service';

@Component({
  selector: 'dialog-manage-tth',
  templateUrl: './dialog-manage-tth.component.html',
  styleUrls: ['./dialog-manage-tth.component.scss'],
})
export class DialogManageTthComponent implements OnInit {
  readonly statementCreatableExternalCtrlName = 'statementCreatableExternal';
  readonly statementCreatableInternalCtrlName = 'statementCreatableInternal';
  readonly statementEditableCtrlName = 'statementEditable';

  private _showManageView = true;

  get showManageView(): boolean {
    return this._showManageView;
  }

  private readonly year: number;
  private readonly timetableHearingYear?: TimetableHearingYear;

  private readonly manageTthFormGroup: FormGroup = new FormGroup({
    [this.statementCreatableExternalCtrlName]: new FormControl<boolean>(false),
    [this.statementCreatableInternalCtrlName]: new FormControl<boolean>(false),
    [this.statementEditableCtrlName]: new FormControl<boolean>(false),
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) private readonly matDialogData: number,
    private readonly tthService: TimetableHearingService,
    private readonly notificationService: NotificationService,
    private readonly dialogRef: MatDialogRef<DialogManageTthComponent, boolean>
  ) {
    this.year = matDialogData;
  }

  ngOnInit() {
    console.log(this.year);
    this.tthService
      .getHearingYear(this.year)
      .pipe(take(1))
      .subscribe({
        next: (year) => {
          (this.timetableHearingYear as TimetableHearingYear | undefined) = year;
          this.manageTthFormGroup.setValue({
            statementCreatableExternal: !!year.statementCreatableExternal,
            statementCreatableInternal: !!year.statementCreatableInternal,
            statementEditable: !!year.statementEditable,
          });
        },
        error: (err) => {
          // close dialog and error notification
          this.dialogRef.close();
          this.notificationService.error(err);
        },
      });
  }

  handleSaveAndCloseClick(): void {
    if (!this.timetableHearingYear) {
      throw 'TimetableHearingYear should be defined here';
    }
    // send request, show notification and close dialog
    // todo: mby show confirmation dialog before save and close

    // update object
    this.timetableHearingYear.statementCreatableExternal =
      this.manageTthFormGroup.value[this.statementCreatableExternalCtrlName];
    this.timetableHearingYear.statementCreatableInternal =
      this.manageTthFormGroup.value[this.statementCreatableInternalCtrlName];
    this.timetableHearingYear.statementEditable =
      this.manageTthFormGroup.value[this.statementEditableCtrlName];

    this.tthService
      .updateTimetableHearingSettings(this.year, this.timetableHearingYear)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.dialogRef.close();
          this.notificationService.success('test'); // todo
        },
        error: (err) => {
          this.dialogRef.close();
          this.notificationService.error(err);
        },
      });
  }

  handleManageViewTthCloseClick(): void {
    // confirmation dialog => send update year request, on success => notification and step over,
    // on error notification and close dialog // todo: ask for specification

    console.log(this.manageTthFormGroup);
    this._showManageView = false;
  }

  handleCancelClick(): void {
    this._showManageView = true;
  }

  handleCloseViewTthCloseClick(): void {
    // send close request, on success => close dialog and success notificiation
    // on error => close dialog and error notification

    this.tthService
      .closeTimetableHearing(this.year)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.dialogRef.close(true);
          this.notificationService.success('worked'); // todo
        },
        error: (err) => {
          this.dialogRef.close();
          this.notificationService.error(err);
        },
      });
  }

  getFormCtrlValueOf(ctrlName: string): boolean {
    return this.manageTthFormGroup.value[ctrlName];
  }

  setFormCtrlValueOf(ctrlName: string, value: boolean): void {
    this.manageTthFormGroup.patchValue({
      [ctrlName]: value,
    });
    this.manageTthFormGroup.controls[ctrlName].markAsDirty();
  }
}
