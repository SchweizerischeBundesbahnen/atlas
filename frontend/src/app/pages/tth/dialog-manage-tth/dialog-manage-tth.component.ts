import { Component, Inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { TimetableHearingYear, TimetableHearingYearsService } from '../../../api';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { take } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';

@Component({
  selector: 'dialog-manage-tth',
  templateUrl: './dialog-manage-tth.component.html',
  styleUrls: ['./dialog-manage-tth.component.scss'],
})
export class DialogManageTthComponent implements OnInit {
  @ViewChild('loadingView', { static: true }) loadingView!: TemplateRef<this>;
  @ViewChild('manageView', { static: true }) manageView!: TemplateRef<this>;
  @ViewChild('closeTimetableHearingView', { static: true })
  closeTimetableHearingView!: TemplateRef<this>;

  statementCreatableExternalSliderValue = false;
  statementCreatableInternalSliderValue = false;
  statementEditableSliderValue = false;

  timetableHearingYear?: TimetableHearingYear;
  currentView: TemplateRef<this> | null = null;
  actionButtonsDisabled = false;

  private readonly year: number;

  constructor(
    @Inject(MAT_DIALOG_DATA) private readonly matDialogData: number,
    private readonly timetableHearingYearsService: TimetableHearingYearsService,
    private readonly notificationService: NotificationService,
    private readonly dialogRef: MatDialogRef<DialogManageTthComponent, boolean>,
  ) {
    this.year = matDialogData;
  }

  ngOnInit() {
    this.currentView = this.loadingView;
    this.timetableHearingYearsService
      .getHearingYear(this.year)
      .pipe(take(1))
      .subscribe({
        next: (year) => {
          this.timetableHearingYear = year;
          [
            this.statementCreatableExternalSliderValue,
            this.statementCreatableInternalSliderValue,
            this.statementEditableSliderValue,
          ] = [
            !!year.statementCreatableExternal,
            !!year.statementCreatableInternal,
            !!year.statementEditable,
          ];
          this.currentView = this.manageView;
        },
        error: (err) => {
          this.dialogRef.close(true);
          this.notificationService.error(err);
        },
      });
  }

  handleManageViewCancelClick(): void {
    this.dialogRef.close();
  }

  handleSaveAndCloseClick(): void {
    if (!this.timetableHearingYear) {
      throw new Error('TimetableHearingYear should be defined here');
    }

    this.actionButtonsDisabled = true;

    [
      this.timetableHearingYear.statementCreatableExternal,
      this.timetableHearingYear.statementCreatableInternal,
      this.timetableHearingYear.statementEditable,
    ] = [
      this.statementCreatableExternalSliderValue,
      this.statementCreatableInternalSliderValue,
      this.statementEditableSliderValue,
    ];

    this.timetableHearingYearsService
      .updateTimetableHearingSettings(this.year, this.timetableHearingYear)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.dialogRef.close(true);
          this.notificationService.success(
            'TTH.MANAGE_TIMETABLE_HEARING.SUCCESSFUL_SAVE_NOTIFICATION',
          );
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
    this.timetableHearingYearsService
      .closeTimetableHearing(this.year)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.dialogRef.close(true);
          this.notificationService.success(
            'TTH.CLOSE_TIMETABLE_HEARING.SUCCESSFUL_CLOSE_NOTIFICATION',
          );
        },
        error: (err) => {
          this.dialogRef.close();
          this.notificationService.error(err);
        },
      });
  }
}
