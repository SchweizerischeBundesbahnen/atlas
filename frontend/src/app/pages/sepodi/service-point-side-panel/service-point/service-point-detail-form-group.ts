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
import { AtLeastOneValidator } from '../../../../core/validation/boolean-cross-validator/at-least-one-validator';
import { filter, map } from 'rxjs/operators';
import { distinctUntilChanged } from 'rxjs';

export interface ServicePointDetailFormGroup {
  country: FormControl<Country | null>;
  number: FormControl<number | undefined>;
  abbreviation: FormControl<string | undefined>;
  status: FormControl<Status | null | undefined>;
  designationOfficial: FormControl<string | undefined>;
  designationLong: FormControl<string | undefined>;
  businessOrganisation: FormControl<string | undefined>;
  categories: FormControl<Array<Category> | undefined>;
  servicePointGeolocation?: FormGroup<GeographyFormGroup>;
  selectedType: FormControl<ServicePointType | undefined>;
  validityGroup: FormGroup<ValidityGroup>;
  spTypeGroup?: FormGroup<StationGroup | OperatingPointGroup>;
  routeNetworkGroup?: FormGroup<RouteNetworkGroup>;
}

interface RouteNetworkGroup {
  operatingPointRouteNetwork: FormControl<boolean>;
  operatingPointKilometer: FormControl<boolean>;
  // todo: disable if  routeNetwork=true (pay attention on
  //   parentForm.enable calls, maybe work with statusChanges obs)
  operatingPointKilometerMaster: FormControl<number | undefined>; // todo: disable if routeNetwork=true
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
  freightServicePoint: FormControl<boolean>;
  stopPointGroup?: FormGroup<StopPointGroup>;
  freightPointGroup?: FormGroup<FreightPointGroup>;
}

interface StopPointGroup {
  stopPointType: FormControl<StopPointType | undefined>;
  meansOfTransport: FormControl<MeanOfTransport[] | undefined>;
}

