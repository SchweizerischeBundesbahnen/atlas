import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NewTimetableHearingYearDialogData } from './model/new-timetable-hearing-year-dialog.data';
import { HearingStatus, TimetableHearingService, TimetableHearingYear } from '../../../api';
import moment from 'moment/moment';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import { ValidationService } from '../../../core/validation/validation.service';
import { NewTimetableHearingYearFormGroup } from './model/new-timetable-hearing-year-form-group';
import { NotificationService } from '../../../core/notification/notification.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { NewTimetableHearingYearDialogService } from './service/new-timetable-hearing-year-dialog.service';
import { Moment } from 'moment';
import { take } from 'rxjs';

@Component({
  selector: 'app-tthdialog',
  templateUrl: './new-timetable-hearing-year-dialog.component.html',
  styleUrls: ['new-timetable-hearing-year-dialog.component.scss'],
})
export class NewTimetableHearingYearDialogComponent implements OnInit {
  form: FormGroup<NewTimetableHearingYearFormGroup> = new FormGroup(
    {
      timetableYear: new FormControl(2000, [
        Validators.required,
        AtlasFieldLengthValidator.length_50,
        AtlasCharsetsValidator.sid4pt,
      ]),
      validFrom: new FormControl<Moment | null>(null, [Validators.required]),
      validTo: new FormControl<Moment | null>(null, [Validators.required]),
    },
    [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
  );
  YEAR_OPTIONS: number[] = [];
  defaultYearSelection = this.YEAR_OPTIONS[0];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: NewTimetableHearingYearDialogData,
    private readonly timetableHearingService: TimetableHearingService,
    protected notificationService: NotificationService,
    private readonly dialogService: DialogService,
    private readonly newTimetableHearingYearDialogService: NewTimetableHearingYearDialogService
  ) {}

  ngOnInit(): void {
    this.initOverviewOfferedYears();
  }

  initOverviewOfferedYears() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Active, HearingStatus.Planned])
      .pipe(take(1))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.objects) {
          const activeYear = this.getActiveYear(timetableHearingYears.objects);
          const plannedYears = this.getPlannedYears(timetableHearingYears.objects);
          this.YEAR_OPTIONS = this.calculateProposedYears(activeYear, plannedYears);
          this.defaultYearSelection = this.YEAR_OPTIONS[0];
        }
      });
  }

  createNewTimetableHearingYear() {
    const hearingFromDate = this.form.controls['validFrom'].value?.toDate();
    const hearingToDate = this.form.controls['validTo'].value?.toDate();
    const timetableHearingYear: TimetableHearingYear = {
      timetableYear: Number(this.form.controls['timetableYear'].value),
      hearingFrom: hearingFromDate ?? moment().toDate(),
      hearingTo: hearingToDate ?? moment().toDate(),
    };
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      this.timetableHearingService.createHearingYear(timetableHearingYear).subscribe(() => {
        this.notificationService.success('TTH.DIALOG.NOTIFICATION_SUCCESS');
        this.newTimetableHearingYearDialogService.closeConfirmDialog();
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

  getPlannedYears(timetableHearingYears: Array<TimetableHearingYear>): Array<TimetableHearingYear> {
    const plannedYears: TimetableHearingYear[] = [];
    for (const i in timetableHearingYears) {
      if (HearingStatus.Planned === timetableHearingYears[i].hearingStatus) {
        plannedYears.push(timetableHearingYears[i]);
      }
    }
    return plannedYears;
  }

  calculateProposedYears(
    activeYear: number,
    timetableHearingYears: Array<TimetableHearingYear>
  ): Array<number> {
    const proposedYears: number[] = [];
    let counter = 1;
    while (proposedYears.length < 5) {
      const proposedYear = activeYear + counter;
      if (!this.isYearAlreadyPlanned(proposedYear, timetableHearingYears)) {
        proposedYears.push(proposedYear);
      }
      counter++;
    }
    return proposedYears;
  }

  isYearAlreadyPlanned(
    proposedYear: number,
    timetableHearingYears: Array<TimetableHearingYear>
  ): boolean {
    return timetableHearingYears.filter((year) => year.timetableYear === proposedYear).length > 0;
  }

  closeDialog() {
    this.dialogService.confirmLeave().subscribe((confirm) => {
      if (confirm) {
        this.newTimetableHearingYearDialogService.closeConfirmDialog();
      }
    });
  }
}
