import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import {
  Category,
  Country,
  CreateServicePointVersion,
  MeanOfTransport,
  OperatingPointTechnicalTimetableType,
  OperatingPointTrafficPointType,
  OperatingPointType,
  ReadServicePointVersion,
  Status,
  StopPointType,
} from '../../../../api';
import moment from 'moment';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import {
  GeographyFormGroup,
  GeographyFormGroupBuilder,
} from '../../geography/geography-form-group';
import { ServicePointType } from './service-point-type';
import { AtLeastOneValidator } from '../../../../core/validation/boolean-cross-validator/at-least-one-validator';

export interface ServicePointDetailFormGroup extends BaseDetailFormGroup {
  country?: FormControl<Country | null>;
  number: FormControl<number | null | undefined>;
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
  servicePointGeolocation?: FormGroup<GeographyFormGroup>;
  selectedType: FormControl<ServicePointType | null | undefined>;
}

type OptionalKeysOfServicePointDetailFormGroup = {
  [K in keyof ServicePointDetailFormGroup]-?: undefined extends ServicePointDetailFormGroup[K]
    ? K
    : never;
}[keyof ServicePointDetailFormGroup];

export class ServicePointFormGroupBuilder {
  static addGroupToForm(
    form: FormGroup<ServicePointDetailFormGroup>,
    controlName: keyof ServicePointDetailFormGroup,
    group: FormGroup,
  ) {
    form.addControl(controlName, group);
  }

  static removeGroupFromForm(
    form: FormGroup<ServicePointDetailFormGroup>,
    controlName: OptionalKeysOfServicePointDetailFormGroup,
  ) {
    form.removeControl(controlName);
  }