interface FreightPointGroup {
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
  static addGroupToForm<
    T extends {
      [K in keyof T]: AbstractControl;
    },
  >(
    form: FormGroup<T>,
    controlName: string & keyof T,
    group: Required<T>[string & keyof T]
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
    const formGroup = new FormGroup<ServicePointDetailFormGroup>({
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
          Validators.maxLength(6),
          AtlasCharsetsValidator.uppercaseNumeric,
        ],
      }),
      status: new FormControl(),
      designationLong: new FormControl(undefined, {
        nonNullable: true,
        validators: [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(50),
          Validators.minLength(2),
        ],
      }),
      businessOrganisation: new FormControl(undefined, {
        nonNullable: true,
        validators: [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ],
      }),
      categories: new FormControl([], { nonNullable: true }),
      selectedType: new FormControl(undefined, {
        nonNullable: true,
        validators: Validators.required,
      }),
      validityGroup: new FormGroup<ValidityGroup>(
        {
          validFrom: new FormControl(null, [Validators.required]),
          validTo: new FormControl(null, [Validators.required]),
        },
        DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')
      ),
    });

    this.handleServicePointTypes(formGroup);

    return formGroup;
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
      categories: new FormControl(version.categories, { nonNullable: true }),
      selectedType: new FormControl(this.determineType(version), {
        nonNullable: true,
      }),
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
      this.addGroupToForm(
        formGroup,
        'routeNetworkGroup',
        this.routeNetworkGroup(version).group
      );
    } else if (
      formGroup.controls.selectedType.value === ServicePointType.StopPoint
    ) {
      const stationGroup = new FormGroup<StationGroup>({
        stopPoint: new FormControl(!!version.stopPoint, {
          nonNullable: true,
        }),
        freightServicePoint: new FormControl(!!version.freightServicePoint, {
          nonNullable: true,
        }),
      });
      this.addGroupToForm(formGroup, 'spTypeGroup', stationGroup);

      if (stationGroup.controls.stopPoint.value) {
        const stopPointGroup = new FormGroup<StopPointGroup>({
          stopPointType: new FormControl(version.stopPointType, {
            nonNullable: true,
          }),
          meansOfTransport: new FormControl(version.meansOfTransport, {
            nonNullable: true,
          }),
        });
        this.addGroupToForm(stationGroup, 'stopPointGroup', stopPointGroup);
      }
      if (stationGroup.controls.freightServicePoint.value) {
        const usePointGroup = new FormGroup<FreightPointGroup>({
          sortCodeOfDestinationStation: new FormControl(
            version.sortCodeOfDestinationStation,
            { nonNullable: true }
          ),
        });
        this.addGroupToForm(stationGroup, 'freightPointGroup', usePointGroup);
      }
      this.addGroupToForm(
        formGroup,
        'routeNetworkGroup',
        this.routeNetworkGroup(version).group
      );
    } // todo: cleanup

    if (version.servicePointGeolocation?.spatialReference) {
      formGroup.addControl(
        'servicePointGeolocation',
        GeographyFormGroupBuilder.buildFormGroup(
          version.servicePointGeolocation
        )
      );
    }

    this.handleServicePointTypes(formGroup); // todo: think i need to give version, for filling the kilometermaster when i
    // switch the selectedtype first, and make it everywhere the same => integrate above methods into empty...() and give
    // version

    return formGroup;
  }

  private static handleServicePointTypes(
    formGroup: FormGroup<ServicePointDetailFormGroup>
  ) {
    let cleanupFun = () => {
      formGroup.removeControl('spTypeGroup'); // todo: cleanup also existing subs
      formGroup.removeControl('routeNetworkGroup');
    };

    const selectedSPTypeTransition = {
      [ServicePointType.StopPoint]: () => {
        const emptyStationGroup = this.emptyStationGroup(formGroup);
        this.addGroupToForm(formGroup, 'spTypeGroup', emptyStationGroup.group);
        const routeNetworkGroup = this.routeNetworkGroup();
        this.addGroupToForm(
          formGroup,
          'routeNetworkGroup',
          routeNetworkGroup.group
        );
        cleanupFun = () => {
          emptyStationGroup.cleanupFn();
          routeNetworkGroup.cleanupFn();
          formGroup.removeControl('spTypeGroup');
          formGroup.removeControl('routeNetworkGroup');
        };
      },
      [ServicePointType.OperatingPoint]: () => {
        this.addGroupToForm(
          formGroup,
          'spTypeGroup',
          this.emptyOperatingGroup()
        );
        const routeNetworkGroup = this.routeNetworkGroup();
        this.addGroupToForm(
          formGroup,
          'routeNetworkGroup',
          routeNetworkGroup.group
        );
        cleanupFun = () => {
          routeNetworkGroup.cleanupFn();
          formGroup.removeControl('spTypeGroup');
          formGroup.removeControl('routeNetworkGroup');
        };
      },
      [ServicePointType.ServicePoint]: () => {},
      [ServicePointType.FareStop]: () => {},
    };

    formGroup.controls.selectedType.valueChanges.subscribe((selectedType) => {
      if (!selectedType)
        throw 'IllegalState: value of servicePointType should not change to undefined';
      cleanupFun();
      cleanupFun = () => {};
      selectedSPTypeTransition[selectedType]();
    });
  }

  private static emptyOperatingGroup() {
    return new FormGroup<OperatingPointGroup>({
      operatingPointType: new FormControl(undefined, {
        nonNullable: true,
        validators: Validators.required,
      }),
    });
  }

  private static emptyStationGroup(
    formGroup: FormGroup<ServicePointDetailFormGroup>
  ) {
    const stationGroup = new FormGroup<StationGroup>(
      {
        stopPoint: new FormControl(false, {
          nonNullable: true,
        }),
        freightServicePoint: new FormControl(false, {
          nonNullable: true,
        }),
      },
      AtLeastOneValidator.of('stopPoint', 'freightServicePoint')
    );

    const stopPointSub = stationGroup.controls.stopPoint.valueChanges.subscribe(
      (value) => {
        if (value) {
          this.addGroupToForm(
            stationGroup,
            'stopPointGroup',
            this.emptyStopPointGroup()
          );
        } else {
          stationGroup.removeControl('stopPointGroup');
        }
      }
    );

    let emptyFreightPointGroup:
      | ReturnType<typeof this.emptyFreightPointGroup>
      | undefined;
    const freightPointSub =
      stationGroup.controls.freightServicePoint.valueChanges.subscribe(
        (value) => {
          if (value) {
            emptyFreightPointGroup = this.emptyFreightPointGroup(formGroup);
            this.addGroupToForm(
              stationGroup,
              'freightPointGroup',
              emptyFreightPointGroup.group
            );
          } else {
            emptyFreightPointGroup?.cleanupFn();
            stationGroup.removeControl('freightPointGroup');
          }
        }
      );

    return {
      group: stationGroup,
      cleanupFn: () => {
        stopPointSub.unsubscribe();
        freightPointSub.unsubscribe();
        emptyFreightPointGroup?.cleanupFn();
      },
    };
  }

  private static emptyStopPointGroup() {
    return new FormGroup<StopPointGroup>({
      stopPointType: new FormControl(undefined, {
        nonNullable: true,
        validators: stopPointTypeRequiredValidator,
      }),
      meansOfTransport: new FormControl([], {
        nonNullable: true,
        validators: Validators.required,
      }),
    });
  }

  private static emptyFreightPointGroup(
    formGroup: FormGroup<ServicePointDetailFormGroup>
  ) {
    const isRequired =
      formGroup.controls.validityGroup.controls.validFrom.value?.isSameOrAfter(
        moment(),
        'day'
      ) && formGroup.controls.country.value === 'SWITZERLAND';
    const freightPointGroup = new FormGroup<FreightPointGroup>({
      sortCodeOfDestinationStation: new FormControl(undefined, {
        nonNullable: true,
        validators: isRequired
          ? [Validators.maxLength(5), Validators.required]
          : Validators.maxLength(5),
      }),
    });

    const countrySub = formGroup.controls.country.valueChanges.subscribe(
      (country) => {
        if (
          country === 'SWITZERLAND' &&
          formGroup.controls.validityGroup.controls.validFrom.value?.isSameOrAfter(
            moment(),
            'day'
          )
        ) {
          freightPointGroup.controls.sortCodeOfDestinationStation.addValidators(
            Validators.required
          );
        } else {
          freightPointGroup.controls.sortCodeOfDestinationStation.removeValidators(
            Validators.required
          );
        }
        freightPointGroup.controls.sortCodeOfDestinationStation.updateValueAndValidity();
      }
    );

    const validFromSub =
      formGroup.controls.validityGroup.controls.validFrom.valueChanges.subscribe(
        (validFrom) => {
          if (
            validFrom?.isSameOrAfter(moment(), 'day') &&
            formGroup.controls.country.value === 'SWITZERLAND'
          ) {
            freightPointGroup.controls.sortCodeOfDestinationStation.addValidators(
              Validators.required
            );
          } else {
            freightPointGroup.controls.sortCodeOfDestinationStation.removeValidators(
              Validators.required
            );
          }
          freightPointGroup.controls.sortCodeOfDestinationStation.updateValueAndValidity();
        }
      );

    return {
      group: freightPointGroup,
      cleanupFn: () => {
        countrySub.unsubscribe();
        validFromSub.unsubscribe();
      },
    };
  }

  private static routeNetworkGroup(version?: ReadServicePointVersion) {
    const routeNetworkGroup = new FormGroup<RouteNetworkGroup>({
      operatingPointRouteNetwork: new FormControl(
        version?.operatingPointRouteNetwork ?? false,
        { nonNullable: true }
      ),
      operatingPointKilometer: new FormControl(
        version?.operatingPointKilometer ?? false,
        { nonNullable: true }
      ),
      operatingPointKilometerMaster: new FormControl(
        version?.operatingPointKilometerMaster?.number,
        {
          nonNullable: true,
        }
      ),
    });

    const routeNetworkSub =
      routeNetworkGroup.controls.operatingPointRouteNetwork.valueChanges.subscribe(
        (value) => {
          if (value) {
            routeNetworkGroup.controls.operatingPointKilometer.setValue(true);
            routeNetworkGroup.controls.operatingPointKilometer.disable();
            routeNetworkGroup.controls.operatingPointKilometerMaster.setValue(
              version?.number.number
            );
            routeNetworkGroup.controls.operatingPointKilometerMaster.disable();
          } else {
            routeNetworkGroup.controls.operatingPointKilometer.setValue(false);
            routeNetworkGroup.controls.operatingPointKilometer.enable();
            routeNetworkGroup.controls.operatingPointKilometerMaster.reset({
              value: undefined,
              disabled: false,
            });
          }
        }
      );

    const kilometerSub =
      routeNetworkGroup.controls.operatingPointKilometer.valueChanges.subscribe(
        (value) => {
          if (!value) {
            routeNetworkGroup.controls.operatingPointKilometerMaster.reset({
              value: undefined,
              disabled: false,
            });
          }
        }
      );

    const statusSub = routeNetworkGroup.statusChanges
      .pipe(
        map(() => routeNetworkGroup.enabled),
        distinctUntilChanged(),
        filter((enabled) => enabled)
      )
      .subscribe(() => {
        if (routeNetworkGroup.controls.operatingPointRouteNetwork.value) {
          routeNetworkGroup.controls.operatingPointKilometer.disable();
          routeNetworkGroup.controls.operatingPointKilometerMaster.disable();
        }
      });

    return {
      group: routeNetworkGroup,
      cleanupFn: () => {
        routeNetworkSub.unsubscribe();
        kilometerSub.unsubscribe();
        statusSub.unsubscribe();
      },
    };
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

  static getWritableServicePoint(
    form: FormGroup<ServicePointDetailFormGroup>
  ): CreateServicePointVersion {
    console.log(form);
    const formControls = form.controls;
    const validityGroupControls = formControls.validityGroup.controls;
    if (
      !formControls.country.value ||
      !formControls.designationOfficial.value ||
      !formControls.businessOrganisation.value ||
      !validityGroupControls.validFrom.value ||
      !validityGroupControls.validTo.value
    ) {
      throw 'required fields are not defined';
    }

    const routeNetworkGroupControls = formControls.routeNetworkGroup?.controls;
    const writableForm: CreateServicePointVersion = {
      country: formControls.country.value,
      numberShort: formControls.number.value,
      designationOfficial: formControls.designationOfficial.value,
      designationLong: formControls.designationLong.value,
      abbreviation: formControls.abbreviation.value,
      businessOrganisation: formControls.businessOrganisation.value,
      categories: formControls.categories.value,
      operatingPointRouteNetwork:
        routeNetworkGroupControls?.operatingPointRouteNetwork.value,
      validFrom: validityGroupControls.validFrom.value.toDate(),
      validTo: validityGroupControls.validTo.value.toDate(),
      operatingPointTrafficPointType:
        formControls.selectedType.value === 'FARE_STOP'
          ? 'TARIFF_POINT'
          : undefined,
    };

    if (routeNetworkGroupControls?.operatingPointRouteNetwork.value === false) {
      console.log('ok');
      writableForm.operatingPointKilometerMasterNumber =
        routeNetworkGroupControls.operatingPointKilometerMaster.value;
    }

    const spgControls = formControls.servicePointGeolocation?.controls;
    if (
      spgControls?.spatialReference.value &&
      spgControls.north.value &&
      spgControls.east.value
    ) {
      writableForm.servicePointGeolocation = {
        spatialReference: spgControls.spatialReference.value,
        north: spgControls.north.value,
        east: spgControls.east.value,
        height: spgControls.height.value,
      };
    }

    const spTypeControls = formControls.spTypeGroup?.controls;
    if (
      spTypeControls &&
      'operatingPointType' in spTypeControls &&
      spTypeControls.operatingPointType.value
    ) {
      console.log('hhäää');
      const operatingPointType = spTypeControls.operatingPointType.value;
      writableForm.operatingPointType = Object.values(OperatingPointType).find(
        (value) => value === operatingPointType
      );

      writableForm.operatingPointTechnicalTimetableType = Object.values(
        OperatingPointTechnicalTimetableType
      ).find((value) => value === operatingPointType);
    }

    if (spTypeControls && 'stopPoint' in spTypeControls) {
      writableForm.freightServicePoint =
        spTypeControls.freightServicePoint.value;
      if (spTypeControls.freightPointGroup) {
        writableForm.sortCodeOfDestinationStation =
          spTypeControls.freightPointGroup.controls.sortCodeOfDestinationStation.value;
      }

      if (spTypeControls.stopPointGroup) {
        writableForm.meansOfTransport =
          spTypeControls.stopPointGroup.controls.meansOfTransport.value;
        writableForm.stopPointType =
          spTypeControls.stopPointGroup.controls.stopPointType.value;
      }
    }

    return writableForm;
  } // todo: cleanup
}

// todo: make kilometermasternumber required when bpk is checked

// todo: check if bulkimport logs work with new feature
