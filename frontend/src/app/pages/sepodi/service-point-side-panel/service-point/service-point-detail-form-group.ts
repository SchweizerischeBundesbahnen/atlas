import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import { MeanOfTransport, ReadServicePointVersion, Status, StopPointType } from '../../../../api';
import moment from 'moment';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';

export interface ServicePointDetailFormGroup extends BaseDetailFormGroup {
  status: FormControl<Status | null | undefined>;
  designationOfficial: FormControl<string | null | undefined>;
  designationLong: FormControl<string | null | undefined>;
  businessOrganisation: FormControl<string | null | undefined>;
  operatingPointType: FormControl<string | null | undefined>;
  sortCodeOfDestinationStation: FormControl<string | null | undefined>;
  stopPointType: FormControl<StopPointType | null | undefined>;
  meansOfTransport: FormControl<Array<MeanOfTransport> | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
}

export class ServicePointFormGroupBuilder {
  static buildFormGroup(version: ReadServicePointVersion): FormGroup {
    return new FormGroup<ServicePointDetailFormGroup>(
      {
        status: new FormControl(version.status),
        designationOfficial: new FormControl(version.designationOfficial, [
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
        ]),
        designationLong: new FormControl(version.designationOfficial, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required]
        ),
        validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo, [
          Validators.required,
        ]),
        businessOrganisation: new FormControl(version.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        operatingPointType: new FormControl(
          version.operatingPointType ?? version.operatingPointTechnicalTimetableType
        ),
        sortCodeOfDestinationStation: new FormControl(version.sortCodeOfDestinationStation),
        stopPointType: new FormControl(version.stopPointType),
        meansOfTransport: new FormControl(version.meansOfTransport),
        etagVersion: new FormControl(version.etagVersion),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }
}
