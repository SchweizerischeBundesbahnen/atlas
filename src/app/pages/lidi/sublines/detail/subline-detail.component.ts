import { Component, OnDestroy, OnInit } from '@angular/core';
import { Line, LinesService, LineVersion, SublinesService, SublineVersion } from '../../../../api';
import {
  DateService,
  MAX_DATE,
  MAX_DATE_FORMATTED,
  MIN_DATE,
} from 'src/app/core/date/date.service';
import { DetailWrapperController } from '../../../../core/components/detail-wrapper/detail-wrapper-controller';
import { catchError, distinctUntilChanged, EMPTY, Subject, takeUntil } from 'rxjs';
import { ValidationService } from '../../../../core/validation/validation.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Page } from '../../../../core/model/page';
import { Pages } from '../../../pages';
import moment from 'moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { ValidationError } from '../../../../core/validation/validation-error';
import { switchMap } from 'rxjs/operators';

@Component({
  templateUrl: './subline-detail.component.html',
  styleUrls: ['./subline-detail.component.scss'],
})
export class SublineDetailComponent
  extends DetailWrapperController<SublineVersion>
  implements OnInit, OnDestroy
{
  TYPE_OPTIONS = Object.values(SublineVersion.TypeEnum);
  PAYMENT_TYPE_OPTIONS = Object.values(LineVersion.PaymentTypeEnum);
  STATUS_OPTIONS = Object.values(LineVersion.StatusEnum);
  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;
  VALID_TO_PLACEHOLDER = MAX_DATE_FORMATTED;

  private ngUnsubscribe = new Subject<void>();
  mainlineSearchTerm = new Subject<string>();
  mainlines: Line[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private sublinesService: SublinesService,
    private formBuilder: FormBuilder,
    protected notificationService: NotificationService,
    protected dialogService: DialogService,
    private validationService: ValidationService,
    private dateService: DateService,
    private linesService: LinesService
  ) {
    super(dialogService, notificationService);
  }

  ngOnInit() {
    super.ngOnInit();
    if (this.isExistingRecord()) {
      this.linesService
        .getLine(this.record.mainlineSlnid)
        .subscribe((line) => (this.mainlines = [line]));
    }
    this.initMainlineSearch();
  }

  getPageType(): Page {
    return Pages.SUBLINES;
  }

  readRecord(): SublineVersion {
    return this.activatedRoute.snapshot.data.sublineDetail;
  }

  getTitle(record: SublineVersion): string | undefined {
    return record.swissSublineNumber;
  }

  updateRecord(): void {
    this.sublinesService
      .updateSublineVersion(this.getId(), this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
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
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    const selectedSublineVersion = this.getSelectedRecord();
    if (selectedSublineVersion.slnid != null) {
      this.sublinesService
        .deleteSublines(selectedSublineVersion.slnid)
        .pipe(
          takeUntil(this.ngUnsubscribe),
          catchError((err) => {
            this.notificationService.error(err, 'LIDI.SUBLINE.NOTIFICATION.DELETE_ERROR');
            return EMPTY;
          })
        )
        .subscribe(() => {
          this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
          this.backToOverview();
        });
    }
  }

  backToOverview(): void {
    this.router.navigate([Pages.LIDI.path]).then();
  }

  getFormGroup(version: SublineVersion): FormGroup {
    return this.formBuilder.group(
      {
        swissSublineNumber: [
          version.swissSublineNumber,
          [Validators.required, Validators.maxLength(50)],
        ],
        mainlineSlnid: [version.mainlineSlnid, [Validators.required]],
        slnid: [version.slnid],
        status: [version.status],
        type: [version.type, [Validators.required]],
        paymentType: [version.paymentType, [Validators.required]],
        businessOrganisation: [
          version.businessOrganisation,
          [Validators.required, Validators.maxLength(50)],
        ],
        number: [version.number, [Validators.maxLength(50)]],
        longName: [version.longName, [Validators.maxLength(255)]],
        description: [version.description, [Validators.maxLength(255)]],
        validFrom: [
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ],
        validTo: [
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required],
        ],
      },
      {
        validators: [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
      }
    );
  }

  getValidation(inputForm: string) {
    return this.validationService.getValidation(this.form?.controls[inputForm]?.errors);
  }

  displayDate(validationError: ValidationError) {
    return this.validationService.displayDate(validationError);
  }

  getValidFromPlaceHolder() {
    return this.dateService.getCurrentDateFormatted();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  initMainlineSearch() {
    this.mainlineSearchTerm
      .pipe(
        distinctUntilChanged(),
        switchMap((term) =>
          this.linesService.getLines(term, [], [], [], undefined, undefined, undefined, [
            'swissLineNumber,ASC',
          ])
        )
      )
      .subscribe((lines) => (this.mainlines = lines.objects!));
  }
}
