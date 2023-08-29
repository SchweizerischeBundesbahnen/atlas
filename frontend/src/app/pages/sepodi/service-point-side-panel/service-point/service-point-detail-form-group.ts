import {
  Form,
  FormControl,
  FormGroup,
  Validators,
  ɵFormGroupValue,
  ɵTypedOrUntyped,
} from '@angular/forms';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import {
  Category,
  CreateServicePointVersion,
  MeanOfTransport,
  OperatingPointTechnicalTimetableType,
  OperatingPointTrafficPointType,
  OperatingPointType,
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
import { ServicePointType } from './service-point-type';
import { AtLeastOneValidator } from '../../../../core/validation/boolean-cross-validator/at-least-one-validator';

export interface ServicePointDetailFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  abbreviation: FormControl<string | null | undefined>;
  status: FormControl<Status | null | undefined>;
  designationOfficial: FormControl<string | null | undefined>;
  designationLong: FormControl<string | null | undefined>;
  businessOrganisation: FormControl<string | null | undefined>;
  operatingPointType: FormControl<string | null | undefined>;
  freightServicePoint: FormControl<boolean | null | undefined>;
  sortCodeOfDestinationStation: FormControl<string | null | undefined>;
  stopPoint: FormControl<boolean | null | undefined>;
  stopPointType: FormControl<StopPointType | null | undefined>;
  meansOfTransport: FormControl<Array<MeanOfTransport> | null | undefined>;
  categories: FormControl<Array<Category> | null | undefined>;
  operatingPointRouteNetwork: FormControl<boolean | null | undefined>;
  operatingPointKilometer: FormControl<boolean | null | undefined>;
  operatingPointKilometerMaster: FormControl<number | null | undefined>;
  operatingPointTrafficPointType: FormControl<OperatingPointTrafficPointType | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
  servicePointGeolocation: FormGroup<GeographyFormGroup>;

  selectedType: FormControl<ServicePointType | null | undefined>;
}

