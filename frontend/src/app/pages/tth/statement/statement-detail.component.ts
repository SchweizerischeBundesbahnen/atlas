import { Component, OnInit } from '@angular/core';
import {
  HearingStatus,
  StatementStatus,
  TimetableHearingService,
  TimetableHearingStatement,
} from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Cantons } from '../overview/canton/Cantons';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import { StatementDetailFormGroup, StatementSenderFormGroup } from './statement-detail-form-group';
import { Canton } from '../overview/canton/Canton';
import { takeUntil } from 'rxjs/operators';
import { catchError, EMPTY, Subject } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { ValidationService } from '../../../core/validation/validation.service';

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
  isNew!: boolean;

  form!: FormGroup<StatementDetailFormGroup>;
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private timetableHearingService: TimetableHearingService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.statement = this.route.snapshot.data.statement;
    this.isNew = !this.statement;

    this.form = this.getFormGroup(this.statement);
    this.initYearOptions();
    this.initCantonOptions();
    this.initStatusOptions();
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
      responsibleTransportCompanies: new FormControl(statement?.responsibleTransportCompanies),
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
        organisation: new FormControl(statement?.statementSender.organisation, [
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.iso88591,
        ]),
        zip: new FormControl(statement?.statementSender.zip, [
          AtlasCharsetsValidator.numeric,
          Validators.min(1000),
          Validators.max(99999),
        ]),
        city: new FormControl(statement?.statementSender.city, [
          AtlasFieldLengthValidator.length_50,
          AtlasCharsetsValidator.iso88591,
        ]),
        street: new FormControl(statement?.statementSender.street, [
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
        AtlasFieldLengthValidator.statement,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      justification: new FormControl(statement?.justification, [
        AtlasFieldLengthValidator.statement,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      etagVersion: new FormControl(statement?.etagVersion),
    });
  }

  private initStatusOptions() {
    this.STATUS_OPTIONS = Object.values(StatementStatus);
    if (this.isNew) {
      this.form.controls.statementStatus.setValue(StatementStatus.Received);
      this.form.controls.statementStatus.disable();
    }
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      this.form.disable();
      const hearingStatement = this.form.value as TimetableHearingStatement;
      if (this.isNew) {
        this.createStatement(hearingStatement);
      } else {
        this.updateStatement(hearingStatement);
      }
    }
  }

  private createStatement(statement: TimetableHearingStatement) {
    this.timetableHearingService
      .createStatement(statement, undefined)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((statement) => {
        this.notificationService.success('TTH.STATEMENT.NOTIFICATION.ADD_SUCCESS');
        this.navigateToStatementDetail(statement);
      });
  }

  private updateStatement(statement: TimetableHearingStatement) {
    this.timetableHearingService
      .updateHearingStatement(statement.id!, statement, undefined)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((statement) => {
        this.notificationService.success('TTH.STATEMENT.NOTIFICATION.EDIT_SUCCESS');
        this.navigateToStatementDetail(statement);
      });
  }

  private navigateToStatementDetail(statement: TimetableHearingStatement) {
    this.router
      .navigate(['..', statement.id], {
        relativeTo: this.route,
      })
      .then(() => this.ngOnInit());
  }

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }
}
