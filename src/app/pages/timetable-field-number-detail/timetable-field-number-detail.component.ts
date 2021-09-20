import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../../api';
import { DetailWrapperController } from '../../core/components/detail-wrapper/detail-wrapper-controller';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../core/notification/notification.service';
import { catchError } from 'rxjs';
import { ValidationError } from './validation-error';
import moment from 'moment/moment';
import { DateRangeValidator } from './date-range-validator';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
})
export class TimetableFieldNumberDetailComponent
  extends DetailWrapperController<Version>
  implements OnInit
{
  SWISS_TIMETABLE_FIELD_NUMBER_PLACEHOLDER = 'bO.BEX:a';
  TTFNID_PLACEHOLDER = 'ch:1:fpfnid:100000';
  VALID_TO_PLACEHOLDER = '31.12.2099';
  NAME_PLACEHOLDER = 'Grenze - Bad, Bahnhof - Basel SBB - Zürich HB - Chur';

  DATE_PATTERN = 'DD.MM.yyyy';
  MAX_LENGTH = 255;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
    private formBuilder: FormBuilder,
    private notificationService: NotificationService
  ) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  get minDateValue(): Date {
    return new Date(moment().valueOf());
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
        catchError((err) => {
          this.notificationService.error('TTFN.NOTIFICATION.EDIT_ERROR');
          throw err;
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
        catchError((err) => {
          this.notificationService.error('TTFN.NOTIFICATION.ADD_ERROR');
          throw err;
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
        catchError((err) => {
          this.notificationService.error('TTFN.NOTIFICATION.DELETE_ERROR');
          throw err;
        })
      )
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.DELETE_SUCCESS');
        this.router.navigate(['']).then();
      });
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
          version.validFrom,
          [Validators.required, Validators.maxLength(this.MAX_LENGTH)],
        ],
        validTo: [version.validTo, [Validators.required, Validators.maxLength(this.MAX_LENGTH)]],
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
    const result: ValidationError[] = [];
    const inputFormValidated = this.form.controls[inputForm];
    const controlErrors = inputFormValidated?.errors;
    if (controlErrors) {
      Object.keys(controlErrors).forEach((keyError) => {
        result.push({
          error: 'VALIDATION.' + keyError.toUpperCase(),
          value: controlErrors[keyError],
        });
      });
    }
    return result;
  }

  displayDate(validationError: ValidationError) {
    const pattern = this.DATE_PATTERN;
    if (validationError?.value.date) {
      const validFrom = validationError.value.date.validFrom;
      const validTo = validationError.value.date.validTo;
      return validFrom.format(pattern) + ' - ' + validTo.format(pattern);
    }
    if (validationError?.value.min) {
      return validationError.value.min.format(pattern);
    }
    if (validationError?.value.max) {
      return validationError.value.max.format(pattern);
    }
  }
}