export class ServicePointFormGroupBuilder {
  static buildFormGroup(version: ReadServicePointVersion): FormGroup {
    const formGroup = new FormGroup<ServicePointDetailFormGroup>(
      {
        sloid: new FormControl(version.sloid),
        abbreviation: new FormControl(version.abbreviation),
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
        sortCodeOfDestinationStation: new FormControl(version.sortCodeOfDestinationStation, [
          Validators.maxLength(5),
        ]),
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
        selectedType: new FormControl(this.determineType(version), { nonNullable: true }),
        freightServicePoint: new FormControl(version.freightServicePoint),
        stopPoint: new FormControl(version.stopPoint),
        operatingPointTrafficPointType: new FormControl(version.operatingPointTrafficPointType),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
    this.initConditionalValidators(formGroup);
    return formGroup;
  }

  private static getCoordinates(version: ReadServicePointVersion) {
    if (version.servicePointGeolocation?.spatialReference === SpatialReference.Wgs84) {
      return version.servicePointGeolocation?.wgs84;
    }
    return version.servicePointGeolocation?.lv95;
  }

  private static determineType(version: ReadServicePointVersion) {
    if (version.operatingPointType || version.operatingPointTechnicalTimetableType) {
      return ServicePointType.OperatingPoint;
    }
    if (version.stopPoint || version.freightServicePoint) {
      return ServicePointType.StopPoint;
    }
    if (version.fareStop) {
      return ServicePointType.FareStop;
    }
    return ServicePointType.ServicePoint;
  }

  private static initConditionalValidators(formGroup: FormGroup<ServicePointDetailFormGroup>) {
    formGroup.controls.selectedType.valueChanges.subscribe((newType) => {
      if (newType === ServicePointType.OperatingPoint) {
        formGroup.controls.operatingPointType.setValidators([Validators.required]);
      } else {
        formGroup.controls.operatingPointType.clearValidators();
      }
      if (newType === ServicePointType.StopPoint) {
        formGroup.addValidators(AtLeastOneValidator.of('stopPoint', 'freightServicePoint'));
      } else {
        formGroup.clearValidators();
        formGroup.updateValueAndValidity();
      }

      formGroup.controls.operatingPointType.updateValueAndValidity();
    });

    formGroup.controls.stopPoint.valueChanges.subscribe((isStopPoint) => {
      if (isStopPoint) {
        formGroup.controls.stopPointType.setValidators([Validators.required]);
        formGroup.controls.meansOfTransport.setValidators([Validators.required]);
      } else {
        formGroup.controls.stopPointType.clearValidators();
        formGroup.controls.meansOfTransport.clearValidators();
      }
      formGroup.controls.operatingPointType.updateValueAndValidity();
    });
  }

  static getWritableServicePoint(
    form: FormGroup<ServicePointDetailFormGroup>
  ): CreateServicePointVersion {
    const value = form.value;

    const writableForm: CreateServicePointVersion = {
      sloid: value.sloid!,
      designationOfficial: value.designationOfficial!,
      designationLong: value.designationLong!,
      abbreviation: value.abbreviation!,
      businessOrganisation: value.businessOrganisation!,
      categories: value.categories!,
      operatingPointRouteNetwork: value.operatingPointRouteNetwork!,
      operatingPointKilometerMasterNumber: value.operatingPointKilometerMaster!,
      meansOfTransport: [],
      status: value.status!,
      validFrom: value.validFrom!.toDate(),
      validTo: value.validTo!.toDate(),
    };
    if (value.selectedType == ServicePointType.ServicePoint) {
      writableForm.operatingPoint = false;
      writableForm.operatingPointWithTimetable = false;
    }
    if (value.selectedType == ServicePointType.OperatingPoint) {
      writableForm.operatingPointType = this.getOperatingPointType(form);
      writableForm.operatingPointTechnicalTimetableType =
        this.getOperatingPointTechnicalTimetableType(form);

      writableForm.operatingPoint = true;
      writableForm.operatingPointWithTimetable = !writableForm.operatingPointType;
    }
    if (value.selectedType == ServicePointType.StopPoint) {
      writableForm.meansOfTransport = value.meansOfTransport!;
      writableForm.stopPointType = value.stopPointType!;
      writableForm.freightServicePoint = value.freightServicePoint!;
      writableForm.sortCodeOfDestinationStation = value.sortCodeOfDestinationStation!;

      writableForm.operatingPoint = true;
      writableForm.operatingPointWithTimetable = true;
    }
    if (value.selectedType == ServicePointType.FareStop) {
      writableForm.operatingPointTrafficPointType = OperatingPointTrafficPointType.TariffPoint;

      writableForm.operatingPoint = true;
      writableForm.operatingPointWithTimetable = true;
    }
    if (value.servicePointGeolocation) {
      writableForm.servicePointGeolocation = {
        spatialReference: value.servicePointGeolocation.spatialReference!,
        north: value.servicePointGeolocation.north!,
        east: value.servicePointGeolocation.east!,
        height: value.servicePointGeolocation.height!,
      };
    }
    return writableForm;
  }

  private static getOperatingPointTechnicalTimetableType(
    form: FormGroup<ServicePointDetailFormGroup>
  ) {
    if (
      Object.values(OperatingPointTechnicalTimetableType).includes(
        form.value.operatingPointType as OperatingPointTechnicalTimetableType
      )
    ) {
      return form.value.operatingPointType! as OperatingPointTechnicalTimetableType;
    }
    return undefined;
  }

  private static getOperatingPointType(form: FormGroup<ServicePointDetailFormGroup>) {
    if (
      Object.values(OperatingPointType).includes(
        form.value.operatingPointType as OperatingPointType
      )
    ) {
      return form.value.operatingPointType! as OperatingPointType;
    }
    return undefined;
  }
}
