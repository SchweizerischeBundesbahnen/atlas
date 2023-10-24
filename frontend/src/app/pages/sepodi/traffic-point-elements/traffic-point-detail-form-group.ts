import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ReadTrafficPointElementVersionModel, SpatialReference } from '../../../api';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../core/components/base-detail/base-detail-form-group';
import { GeographyFormGroup } from '../geography/geography-form-group';
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
  servicePointGeolocation: FormGroup<GeographyFormGroup>;
}

export class TrafficPointElementFormGroupBuilder {
  static buildFormGroup(version: ReadTrafficPointElementVersionModel): FormGroup {
    const formGroup = new FormGroup<TrafficPointElementDetailFormGroup>(
      {
        sloid: new FormControl(version.sloid),
        designationOperational: new FormControl(version.designationOperational),
        parentSloid: new FormControl(version.parentSloid),
        length: new FormControl(version.length),
        boardingAreaHeight: new FormControl(version.boardingAreaHeight),
        compassDirection: new FormControl(version.compassDirection),
        designation: new FormControl(version.designation, [
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(30),
          Validators.minLength(2),
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ),
        validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo, [
          Validators.required,
        ]),
        servicePointGeolocation: new FormGroup<GeographyFormGroup>({
          east: new FormControl(this.getCoordinates(version)?.east, [
            this.getValidatorForCoordinates(
              version.trafficPointElementGeolocation?.spatialReference,
            ),
          ]),
          north: new FormControl(this.getCoordinates(version)?.north, [
            this.getValidatorForCoordinates(
              version.trafficPointElementGeolocation?.spatialReference,
            ),
          ]),
          height: new FormControl(version.trafficPointElementGeolocation?.height, [
            AtlasCharsetsValidator.decimalWithDigits(4),
          ]),
          spatialReference: new FormControl(
            version.trafficPointElementGeolocation?.spatialReference,
          ),
        }),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
    return formGroup;
  }

  private static getCoordinates(version: ReadTrafficPointElementVersionModel) {
    if (version.trafficPointElementGeolocation?.spatialReference === SpatialReference.Wgs84) {
      return version.trafficPointElementGeolocation?.wgs84;
    }
    return version.trafficPointElementGeolocation?.lv95;
  }

  private static getValidatorForCoordinates(spatialReference?: SpatialReference) {
    return AtlasCharsetsValidator.decimalWithDigits(
      spatialReference == SpatialReference.Lv95 ? LV95_MAX_DIGITS : WGS84_MAX_DIGITS,
    );
  }
}
