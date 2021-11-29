import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../../../api';
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
import { Pages } from '../../pages';
import {
  DateService,
  MAX_DATE,
  MAX_DATE_FORMATTED,
  MIN_DATE,
} from '../../../core/date/date.service';

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
  VALID_TO_PLACEHOLDER = MAX_DATE_FORMATTED;
  NAME_PLACEHOLDER = 'Grenze - Bad, Bahnhof - Basel SBB - Zürich HB - Chur';

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;
  MAX_LENGTH_255 = 255;
  MAX_LENGTH_250 = 250;
  MAX_LENGTH_50 = 50;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
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
        this.router.navigate([Pages.TTFN.path, this.record.ttfnid]).then(() => this.ngOnInit());
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
        this.router.navigate([Pages.TTFN.path, version.ttfnid]).then(() => this.ngOnInit());
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
    this.router.navigate([Pages.TTFN.path]).then();
  }

  getFormGroup(version: Version): FormGroup {
    return this.formBuilder.group(
      {
        swissTimetableFieldNumber: [
          version.swissTimetableFieldNumber,
          [Validators.required, Validators.maxLength(this.MAX_LENGTH_50)],
        ],
        validFrom: [
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ],
        validTo: [
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required],
        ],
        ttfnid: version.ttfnid,
        businessOrganisation: [
          version.businessOrganisation,
          [Validators.required, Validators.maxLength(this.MAX_LENGTH_50)],
        ],
        number: [
          version.number,
          [
            Validators.required,
            Validators.maxLength(this.MAX_LENGTH_50),
            Validators.pattern('^[.0-9]+$'),
          ],
        ],
        name: [version.name, Validators.maxLength(this.MAX_LENGTH_255)],
        comment: [version.comment, Validators.maxLength(this.MAX_LENGTH_250)],
      },
      {
        validators: [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
      }
    );
  }

  getValidFromPlaceHolder() {
    return this.dateService.getCurrentDateFormatted();
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
