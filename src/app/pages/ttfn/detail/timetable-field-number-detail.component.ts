import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../../../api/ttfn';
import { DetailWrapperController } from '../../../core/components/detail-wrapper/detail-wrapper-controller';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../../core/notification/notification.service';
import { catchError, EMPTY, Subject } from 'rxjs';
import { ValidationError } from '../../../core/validation/validation-error';
import moment from 'moment/moment';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import { takeUntil } from 'rxjs/operators';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../core/validation/validation.service';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
})
export class TimetableFieldNumberDetailComponent
  extends DetailWrapperController<Version>
  implements OnInit, OnDestroy
{
  SWISS_TIMETABLE_FIELD_NUMBER_PLACEHOLDER = 'bO.BEX:a';
  TTFNID_PLACEHOLDER = 'ch:1:fpfnid:100000';
  VALID_TO_PLACEHOLDER = '31.12.2099';
  NAME_PLACEHOLDER = 'Grenze - Bad, Bahnhof - Basel SBB - Zürich HB - Chur';

  DATE_PATTERN = 'DD.MM.yyyy';
  MAX_LENGTH = 255;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
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

  get minDateValue(): Date {
    return new Date(moment('1900-01-01 00:00:00').valueOf());
  }

  get maxDateValue(): Date {
    return new Date(moment('2099-12-31 23:59:59').valueOf());
  }

  readRecord(): Version {
    return this.activatedRoute.snapshot.data.timetableFieldNumberDetail;
  }

  getTitle(record: Version): string | undefined {
    return record.swissTimetableFieldNumber;
  }

  updateRecord(): void {
    this.timetableFieldNumberService
      .updateVersion(this.getId(), this.form.value)
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          this.notificationService.error('TTFN.NOTIFICATION.EDIT_ERROR');
          console.log(err);
          return EMPTY;
        })
      )
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.EDIT_SUCCESS');
        this.router.navigate([this.getId()]).then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.timetableFieldNumberService
      .createVersion(this.form.value)
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          this.notificationService.error('TTFN.NOTIFICATION.ADD_ERROR');
          console.log(err);
          return EMPTY;
        })
      )
      .subscribe((version) => {
        this.notificationService.success('TTFN.NOTIFICATION.ADD_SUCCESS');
        this.router.navigate([version.id]).then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    this.timetableFieldNumberService
      .deleteVersion(this.getId())
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          this.notificationService.error('TTFN.NOTIFICATION.DELETE_ERROR');
          console.log(err);
          return EMPTY;
        })
      )
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
  }

  backToOverview(): void {
    this.router.navigate(['']).then();
  }

  getFormGroup(version: Version): FormGroup {
    return this.formBuilder.group(
      {
        swissTimetableFieldNumber: [
          version.swissTimetableFieldNumber,
          [Validators.required, Validators.maxLength(this.MAX_LENGTH)],
        ],
        ttfnid: [version.ttfnid, [Validators.required, Validators.maxLength(this.MAX_LENGTH)]],
        validFrom: [
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ],
        validTo: [
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required],
        ],
        businessOrganisation: [
          version.businessOrganisation,
          [Validators.required, Validators.maxLength(this.MAX_LENGTH)],
        ],
        number: [version.number, [Validators.required, Validators.maxLength(this.MAX_LENGTH)]],
        name: [version.name, [Validators.required, Validators.maxLength(this.MAX_LENGTH)]],
        comment: [version.comment],
      },
      {
        validators: [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
      }
    );
  }

  getValidFromPlaceHolder() {
    return moment().format(this.DATE_PATTERN);
  }

  getValidation(inputForm: string) {
    return this.validationService.getValidation(this.form?.controls[inputForm]?.errors);
  }

  displayDate(validationError: ValidationError) {
    return this.validationService.displayDate(validationError);
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
