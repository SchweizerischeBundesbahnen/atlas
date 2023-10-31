import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NewTimetableHearingYearDialogData } from './model/new-timetable-hearing-year-dialog.data';
import { HearingStatus, TimetableHearingYear, TimetableHearingYearsService } from '../../../api';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import { ValidationService } from '../../../core/validation/validation.service';
import { NewTimetableHearingYearFormGroup } from './model/new-timetable-hearing-year-form-group';
import { NotificationService } from '../../../core/notification/notification.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Moment } from 'moment';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NewHearingYearValidator } from './new-hearing-year-validator';
import { TthUtils } from '../util/tth-utils';

@Component({
  selector: 'app-tthdialog',
  templateUrl: './new-timetable-hearing-year-dialog.component.html',
})
export class NewTimetableHearingYearDialogComponent implements OnInit {
  form: FormGroup = new FormGroup<NewTimetableHearingYearFormGroup>(
    {
      timetableYear: new FormControl(2000, [
        Validators.required,
        AtlasFieldLengthValidator.length_50,
        AtlasCharsetsValidator.sid4pt,
      ]),
      hearingFrom: new FormControl<Moment | null>(null, [Validators.required]),
      hearingTo: new FormControl<Moment | null>(null, [Validators.required]),
    },
    [
      DateRangeValidator.fromGreaterThenTo('hearingFrom', 'hearingTo'),
      NewHearingYearValidator.fromAndToOneYearBefore('timetableYear', 'hearingFrom', 'hearingTo'),
    ],
  );
  YEAR_OPTIONS: number[] = [];
  defaultYearSelection = this.YEAR_OPTIONS[0];

  private readonly ngUnsubscribe = new Subject<void>();

  constructor(
    private dialogRef: MatDialogRef<NewTimetableHearingYearDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: NewTimetableHearingYearDialogData,
    private readonly timetableHearingYearsService: TimetableHearingYearsService,
    protected notificationService: NotificationService,
    private readonly dialogService: DialogService,
  ) {}

  ngOnInit(): void {
    this.initOverviewOfferedYears();
  }

  initOverviewOfferedYears() {
    this.timetableHearingYearsService
      .getHearingYears([HearingStatus.Active, HearingStatus.Planned, HearingStatus.Archived])
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((timetableHearingYears) => {
        const sortedTimetableHearingYears = TthUtils.sortByTimetableHearingYear(
          timetableHearingYears,
          false,
        );
        if (sortedTimetableHearingYears) {
          const activeYear = this.getActiveYear(sortedTimetableHearingYears);
          const plannedAndArchivedYears = this.getAllPlanedAndArchivedYears(
            sortedTimetableHearingYears,
          );
          this.YEAR_OPTIONS = this.calculateProposedYears(activeYear, plannedAndArchivedYears);
          this.defaultYearSelection = this.YEAR_OPTIONS[0];
        }
      });
  }

  createNewTimetableHearingYear() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const timetableHearingYear = this.form.value as TimetableHearingYear;
      this.timetableHearingYearsService
        .createHearingYear(timetableHearingYear)
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(() => {
          this.notificationService.success('TTH.NEW_YEAR.DIALOG.NOTIFICATION_SUCCESS');
          this.dialogRef.close(true);
        });
    }
  }

  getActiveYear(timetableHearingYears: Array<TimetableHearingYear>): number {
    const timetableHearingYear = timetableHearingYears.find((thy) => {
      return thy.hearingStatus === HearingStatus.Active;
    });
    if (!timetableHearingYear) {
      return new Date().getFullYear();
    }
    return timetableHearingYear.timetableYear;
  }

  getAllPlanedAndArchivedYears(
    timetableHearingYears: Array<TimetableHearingYear>,
  ): Array<TimetableHearingYear> {
    return timetableHearingYears.filter(
      (year) =>
        year.hearingStatus === HearingStatus.Planned ||
        year.hearingStatus === HearingStatus.Archived,
    );
  }

  calculateProposedYears(
    activeYear: number,
    plannedAndArchivedHearingYears: Array<TimetableHearingYear>,
  ): Array<number> {
    const proposedYears: number[] = [];
    let counter = 1;
    while (proposedYears.length < 5) {
      const proposedYear = activeYear + counter;
      if (!this.isYearAlreadyPlannedOrArchived(proposedYear, plannedAndArchivedHearingYears)) {
        proposedYears.push(proposedYear);
      }
      counter++;
    }
    return proposedYears;
  }

  isYearAlreadyPlannedOrArchived(
    proposedYear: number,
    timetableHearingYears: Array<TimetableHearingYear>,
  ): boolean {
    return timetableHearingYears.filter((year) => year.timetableYear === proposedYear).length > 0;
  }

  closeDialog() {
    this.dialogService.confirmLeave().subscribe((confirm) => {
      if (confirm) {
        this.dialogRef.close();
      }
    });
  }
}
