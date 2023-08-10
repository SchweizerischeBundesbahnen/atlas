import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import {
  Category,
  MeanOfTransport,
  ReadServicePointVersion,
  SpatialReference,
  Status,
  StopPointType,
} from '../../../../api';
import moment from 'moment';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { GeographyFormGroup } from '../../geography/geography-form-group';

export interface ServicePointDetailFormGroup extends BaseDetailFormGroup {
  status: FormControl<Status | null | undefined>;
  designationOfficial: FormControl<string | null | undefined>;
  designationLong: FormControl<string | null | undefined>;
  businessOrganisation: FormControl<string | null | undefined>;
  operatingPointType: FormControl<string | null | undefined>;
  sortCodeOfDestinationStation: FormControl<string | null | undefined>;
  stopPointType: FormControl<StopPointType | null | undefined>;
  meansOfTransport: FormControl<Array<MeanOfTransport> | null | undefined>;
  categories: FormControl<Array<Category> | null | undefined>;
  operatingPointRouteNetwork: FormControl<boolean | null | undefined>;
  operatingPointKilometer: FormControl<boolean | null | undefined>;
  operatingPointKilometerMaster: FormControl<number | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
  servicePointGeolocation: FormGroup<GeographyFormGroup>;
}

export class ServicePointFormGroupBuilder {
  static buildFormGroup(version: ReadServicePointVersion): FormGroup {
    return new FormGroup<ServicePointDetailFormGroup>(
      {
        status: new FormControl(version.status),
        designationOfficial: new FormControl(version.designationOfficial, [
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(30),
          Validators.minLength(2),
        ]),
        designationLong: new FormControl(version.designationLong, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(50),
          Validators.minLength(2),
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
        categories: new FormControl(version.categories),
        etagVersion: new FormControl(version.etagVersion),
        servicePointGeolocation: new FormGroup<GeographyFormGroup>({
          east: new FormControl(this.getCoordinates(version)?.east),
          north: new FormControl(this.getCoordinates(version)?.north),
          height: new FormControl(version.servicePointGeolocation?.height),
          spatialReference: new FormControl(version.servicePointGeolocation?.spatialReference),
        }),
        operatingPointRouteNetwork: new FormControl(version.operatingPointRouteNetwork),
        operatingPointKilometer: new FormControl(version.operatingPointKilometer),
        operatingPointKilometerMaster: new FormControl(
          version.operatingPointKilometerMaster?.number
        ),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }

  private static getCoordinates(version: ReadServicePointVersion) {
    if (version.servicePointGeolocation?.spatialReference === SpatialReference.Wgs84) {
      return version.servicePointGeolocation?.wgs84;
    }
    return version.servicePointGeolocation?.lv95;
  }
}