  static buildEmptyFormGroup(): FormGroup<ServicePointDetailFormGroup> {
    const formGroup = new FormGroup<ServicePointDetailFormGroup>(
      {
        number: new FormControl({ value: null, disabled: true }, [
          Validators.min(1),
          Validators.max(99999),
          AtlasCharsetsValidator.numeric,
          Validators.required,
        ]),
        country: new FormControl(null, [Validators.required]),
        abbreviation: new FormControl(null, [
          Validators.maxLength(6),
          Validators.minLength(1),
          AtlasCharsetsValidator.uppercaseNumeric,
        ]),
        status: new FormControl(),
        designationOfficial: new FormControl(null, [
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(30),
          Validators.minLength(2),
        ]),
        designationLong: new FormControl(null, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(50),
          Validators.minLength(2),
        ]),
        validFrom: new FormControl(null, [Validators.required]),
        validTo: new FormControl(null, [Validators.required]),
        businessOrganisation: new FormControl(null, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        operatingPointType: new FormControl(),
        sortCodeOfDestinationStation: new FormControl(null, [Validators.maxLength(5)]),
        stopPointType: new FormControl(),
        meansOfTransport: new FormControl([]),
        categories: new FormControl([]),
        servicePointGeolocation: GeographyFormGroupBuilder.buildFormGroup(),
        operatingPointRouteNetwork: new FormControl(),
        operatingPointKilometer: new FormControl(),
        operatingPointKilometerMaster: new FormControl(),
        selectedType: new FormControl(null, Validators.required),
        freightServicePoint: new FormControl(),
        stopPoint: new FormControl(),
        operatingPointTrafficPointType: new FormControl(),
        etagVersion: new FormControl(),
        creationDate: new FormControl(),
        editionDate: new FormControl(),
        editor: new FormControl(),
        creator: new FormControl(),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
    this.initConditionalValidators(formGroup);
    return formGroup;
  }

  static buildFormGroup(version: ReadServicePointVersion): FormGroup {
    const formGroup = new FormGroup<ServicePointDetailFormGroup>(
      {
        number: new FormControl(version.number.numberShort),
        country: new FormControl(version.country),
        abbreviation: new FormControl(version.abbreviation, [
          Validators.maxLength(6),
          Validators.minLength(2),
          AtlasCharsetsValidator.uppercaseNumeric,
        ]),
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
          [Validators.required],
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
          version.operatingPointType ?? version.operatingPointTechnicalTimetableType,
        ),
        sortCodeOfDestinationStation: new FormControl(version.sortCodeOfDestinationStation, [
          Validators.maxLength(5),
        ]),
        stopPointType: new FormControl(version.stopPointType),
        meansOfTransport: new FormControl(version.meansOfTransport),
        categories: new FormControl(version.categories),
        operatingPointRouteNetwork: new FormControl(version.operatingPointRouteNetwork),
        operatingPointKilometer: new FormControl(version.operatingPointKilometer),
        operatingPointKilometerMaster: new FormControl(
          version.operatingPointKilometerMaster?.number,
        ),
        selectedType: new FormControl(this.determineType(version), { nonNullable: true }),
        freightServicePoint: new FormControl(version.freightServicePoint),
        stopPoint: new FormControl(version.stopPoint),
        operatingPointTrafficPointType: new FormControl(version.operatingPointTrafficPointType),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );

    if (version.servicePointGeolocation?.spatialReference) {
      formGroup.addControl(
        'servicePointGeolocation',
        GeographyFormGroupBuilder.buildFormGroup(version.servicePointGeolocation),
      );
    }

    this.initConditionalValidators(formGroup);
    return formGroup;
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
    this.initSelectedTypeValidation(formGroup);
    this.initStopPointValidation(formGroup);
    this.initFreightServicePointValidation(formGroup);
  }

  private static initSelectedTypeValidation(formGroup: FormGroup<ServicePointDetailFormGroup>) {
    formGroup.controls.selectedType.valueChanges.subscribe((newType) => {
      if (newType === ServicePointType.OperatingPoint) {
        formGroup.controls.operatingPointType.setValidators([Validators.required]);
      } else {
        formGroup.controls.operatingPointType.clearValidators();
      }
      formGroup.controls.operatingPointType.updateValueAndValidity();

      if (newType === ServicePointType.StopPoint) {
        formGroup.addValidators(AtLeastOneValidator.of('stopPoint', 'freightServicePoint'));
      } else {
        formGroup.clearValidators();
        formGroup.controls.stopPoint.updateValueAndValidity();
        formGroup.controls.freightServicePoint.updateValueAndValidity();
        formGroup.updateValueAndValidity();
      }
    });
  }

  private static initStopPointValidation(formGroup: FormGroup<ServicePointDetailFormGroup>) {
    formGroup.controls.stopPoint.valueChanges.subscribe((isStopPoint) => {
      if (isStopPoint) {
        formGroup.controls.meansOfTransport.setValidators([Validators.required]);
      } else {
        formGroup.controls.meansOfTransport.clearValidators();
      }
      formGroup.controls.meansOfTransport.updateValueAndValidity();
    });
  }

  private static initFreightServicePointValidation(
    formGroup: FormGroup<ServicePointDetailFormGroup>,
  ) {
    formGroup.controls.freightServicePoint.valueChanges.subscribe((isFreightServicePoint) => {
      if (isFreightServicePoint) {
        formGroup.controls.sortCodeOfDestinationStation.setValidators([Validators.maxLength(5)]);
        if (
          String(formGroup.controls.number.value).startsWith('85') &&
          !formGroup.controls.validFrom.value?.isAfter(moment())
        ) {
          formGroup.controls.sortCodeOfDestinationStation.setValidators([
            Validators.required,
            Validators.maxLength(5),
          ]);
        }
      } else {
        formGroup.controls.sortCodeOfDestinationStation.clearValidators();
      }
      formGroup.controls.sortCodeOfDestinationStation.updateValueAndValidity();
    });
  }

  static getWritableServicePoint(
    form: FormGroup<ServicePointDetailFormGroup>,
  ): CreateServicePointVersion {
    const value = form.value;

    const writableForm: CreateServicePointVersion = {
      country: value.country!,
      numberShort: value.number!,
      designationOfficial: value.designationOfficial!,
      designationLong: value.designationLong ? value.designationLong : undefined,
      abbreviation:
        value.abbreviation && value.abbreviation.trim().length !== 0
          ? value.abbreviation
          : undefined,
      businessOrganisation: value.businessOrganisation!,
      categories: value.categories!,
      operatingPointRouteNetwork: value.operatingPointRouteNetwork!,
      operatingPointKilometerMasterNumber: value.operatingPointRouteNetwork
        ? undefined
        : value.operatingPointKilometerMaster!,
      meansOfTransport: [],
      validFrom: value.validFrom!.toDate(),
      validTo: value.validTo!.toDate(),
      etagVersion: value.etagVersion!,
      creationDate: value.creationDate!,
      editionDate: value.editionDate!,
      editor: value.editor!,
      creator: value.creator!,
    };
    if (value.selectedType == ServicePointType.OperatingPoint) {
      writableForm.operatingPointType = this.getOperatingPointType(form);
      writableForm.operatingPointTechnicalTimetableType =
        this.getOperatingPointTechnicalTimetableType(form);
    }
    if (value.selectedType == ServicePointType.StopPoint) {
      if (value.stopPoint) {
        writableForm.meansOfTransport = value.meansOfTransport!;
        writableForm.stopPointType = value.stopPointType!;
      }
      writableForm.freightServicePoint = value.freightServicePoint!;
      if (value.freightServicePoint) {
        writableForm.sortCodeOfDestinationStation = value.sortCodeOfDestinationStation!;
      }
    }
    if (value.selectedType == ServicePointType.FareStop) {
      writableForm.operatingPointKilometerMasterNumber = undefined;
      writableForm.operatingPointRouteNetwork = false;
      writableForm.operatingPointTrafficPointType = OperatingPointTrafficPointType.TariffPoint;
    }
    if (value.selectedType == ServicePointType.ServicePoint) {
      writableForm.operatingPointKilometerMasterNumber = undefined;
      writableForm.operatingPointRouteNetwork = false;
    }
    if (value.servicePointGeolocation?.spatialReference) {
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
    form: FormGroup<ServicePointDetailFormGroup>,
  ) {
    if (
      Object.values(OperatingPointTechnicalTimetableType).includes(
        form.value.operatingPointType as OperatingPointTechnicalTimetableType,
      )
    ) {
      return form.value.operatingPointType! as OperatingPointTechnicalTimetableType;
    }
    return undefined;
  }

  private static getOperatingPointType(form: FormGroup<ServicePointDetailFormGroup>) {
    if (
      Object.values(OperatingPointType).includes(
        form.value.operatingPointType as OperatingPointType,
      )
    ) {
      return form.value.operatingPointType! as OperatingPointType;
    }
    return undefined;
  }
}
