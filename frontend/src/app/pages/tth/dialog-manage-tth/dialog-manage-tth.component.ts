import { Component, Inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
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

  @ViewChild('loadingView', { static: true }) loadingView!: TemplateRef<this>;
  @ViewChild('manageView', { static: true }) manageView!: TemplateRef<this>;
  @ViewChild('closeTimetableHearingView', { static: true })
  closeTimetableHearingView!: TemplateRef<this>;

  currentView: TemplateRef<this> | null = null;
  actionButtonsDisabled = false;

  private readonly year: number;
  private timetableHearingYear?: TimetableHearingYear;

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
    this.currentView = this.loadingView;
    this.tthService
      .getHearingYear(this.year)
      .pipe(take(1))
      .subscribe({
        next: (year) => {
          this.timetableHearingYear = year;
          this.manageTthFormGroup.setValue({
            statementCreatableExternal: !!year.statementCreatableExternal,
            statementCreatableInternal: !!year.statementCreatableInternal,
            statementEditable: !!year.statementEditable,
          });
          this.currentView = this.manageView;
        },
        error: (err) => {
          this.dialogRef.close();
          this.notificationService.error(err);
        },
      });
  }

  handleManageViewCancelClick(): void {
    this.dialogRef.close();
  }

  handleSaveAndCloseClick(): void {
    if (!this.timetableHearingYear) {
      throw 'TimetableHearingYear should be defined here';
    }

    this.actionButtonsDisabled = true;

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
          this.notificationService.success('USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS');
        },
        error: (err) => {
          this.dialogRef.close();
          this.notificationService.error(err);
        },
      });
  }

  handleManageViewTthCloseClick(): void {
    this.currentView = this.closeTimetableHearingView;
  }

  handleCloseViewCancelClick(): void {
    this.currentView = this.manageView;
  }

  handleCloseViewTthCloseClick(): void {
    this.actionButtonsDisabled = true;
    this.tthService
      .closeTimetableHearing(this.year)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.dialogRef.close(true);
          this.notificationService.success(
            'TTH.CLOSE_TIMETABLE_HEARING.SUCCESSFUL_CLOSE_NOTIFICATION'
          );
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
