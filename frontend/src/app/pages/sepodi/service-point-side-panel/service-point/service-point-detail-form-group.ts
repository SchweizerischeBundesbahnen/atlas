import {
  AbstractControl,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
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
import { Moment } from 'moment/moment';

export interface ServicePointDetailFormGroup {
  country: FormControl<Country | null>;
  number: FormControl<number | undefined>;
  abbreviation: FormControl<string | undefined>;
  status: FormControl<Status | null | undefined>;
  designationOfficial: FormControl<string | undefined>;
  designationLong: FormControl<string | undefined>;
  businessOrganisation: FormControl<string | undefined>;
  // freightServicePoint: FormControl<boolean | null | undefined>;
  // stopPoint: FormControl<boolean | null | undefined>;

  categories: FormControl<Array<Category> | undefined>;
  operatingPointRouteNetwork: FormControl<boolean | undefined>;
  operatingPointKilometer: FormControl<boolean | null | undefined>;
  operatingPointKilometerMaster: FormControl<number | undefined>;
  // operatingPointTrafficPointType: FormControl<OperatingPointTrafficPointType | null | undefined>;
  // etagVersion: FormControl<number | null | undefined>;
  servicePointGeolocation?: FormGroup<GeographyFormGroup>;
  selectedType: FormControl<ServicePointType | undefined>;
  validityGroup: FormGroup<ValidityGroup>;
  spTypeGroup?: FormGroup<StationGroup | OperatingPointGroup>;
}

interface ValidityGroup {
  validFrom: FormControl<Moment | null>;
  validTo: FormControl<Moment | null>;
}

interface OperatingPointGroup {
  operatingPointType: FormControl<string | undefined>;
}

interface StationGroup {
  stopPoint: FormControl<boolean>;
  usePoint: FormControl<boolean>;
  stopPointGroup?: FormGroup<StopPointGroup>;
  usePointGroup?: FormGroup<UsePointGroup>;
}

interface StopPointGroup {
  stopPointType: FormControl<StopPointType | undefined>;
  meansOfTransport: FormControl<MeanOfTransport[] | undefined>;
}

interface UsePointGroup {
  sortCodeOfDestinationStation: FormControl<string | undefined>;
}

type OptionalKeysOfServicePointDetailFormGroup = {
  [K in keyof ServicePointDetailFormGroup]-?: undefined extends ServicePointDetailFormGroup[K]
    ? K
    : never;
}[keyof ServicePointDetailFormGroup];

const stopPointTypeRequiredValidator = (
  control: AbstractControl<StopPointType | null | undefined>
) => {
  if (!control.value || control.value === 'UNKNOWN') {
    return {
      requiredAndNotUnknown: true,
    };
  }
  return null;
};

export class ServicePointFormGroupBuilder {
  static addGroupToForm(
    form: FormGroup<ServicePointDetailFormGroup>,
    controlName: keyof ServicePointDetailFormGroup,
    group: FormGroup
  ) {
    form.addControl(controlName, group);
  }

  static removeGroupFromForm(
    form: FormGroup<ServicePointDetailFormGroup>,
    controlName: OptionalKeysOfServicePointDetailFormGroup
  ) {
    form.removeControl(controlName);
  }

  static buildEmptyFormGroup(): FormGroup<ServicePointDetailFormGroup> {
    // this.initConditionalValidators(formGroup);
    return new FormGroup<ServicePointDetailFormGroup>({
      number: new FormControl(
        { value: undefined, disabled: true },
        {
          nonNullable: true,
          validators: [
            Validators.min(1),
            Validators.max(99999),
            AtlasCharsetsValidator.numeric,
            Validators.required,
          ],
        }
      ),
      designationOfficial: new FormControl(undefined, {
        nonNullable: true,
        validators: [
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      }),
      country: new FormControl(null, [Validators.required]),
      abbreviation: new FormControl(undefined, {
        nonNullable: true,
        validators: [
          // todo: darf leer sein aber nicht nur whitespaces
          Validators.maxLength(6),
          Validators.minLength(1),
          AtlasCharsetsValidator.uppercaseNumeric,
        ],
      }),
      status: new FormControl(),
      designationLong: new FormControl(undefined, {
        nonNullable: true,
        validators: [
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      }),
      businessOrganisation: new FormControl(undefined, {
        nonNullable: true,
        validators: [
          // todo: why not required?
          Validators.maxLength(6),
          Validators.minLength(2),
          AtlasCharsetsValidator.uppercaseNumeric,
        ],
      }),
      // operatingPointType: new FormControl(),
      // sortCodeOfDestinationStation: new FormControl(null, [
      //   Validators.maxLength(5),
      // ]),
      // stopPointType: new FormControl(),
      // meansOfTransport: new FormControl([]),
      categories: new FormControl([], { nonNullable: true }),
      operatingPointRouteNetwork: new FormControl(undefined, {
        nonNullable: true,
      }),
      operatingPointKilometer: new FormControl(),
      operatingPointKilometerMaster: new FormControl(undefined, {
        nonNullable: true,
      }),
      selectedType: new FormControl(undefined, {
        nonNullable: true,
        validators: Validators.required,
      }),
      // freightServicePoint: new FormControl(),
      // stopPoint: new FormControl(),
      // operatingPointTrafficPointType: new FormControl(),
      validityGroup: new FormGroup<ValidityGroup>(
        {
          validFrom: new FormControl(null, [Validators.required]),
          validTo: new FormControl(null, [Validators.required]),
        },
        DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')
      ),

      // etagVersion: new FormControl(),
      // creationDate: new FormControl(),
      // editionDate: new FormControl(),
      // editor: new FormControl(),
      // creator: new FormControl(),
    });
    // todo: add dynamic formgroups
  }

  static buildFormGroup(version: ReadServicePointVersion): FormGroup {
    const formGroup = new FormGroup<ServicePointDetailFormGroup>({
      number: new FormControl(version.number.numberShort, {
        nonNullable: true,
      }),
      country: new FormControl(version.country),
      abbreviation: new FormControl(version.abbreviation, {
        nonNullable: true,
        validators: [
          Validators.maxLength(6),
          Validators.minLength(2),
          AtlasCharsetsValidator.uppercaseNumeric,
        ],
      }),
      status: new FormControl(version.status),
      designationOfficial: new FormControl(version.designationOfficial, {
        nonNullable: true,
        validators: [
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      }),
      designationLong: new FormControl(version.designationLong, {
        nonNullable: true,
        validators: [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(50),
          Validators.minLength(2),
        ],
      }),
      validityGroup: new FormGroup<ValidityGroup>(
        {
          validFrom: new FormControl(
            version.validFrom ? moment(version.validFrom) : version.validFrom,
            [Validators.required]
          ),
          validTo: new FormControl(
            version.validTo ? moment(version.validTo) : version.validTo,
            [Validators.required]
          ),
        },
        DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')
      ),
      businessOrganisation: new FormControl(version.businessOrganisation, {
        nonNullable: true,
        validators: [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ],
      }),
      // operatingPointType: new FormControl(
      //   version.operatingPointType ??
      //     version.operatingPointTechnicalTimetableType
      // ),
      // sortCodeOfDestinationStation: new FormControl(
      //   version.sortCodeOfDestinationStation,
      //   [Validators.maxLength(5)]
      // ),
      // stopPointType: new FormControl(version.stopPointType),
      // meansOfTransport: new FormControl(version.meansOfTransport),
      categories: new FormControl(version.categories, { nonNullable: true }),
      operatingPointRouteNetwork: new FormControl(
        version.operatingPointRouteNetwork,
        { nonNullable: true }
      ),
      operatingPointKilometer: new FormControl(version.operatingPointKilometer),
      operatingPointKilometerMaster: new FormControl(
        version.operatingPointKilometerMaster?.number,
        { nonNullable: true }
      ),
      selectedType: new FormControl(this.determineType(version), {
        nonNullable: true,
      }),
      // freightServicePoint: new FormControl(version.freightServicePoint),
      // stopPoint: new FormControl(version.stopPoint),
      // operatingPointTrafficPointType: new FormControl(version.operatingPointTrafficPointType),
      // etagVersion: new FormControl(version.etagVersion),
      // creationDate: new FormControl(version.creationDate),
      // editionDate: new FormControl(version.editionDate),
      // editor: new FormControl(version.editor),
      // creator: new FormControl(version.creator),
    });

    if (
      formGroup.controls.selectedType.value === ServicePointType.OperatingPoint
    ) {
      const operatingPointGroup = new FormGroup<OperatingPointGroup>({
        operatingPointType: new FormControl(
          version.operatingPointType ??
            version.operatingPointTechnicalTimetableType,
          { nonNullable: true }
        ),
      });
      this.addGroupToForm(formGroup, 'spTypeGroup', operatingPointGroup);
    }
    // todo: add StationGroup if selectedType is StopPoint

    if (version.servicePointGeolocation?.spatialReference) {
      formGroup.addControl(
        'servicePointGeolocation',
        GeographyFormGroupBuilder.buildFormGroup(
          version.servicePointGeolocation
        )
      );
    }

    // this.initConditionalValidators(formGroup);
    return formGroup;
  }

  private static determineType(version: ReadServicePointVersion) {
    if (
      version.operatingPointType ||
      version.operatingPointTechnicalTimetableType
    ) {
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

  /*private static initConditionalValidators(
    formGroup: FormGroup<ServicePointDetailFormGroup>
  ) {
    this.initSelectedTypeValidation(formGroup);
    this.initStopPointValidation(formGroup);
    this.initFreightServicePointValidation(formGroup);
  }*/

  /*private static initSelectedTypeValidation(
    formGroup: FormGroup<ServicePointDetailFormGroup>
  ) {
    formGroup.controls.selectedType.valueChanges.subscribe((newType) => {
      formGroup.controls.meansOfTransport.clearValidators();
      formGroup.controls.stopPointType.clearValidators();
      formGroup.controls.operatingPointType.clearValidators();
      formGroup.controls.sortCodeOfDestinationStation.clearValidators();

      if (newType === ServicePointType.OperatingPoint) {
        formGroup.controls.operatingPointType.setValidators([
          Validators.required,
        ]);
      } else {
        formGroup.controls.operatingPointType.clearValidators();
      }
      formGroup.controls.operatingPointType.updateValueAndValidity();

      if (newType === ServicePointType.StopPoint) {
        formGroup.addValidators(
          AtLeastOneValidator.of('stopPoint', 'freightServicePoint')
        );
      } else {
        console.log('test');
        formGroup.clearValidators();
        formGroup.controls.stopPoint.updateValueAndValidity();
        formGroup.controls.freightServicePoint.updateValueAndValidity();
        formGroup.updateValueAndValidity();
      }
    });
  }

  private static initStopPointValidation(
    formGroup: FormGroup<ServicePointDetailFormGroup>
  ) {
    if (formGroup.controls.stopPoint.value) {
      formGroup.controls.meansOfTransport.setValidators(Validators.required);
      formGroup.controls.stopPointType.setValidators(
        stopPointTypeRequiredValidator
      );
    }
    formGroup.controls.stopPoint.valueChanges.subscribe((isStopPoint) => {
      if (isStopPoint) {
        formGroup.controls.meansOfTransport.setValidators(Validators.required);
        formGroup.controls.stopPointType.setValidators(
          stopPointTypeRequiredValidator
        );
      } else {
        formGroup.controls.meansOfTransport.clearValidators();
        formGroup.controls.stopPointType.clearValidators();
      }
      formGroup.controls.meansOfTransport.updateValueAndValidity();
      formGroup.controls.stopPointType.updateValueAndValidity();
    });
  }*/

  /*private static initFreightServicePointValidation(
    formGroup: FormGroup<ServicePointDetailFormGroup>
  ) {
    formGroup.controls.freightServicePoint.valueChanges.subscribe(
      (isFreightServicePoint) => {
        if (isFreightServicePoint) {
          formGroup.controls.sortCodeOfDestinationStation.setValidators([
            Validators.maxLength(5),
          ]);
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
      }
    );
  }*/

  static getWritableServicePoint(
    form: FormGroup<ServicePointDetailFormGroup>
  ): CreateServicePointVersion {
    if (!form.controls.country.value) throw '';
    if (!form.controls.designationOfficial.value) throw '';
    if (!form.controls.businessOrganisation.value) throw '';
    if (!form.controls.validityGroup.controls.validFrom.value) throw '';
    if (!form.controls.validityGroup.controls.validTo.value) throw '';

    const writableForm: CreateServicePointVersion = {
      country: form.controls.country.value,
      numberShort: form.controls.number.value,
      designationOfficial: form.controls.designationOfficial.value,
      designationLong: form.controls.designationLong.value,
      abbreviation: form.controls.abbreviation.value, // todo: is empty string allowed
      businessOrganisation: form.controls.businessOrganisation.value,
      categories: form.controls.categories.value,
      operatingPointRouteNetwork:
        form.controls.operatingPointRouteNetwork.value,
      operatingPointKilometerMasterNumber: form.controls
        .operatingPointRouteNetwork.value
        ? undefined
        : form.controls.operatingPointKilometerMaster.value,
      // meansOfTransport: [],
      validFrom: form.controls.validityGroup.controls.validFrom.value.toDate(),
      validTo: form.controls.validityGroup.controls.validTo.value.toDate(),
      // etagVersion: value.etagVersion!,
      // creationDate: value.creationDate!,
      // editionDate: value.editionDate!,
      // editor: value.editor!,
      // creator: value.creator!,
    };

    if (
      form.controls.servicePointGeolocation?.controls.spatialReference.value
    ) {
      if (!form.controls.servicePointGeolocation.controls.north.value) throw '';
      if (!form.controls.servicePointGeolocation.controls.east.value) throw '';

      writableForm.servicePointGeolocation = {
        spatialReference:
          form.controls.servicePointGeolocation.controls.spatialReference.value,
        north: form.controls.servicePointGeolocation.controls.north.value,
        east: form.controls.servicePointGeolocation.controls.east.value,
        height: form.controls.servicePointGeolocation.controls.height.value,
      };
    }

    switch (form.controls.selectedType.value) {
      case 'OPERATING_POINT': {
        // writableForm.operatingPointType = this.getOperatingPointType(form);

        if (!form.controls.spTypeGroup) throw '';
        if (!('operatingPointType' in form.controls.spTypeGroup.controls))
          throw '';

        if (form.controls.spTypeGroup.controls.operatingPointType.value) {
          const operatingPointType =
            form.controls.spTypeGroup.controls.operatingPointType.value;
          writableForm.operatingPointType = Object.values(
            OperatingPointType
          ).find((value) => value === operatingPointType); // todo: test if it finds

          writableForm.operatingPointTechnicalTimetableType = Object.values(
            OperatingPointTechnicalTimetableType
          ).find((value) => value === operatingPointType); // todo: test if it finds
        }

        break;
      }
      case 'STOP_POINT': {
        if (!form.controls.spTypeGroup) throw '';
        if (!('stopPoint' in form.controls.spTypeGroup.controls)) throw '';

        if (form.controls.spTypeGroup.controls.stopPoint.value) {
          if (!form.controls.spTypeGroup.controls.stopPointGroup) throw '';
          writableForm.meansOfTransport =
            form.controls.spTypeGroup.controls.stopPointGroup.controls.meansOfTransport.value;
          writableForm.stopPointType =
            form.controls.spTypeGroup.controls.stopPointGroup.controls.stopPointType.value;
        }

        if (form.controls.spTypeGroup.controls.usePoint.value) {
          writableForm.freightServicePoint =
            form.controls.spTypeGroup.controls.usePoint.value;
          if (!form.controls.spTypeGroup.controls.usePointGroup) throw '';
          writableForm.sortCodeOfDestinationStation =
            form.controls.spTypeGroup.controls.usePointGroup.controls.sortCodeOfDestinationStation.value;
        }

        break;
      }
      case 'FARE_STOP': {
        writableForm.operatingPointKilometerMasterNumber = undefined; // todo: need to validate?
        writableForm.operatingPointRouteNetwork = false; // todo: need to validate?
        writableForm.operatingPointTrafficPointType =
          OperatingPointTrafficPointType.TariffPoint;
        break;
      }
      case 'SERVICE_POINT': {
        writableForm.operatingPointKilometerMasterNumber = undefined; // todo: need to validate?
        writableForm.operatingPointRouteNetwork = false; // todo: need to validate?
      }
    }

    return writableForm;
  }
}

// todo: check if bulkimport logs work with new feature
