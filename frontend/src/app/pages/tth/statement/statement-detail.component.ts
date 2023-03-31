import { Component, OnInit } from '@angular/core';
import {
  HearingStatus,
  LineVersion,
  StatementStatus,
  SwissCanton,
  TimetableHearingService,
  TimetableHearingStatement,
} from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { MatSelectChange } from '@angular/material/select';
import { Cantons } from '../overview/canton/Cantons';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { LineDetailFormGroup } from '../../lidi/lines/detail/line-detail-form-group';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import moment from 'moment/moment';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import { StatementDetailFormGroup, StatementSenderFormGroup } from './statement-detail-form-group';
import { Canton } from '../overview/canton/Canton';
import { Statement } from '@angular/compiler';

@Component({
  selector: 'app-statement-detail',
  templateUrl: './statement-detail.component.html',
  styleUrls: ['./statement-detail.component.scss'],
})
export class StatementDetailComponent implements OnInit {
  YEAR_OPTIONS: number[] = [];
  CANTON_OPTIONS: Canton[] = [];
  STATUS_OPTIONS: StatementStatus[] = [];

  statement: TimetableHearingStatement | undefined;

  form!: FormGroup<StatementDetailFormGroup>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private dialogService: DialogService,
    private timetableHearingService: TimetableHearingService
  ) {}

  ngOnInit() {
    this.statement = this.activatedRoute.snapshot.data.statement;

    this.form = this.getFormGroup(this.statement);
    this.initYearOptions();
    this.initCantonOptions();
    this.initStatusOptions(this.statement);
  }

  private initYearOptions() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Active, HearingStatus.Planned])
      .subscribe((yearContainer) => {
        const years = yearContainer.objects
          ?.map((year) => year.timetableYear)
          .sort((n1, n2) => n1 - n2);
        this.YEAR_OPTIONS = years!;
        this.form.controls.timetableYear.setValue(this.YEAR_OPTIONS[0]);
      });
  }

  initCantonOptions() {
    // TODO: get only cantons available for writer
    this.CANTON_OPTIONS = Cantons.cantons;
  }

  getFormGroup(statement: TimetableHearingStatement | undefined): FormGroup {
    return new FormGroup<StatementDetailFormGroup>({
      timetableYear: new FormControl(statement?.timetableYear, [Validators.required]),
      statementStatus: new FormControl(statement?.statementStatus, [Validators.required]),
      ttfnid: new FormControl(statement?.ttfnid),
      swissCanton: new FormControl(statement?.swissCanton),
      stopPlace: new FormControl(statement?.stopPlace, [
        AtlasFieldLengthValidator.length_50,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      statementSender: new FormGroup<StatementSenderFormGroup>({
        firstName: new FormControl(statement?.statementSender.firstName, [
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.iso88591,
        ]),
        lastName: new FormControl(statement?.statementSender.lastName, [
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.iso88591,
        ]),
        email: new FormControl(statement?.statementSender.email, [
          Validators.required,
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.email,
        ]),
      }),
      statement: new FormControl(statement?.statement, [
        Validators.required,
        AtlasFieldLengthValidator.comments,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      justification: new FormControl(statement?.justification, [
        AtlasFieldLengthValidator.comments,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      etagVersion: new FormControl(statement?.etagVersion),
    });
  }

  private initStatusOptions(statement: TimetableHearingStatement | undefined) {
    this.STATUS_OPTIONS = Object.values(StatementStatus);
    if (!statement) {
      this.form.controls.statementStatus.setValue(StatementStatus.Received);
      this.form.controls.statementStatus.disable();
    }
  }
}
