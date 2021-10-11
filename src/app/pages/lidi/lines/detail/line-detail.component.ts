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
import { LINE_DETAILS_PATH } from '../../lidi.routing.module';

@Component({
  templateUrl: './line-detail.component.html',
  styleUrls: ['./line-detail.component.scss'],
})
export class LineDetailComponent
  extends DetailWrapperController<LineVersion>
  implements OnInit, OnDestroy
{
  MAX_LENGTH = 255;
  DATE_PATTERN = 'DD.MM.yyyy';

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private linesService: LinesService,
    private formBuilder: FormBuilder,
    private notificationService: NotificationService,
    protected dialogService: DialogService,
    private validationService: ValidationService
  ) {
    super(dialogService);
  }

  ngOnInit() {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
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
        this.router.navigate([LINE_DETAILS_PATH, this.getId()]).then(() => this.ngOnInit());
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
        this.router.navigate([LINE_DETAILS_PATH, version.id]).then(() => this.ngOnInit());
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
    return moment().format(this.DATE_PATTERN);
  }

  get minDateValue(): Date {
    return new Date(moment('1900-01-01 00:00:00').valueOf());
  }

  get maxDateValue(): Date {
    return new Date(moment('2099-12-31 23:59:59').valueOf());
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
