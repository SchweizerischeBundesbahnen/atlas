import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TthDialogData } from './tthdialog.data';
import { HearingStatus, TimetableHearingService, TimetableHearingYear } from '../../../api';
import moment from 'moment/moment';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../validation/field-lengths/atlas-field-length-validator';
import { AtlasCharsetsValidator } from '../../validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../validation/date-range/date-range-validator';
import { ValidationService } from '../../validation/validation.service';
import { TimetablehearingFormGroup } from './tthformgroup';

@Component({
  selector: 'app-tthdialog',
  templateUrl: './tthdialog.component.html',
  styleUrls: ['tthdialog.component.scss'],
})
export class TthDialogComponent implements OnInit {
  form: FormGroup<TimetablehearingFormGroup> = new FormGroup(
    {
      timetableYear: new FormControl(2000, [
        Validators.required,
        AtlasFieldLengthValidator.length_50,
        AtlasCharsetsValidator.sid4pt,
      ]),
      validFrom: new FormControl(moment(), [Validators.required]),
      validTo: new FormControl(moment(), [Validators.required]),
    },
    [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
  );
  YEAR_OPTIONS: number[] = [];
  defaultYearSelection = this.YEAR_OPTIONS[0];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: TthDialogData,
    private readonly timetableHearingService: TimetableHearingService
  ) {}

  ngOnInit(): void {
    this.initOverviewPlannedTable();
  }

  private initOverviewPlannedTable() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Active, HearingStatus.Planned])
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.objects) {
          const activeYear = this.getActiveYear(timetableHearingYears.objects);
          const plannedYears = this.getPlannedYears(timetableHearingYears.objects);
          this.YEAR_OPTIONS = this.calculateProposedYears(activeYear, plannedYears);
          this.defaultYearSelection = this.YEAR_OPTIONS[0];
        }
      });
  }

  private getActiveYear(timetableHearingYears: Array<TimetableHearingYear>): number {
    const timetableHearingYear = timetableHearingYears.find(function (thy) {
      return thy.hearingStatus === HearingStatus.Active;
    });
    if (timetableHearingYear === undefined) {
      return 0;
    }
    return timetableHearingYear.timetableYear;
  }

  private getPlannedYears(
    timetableHearingYears: Array<TimetableHearingYear>
  ): Array<TimetableHearingYear> {
    const plannedYears: TimetableHearingYear[] = [];
    for (const i in timetableHearingYears) {
      if (HearingStatus.Planned === timetableHearingYears[i].hearingStatus) {
        plannedYears.push(timetableHearingYears[i]);
      }
    }
    return plannedYears;
  }

  private calculateProposedYears(
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

  private isYearAlreadyPlanned(
    activeYear: number,
    timetableHearingYears: Array<TimetableHearingYear>
  ): boolean {
    const years: TimetableHearingYear[] = [];
    for (const i in timetableHearingYears) {
      if (activeYear === timetableHearingYears[i].timetableYear) {
        years.push(timetableHearingYears[i]);
      }
    }
    return years.length > 0;
  }

  createTth() {
    const timetableHearingYear: TimetableHearingYear = {
      timetableYear: Number(this.form.controls['timetableYear'].value),
      hearingFrom: this.form.controls['validFrom'].value?.toDate()
        ? this.form.controls['validFrom'].value?.toDate()
        : moment().toDate(),
      hearingTo: this.form.controls['validTo'].value?.toDate()
        ? this.form.controls['validTo'].value?.toDate()
        : moment().toDate(),
    };
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      this.timetableHearingService.createHearingYear(timetableHearingYear).subscribe((res) => {
        console.log(res);
      });
    }
  }
}
