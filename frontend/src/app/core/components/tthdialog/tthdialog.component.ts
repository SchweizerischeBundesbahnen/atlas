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
export class TthDialogComponent {
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

  createTth() {
    const timetableHearingYear: TimetableHearingYear = {
      timetableYear: Number(this.form.controls['timetableYear'].value),
      // hearingFrom: this.form.controls['validFrom'].value ? this.form.controls['validFrom'].value : moment().to(),
      // hearingTo: this.form.controls['validTo'].value ? this.form.controls['validTo'].value : moment().to(),
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
    // const formData: any = new FormData();
    // const validFrom = this.form.controls['validFrom'].value;
    // const validTo = this.form.controls['validTo'].value;
    // const timetableYear = this.form.controls['timetableYear'].value;
    // // formData.append('timetableYear', this.form.get('name').value);
    // formData.append('validFrom', this.form.get('validFrom').value);
    // formData.append('validTo', this.form.get('validTo').value);
  }
}
