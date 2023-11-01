import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ReadTrafficPointElementVersion, SpatialReference } from '../../../api';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../core/components/base-detail/base-detail-form-group';
import { GeographyFormGroup, GeographyFormGroupBuilder } from '../geography/geography-form-group';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import { LV95_MAX_DIGITS, WGS84_MAX_DIGITS } from '../geography/geography.component';

export interface TrafficPointElementDetailFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  designation: FormControl<string | null | undefined>;
  designationOperational: FormControl<string | null | undefined>;
  parentSloid: FormControl<string | null | undefined>;
  length: FormControl<number | null | undefined>;
  boardingAreaHeight: FormControl<number | null | undefined>;
  compassDirection: FormControl<number | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
  trafficPointGeolocation: FormGroup<GeographyFormGroup>;
}

export class TrafficPointElementFormGroupBuilder {
  static buildFormGroup(
    version?: ReadTrafficPointElementVersion,
  ): FormGroup<TrafficPointElementDetailFormGroup> {
    return new FormGroup<TrafficPointElementDetailFormGroup>(
      {
        sloid: new FormControl(version?.sloid),
        designationOperational: new FormControl(version?.designationOperational, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(20),
        ]),
        parentSloid: new FormControl(version?.parentSloid),
        length: new FormControl(version?.length, [AtlasCharsetsValidator.decimalWithDigits(3)]),
        boardingAreaHeight: new FormControl(version?.boardingAreaHeight, [
          AtlasCharsetsValidator.decimalWithDigits(2),
        ]),
        compassDirection: new FormControl(version?.compassDirection, [
          AtlasCharsetsValidator.decimalWithDigits(2),
        ]),
        designation: new FormControl(version?.designation, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(40),
        ]),
        validFrom: new FormControl(version?.validFrom ? moment(version.validFrom) : null, [
          Validators.required,
        ]),
        validTo: new FormControl(version?.validTo ? moment(version.validTo) : null, [
          Validators.required,
        ]),
        trafficPointGeolocation: GeographyFormGroupBuilder.buildFormGroup(
          version?.trafficPointElementGeolocation,
        ),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  private static getCoordinates(version?: ReadTrafficPointElementVersion) {
    if (version?.trafficPointElementGeolocation?.spatialReference === SpatialReference.Wgs84) {
      return version.trafficPointElementGeolocation?.wgs84;
    }
    return version?.trafficPointElementGeolocation?.lv95;
  }

  private static getValidatorForCoordinates(spatialReference?: SpatialReference) {
    return AtlasCharsetsValidator.decimalWithDigits(
      spatialReference == SpatialReference.Lv95 ? LV95_MAX_DIGITS : WGS84_MAX_DIGITS,
    );
  }
}
