import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../../api';
import { DetailWrapperController } from '../../core/components/detail-wrapper/detail-wrapper-controller';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { formatDate } from '@angular/common';
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
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
    private formBuilder: FormBuilder
  ) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  get f(): { [key: string]: AbstractControl } {
    return this.form.controls;
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
    this.timetableFieldNumberService.updateVersion(this.getId(), this.form.value).subscribe();
  }

  createRecord(): void {
    this.timetableFieldNumberService
      .createVersion(this.form.value)
      .subscribe((version) => this.router.navigate([version.id]).then(() => this.ngOnInit()));
  }

  deleteRecord(): void {
    this.timetableFieldNumberService.deleteVersion(this.getId()).subscribe();
    this.router.navigate(['']).then();
  }

  getFormGroup(version: Version): FormGroup {
    return this.formBuilder.group(
      {
        swissTimetableFieldNumber: [
          version.swissTimetableFieldNumber,
          [Validators.required, Validators.maxLength(255)],
        ],
        ttfnid: [version.ttfnid, [Validators.required, Validators.maxLength(255)]],
        validFrom: [version.validFrom, [Validators.required, Validators.maxLength(255)]],
        validTo: [version.validTo, [Validators.required, Validators.maxLength(255)]],
        businessOrganisation: [
          version.businessOrganisation,
          [Validators.required, Validators.maxLength(255)],
        ],
        number: [version.number, [Validators.required, Validators.maxLength(255)]],
        name: [version.name, [Validators.required, Validators.maxLength(255)]],
        comment: [version.comment],
      },
      {
        validators: [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
      }
    );
  }

  getValidFromPlaceHolder() {
    return formatDate(new Date(), 'dd.MM.yyyy', 'en-US');
  }

  getValidation(inputForm: string) {
    const result: ValidationError[] = [];
    const inputFormValidated = this.f[inputForm];
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
    const pattern = 'DD.MM.yyyy';
    if (validationError?.value['date']) {
      const validFrom = validationError.value['date'].validFrom;
      const validTo = validationError.value['date'].validTo;
      return validFrom.format(pattern) + ' - ' + validTo.format(pattern);
    }
    if (validationError?.value['min']) {
      return validationError.value['min'].format(pattern);
    }
    if (validationError?.value['max']) {
      return validationError.value['max'].format(pattern);
    }
  }
}
