import { Component, OnDestroy, OnInit } from '@angular/core';
import { LineVersion, SublinesService, SublineVersion } from '../../../../api';
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
import { Page } from 'src/app/core/model/page';

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

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private sublinesService: SublinesService,
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
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          const errorMessage =
            err.status == 409
              ? 'COMMON.SWISSNUMBER_NOT_UNIQUE'
              : 'LIDI.SUBLINE.NOTIFICATION.EDIT_ERROR';
          this.notificationService.error(errorMessage);
          console.log(err);
          return EMPTY;
        })
      )
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
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          const errorMessage =
            err.status == 409
              ? 'COMMON.SWISSNUMBER_NOT_UNIQUE'
              : 'LIDI.SUBLINE.NOTIFICATION.ADD_ERROR';
          this.notificationService.error(errorMessage);
          console.log(err);
          return EMPTY;
        })
      )
      .subscribe((version) => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    this.sublinesService
      .deleteSublineVersion(this.getId())
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          this.notificationService.error('LIDI.SUBLINE.NOTIFICATION.DELETE_ERROR');
          console.log(err);
          return EMPTY;
        })
      )
      .subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
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
        swissLineNumber: [version.swissLineNumber, [Validators.required, Validators.maxLength(50)]],
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
}
