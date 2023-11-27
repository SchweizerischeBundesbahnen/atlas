import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ReadLoadingPointVersion } from '../../../api';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../core/components/base-detail/base-detail-form-group';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';

export interface LoadingPointDetailFormGroup extends BaseDetailFormGroup {
  number: FormControl<number | null | undefined>;
  servicePointNumber: FormControl<number | null | undefined>;
  designation: FormControl<string | null | undefined>;
  designationLong: FormControl<string | null | undefined>;
  connectionPoint: FormControl<boolean | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
}

export class LoadingPointFormGroupBuilder {
  static buildFormGroup(version?: ReadLoadingPointVersion): FormGroup<LoadingPointDetailFormGroup> {
    return new FormGroup<LoadingPointDetailFormGroup>(
      {
        number: new FormControl(version?.number, [
          Validators.min(0),
          Validators.max(9999),
          AtlasCharsetsValidator.numeric,
          Validators.required,
        ]),
        servicePointNumber: new FormControl(version?.servicePointNumber.number),
        designation: new FormControl(version?.designation, [
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(12),
        ]),
        designationLong: new FormControl(version?.designationLong, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(35),
        ]),
        connectionPoint: new FormControl(version?.connectionPoint ?? false),
        validFrom: new FormControl(version?.validFrom ? moment(version.validFrom) : null, [
          Validators.required,
        ]),
        validTo: new FormControl(version?.validTo ? moment(version.validTo) : null, [
          Validators.required,
        ]),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }
}
