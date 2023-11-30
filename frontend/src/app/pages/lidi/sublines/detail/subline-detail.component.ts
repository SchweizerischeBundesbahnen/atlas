import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  ApplicationType,
  Line,
  LinesService,
  PaymentType,
  SublinesService,
  SublineType,
  SublineVersion,
} from '../../../../api';
import { BaseDetailController } from '../../../../core/components/base-detail/base-detail-controller';
import { catchError, Observable, of, Subject, takeUntil } from 'rxjs';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Page } from '../../../../core/model/page';
import { Pages } from '../../../pages';
import moment from 'moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { map } from 'rxjs/operators';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { ValidationService } from '../../../../core/validation/validation.service';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { SublineDetailFormGroup } from './subline-detail-form-group';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  templateUrl: './subline-detail.component.html',
  styleUrls: ['./subline-detail.component.scss'],
})
export class SublineDetailComponent
  extends BaseDetailController<SublineVersion>
  implements OnInit, OnDestroy
{
  TYPE_OPTIONS = Object.values(SublineType);
  PAYMENT_TYPE_OPTIONS = Object.values(PaymentType);

  private ngUnsubscribe = new Subject<void>();
  mainlines$: Observable<Line[]> = of([]);

  readonly mainlineSlnidFormControlName = 'mainlineSlnid';

  constructor(
    protected router: Router,
    private sublinesService: SublinesService,
    protected notificationService: NotificationService,
    private validationService: ValidationService,
    private linesService: LinesService,
    protected dialogService: DialogService,
    protected authService: AuthService,
    protected activatedRoute: ActivatedRoute,
  ) {
    super(router, dialogService, notificationService, authService, activatedRoute);
  }

  ngOnInit() {
    super.ngOnInit();
    if (this.isExistingRecord()) {
      this.mainlines$ = this.linesService
        .getLine(this.record.mainlineSlnid)
        .pipe(map((value) => [value]));
    }
  }

  getPageType(): Page {
    return Pages.SUBLINES;
  }

  getApplicationType(): ApplicationType {
    return ApplicationType.Lidi;
  }

  readRecord(): SublineVersion {
    return this.activatedRoute.snapshot.data.sublineDetail;
  }

  getDetailHeading(record: SublineVersion): string {
    return `${record.number ?? ''} - ${record.description ?? ''}`;
  }

  getDetailSubheading(record: SublineVersion): string {
    return record.slnid!;
  }

  updateRecord(): void {
    this.sublinesService
      .updateSublineVersion(this.getId(), this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, this.record.slnid])
          .then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.sublinesService
      .createSublineVersion(this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe((version) => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  revokeRecord(): void {
    const selectedRecord = this.getSelectedRecord();
    if (selectedRecord.slnid) {
      this.sublinesService.revokeSubline(selectedRecord.slnid).subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.REVOKE_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, selectedRecord.slnid])
          .then(() => this.ngOnInit());
      });
    }
  }

  deleteRecord(): void {
    const selectedSublineVersion = this.getSelectedRecord();
    if (selectedSublineVersion.slnid != null) {
      this.sublinesService.deleteSublines(selectedSublineVersion.slnid).subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
    }
  }

  getFormGroup(version: SublineVersion): FormGroup {
    return new FormGroup<SublineDetailFormGroup>(
      {
        swissSublineNumber: new FormControl(version.swissSublineNumber, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          AtlasCharsetsValidator.sid4pt,
        ]),
        [this.mainlineSlnidFormControlName]: new FormControl(version.mainlineSlnid, [
          Validators.required,
        ]),
        slnid: new FormControl(version.slnid),
        status: new FormControl(version.status),
        sublineType: new FormControl(version.sublineType, [Validators.required]),
        paymentType: new FormControl(version.paymentType, [Validators.required]),
        businessOrganisation: new FormControl(version.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        number: new FormControl(version.number, [
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        longName: new FormControl(version.longName, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        description: new FormControl(version.description, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ),
        validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo, [
          Validators.required,
        ]),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  getValidation(inputForm: string) {
    return this.validationService.getValidation(this.form?.controls[inputForm]?.errors);
  }

  getFormControlsToDisable(): string[] {
    return [this.mainlineSlnidFormControlName];
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.unsubscribe();
  }

  searchMainlines(searchString: string) {
    this.mainlines$ = this.linesService
      .getLines(searchString, [], [], [], undefined, undefined, undefined, undefined, [
        'swissLineNumber,ASC',
      ])
      .pipe(map((value) => value.objects ?? []));
  }

  mainlineUrl(): string {
    return `${location.origin}/${Pages.LIDI.path}/${Pages.LINES.path}/${this.form.get(
      this.mainlineSlnidFormControlName,
    )?.value}`;
  }
}
