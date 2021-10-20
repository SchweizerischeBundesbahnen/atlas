import { Component, OnDestroy, OnInit } from '@angular/core';
import { LinesService, LineVersion } from '../../../../api/lidi';
import { DetailWrapperController } from '../../../../core/components/detail-wrapper/detail-wrapper-controller';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { takeUntil } from 'rxjs/operators';
import { catchError, EMPTY, Subject } from 'rxjs';
import moment from 'moment/moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { Pages } from '../../../pages';
import { ValidationError } from '../../../../core/validation/validation-error';
import {
  DateService,
  MAX_DATE,
  MAX_DATE_FORMATTED,
  MIN_DATE,
} from 'src/app/core/date/date.service';

@Component({
  templateUrl: './line-detail.component.html',
  styleUrls: ['./line-detail.component.scss'],
})
export class LineDetailComponent
  extends DetailWrapperController<LineVersion>
  implements OnInit, OnDestroy
{
  TYPE_OPTIONS = Object.values(LineVersion.TypeEnum);
  MAX_LENGTH = 255;
  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;
  VALID_TO_PLACEHOLDER = MAX_DATE_FORMATTED;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private linesService: LinesService,
    private formBuilder: FormBuilder,
    private notificationService: NotificationService,
    protected dialogService: DialogService,
    private validationService: ValidationService,
    private dateService: DateService
  ) {
    super(dialogService);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  readRecord(): LineVersion {
    return this.activatedRoute.snapshot.data.lineDetail;
  }

  getTitle(record: LineVersion): string | undefined {
    return record.swissLineNumber;
  }

  updateRecord(): void {
    this.linesService
      .updateLineVersion(this.getId(), this.form.value)
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          this.notificationService.error('LIDI.LINE.NOTIFICATION.EDIT_ERROR');
          console.log(err);
          return EMPTY;
        })
      )
      .subscribe(() => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, this.getId()])
          .then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.linesService
      .createLineVersion(this.form.value)
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          this.notificationService.error('LIDI.LINE.NOTIFICATION.ADD_ERROR');
          console.log(err);
          return EMPTY;
        })
      )
      .subscribe((version) => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, version.id])
          .then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    this.linesService
      .deleteLineVersion(this.getId())
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          this.notificationService.error('LIDI.LINE.NOTIFICATION.DELETE_ERROR');
          console.log(err);
          return EMPTY;
        })
      )
      .subscribe(() => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
  }

  backToOverview(): void {
    this.router.navigate([Pages.LIDI.path]).then();
  }

  getFormGroup(version: LineVersion): FormGroup {
    return this.formBuilder.group(
      {
        swissLineNumber: [
          version.swissLineNumber,
          [Validators.required, Validators.maxLength(this.MAX_LENGTH)],
        ],
        slnid: [version.slnid, [Validators.required, Validators.maxLength(this.MAX_LENGTH)]],
        type: [version.type, [Validators.required, Validators.maxLength(this.MAX_LENGTH)]],
        businessOrganisation: [
          version.businessOrganisation,
          [Validators.required, Validators.maxLength(this.MAX_LENGTH)],
        ],
        shortName: [
          version.shortName,
          [Validators.required, Validators.maxLength(this.MAX_LENGTH)],
        ],
        alternativeName: [version.alternativeName, [Validators.maxLength(this.MAX_LENGTH)]],
        combinationName: [version.combinationName, [Validators.maxLength(this.MAX_LENGTH)]],
        longName: [version.longName, [Validators.maxLength(this.MAX_LENGTH)]],
        icon: [version.icon, [Validators.maxLength(this.MAX_LENGTH)]],
        colorFontRgb: [version.colorFontRgb],
        colorBackRgb: [version.colorBackRgb],
        colorFontCmyk: [version.colorFontCmyk],
        colorBackCmyk: [version.colorBackCmyk],
        description: [version.description, [Validators.maxLength(this.MAX_LENGTH)]],
        validFrom: [
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ],
        validTo: [
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required],
        ],
        comment: [version.comment],
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
}
