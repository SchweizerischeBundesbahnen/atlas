import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TthDialogData } from './tthdialog.data';
import {
  BusinessType,
  HearingStatus,
  TimetableHearingService,
  TimetableHearingYear,
} from '../../../api';
import moment from 'moment/moment';
import { FormControl, FormGroup } from '@angular/forms';
import { Moment } from 'moment';

@Component({
  selector: 'app-tthdialog',
  templateUrl: './tthdialog.component.html',
  styleUrls: ['tthdialog.component.scss'],
})
export class TthDialogComponent {
  form: any = new FormGroup({
    validFrom: new FormControl<Moment | null>(null),
    validTo: new FormControl<Moment | null>(null),
  });
  BUSINESS_TYPES = Object.values(BusinessType);
  YEAR_OPTIONS: number[] = [];
  defaultYearSelection = this.YEAR_OPTIONS[0];
  foundTimetableHearingYear: TimetableHearingYear = {
    timetableYear: 2000,
    hearingFrom: moment().toDate(),
    hearingTo: moment().toDate(),
  };

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: TthDialogData,
    private readonly timetableHearingService: TimetableHearingService
  ) {}

  ngOnInit(): void {
    this.initOverviewPlannedTable();
  }

  // private initOverviewPlannedTable() {
  //   this.timetableHearingService
  //     .getHearingYears([HearingStatus.Planned])
  //     .subscribe((plannedTimetableHearingYears) => {
  //       if (plannedTimetableHearingYears.objects) {
  //           plannedTimetableHearingYears.objects.sort((n1, n2) => n1.timetableYear - n2.timetableYear)
  //       }
  //     });
  // }

  private initOverviewPlannedTable() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Planned])
      .subscribe((plannedTimetableHearingYears) => {
        if (plannedTimetableHearingYears.objects) {
          plannedTimetableHearingYears.objects.sort(
            (n1, n2) => n1.timetableYear - n2.timetableYear
          );
          this.YEAR_OPTIONS = plannedTimetableHearingYears.objects
            .map((value) => value.timetableYear)
            .slice(0, 5);
          this.defaultYearSelection = this.YEAR_OPTIONS[0];
          this.foundTimetableHearingYear = plannedTimetableHearingYears.objects[0];
        }
      });
  }
}
