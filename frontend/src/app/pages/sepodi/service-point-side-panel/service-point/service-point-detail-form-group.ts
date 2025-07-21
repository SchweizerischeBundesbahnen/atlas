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
  operatingPointKilometerMaster: FormControl<number | undefined>;
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

type BuiltFormGroup = {
  cleanupFn: () => void;
  group: FormGroup<ServicePointDetailFormGroup>;
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
    form.addControl(controlName, group, { emitEvent: false });
  }

  static removeGroupFromForm<
    T extends {
      [K in keyof T]: AbstractControl;
    },
  >(
    form: FormGroup<T>,
    controlName: {
      [K in keyof T]-?: undefined extends T[K] ? K : never;
    }[keyof T] &
      string
  ) {
    form.removeControl(controlName, { emitEvent: false });
  }

  static buildEmptyFormGroup(): BuiltFormGroup {
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

    return {
      cleanupFn: this.handleServicePointTypes(formGroup),
      group: formGroup,
    };
  }

  static buildFormGroup(version: ReadServicePointVersion): BuiltFormGroup {
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

    if (version.servicePointGeolocation?.spatialReference) {
      formGroup.addControl(
        'servicePointGeolocation',
        GeographyFormGroupBuilder.buildFormGroup(
          version.servicePointGeolocation
        )
      );
    }

    const cleanupFn = this.handleServicePointTypes(formGroup, version);
    formGroup.disable({ emitEvent: false });

    return {
      cleanupFn,
      group: formGroup,
    };
  }

  private static handleServicePointTypes(
    formGroup: FormGroup<ServicePointDetailFormGroup>,
    version?: ReadServicePointVersion
  ) {
    let currentSelectedType = formGroup.controls.selectedType.value;
    let cleanupFn = () => {};

    const selectedSPTypeTransition = {
      [ServicePointType.StopPoint]: () => {
        const emptyStationGroup = this.emptyStationGroup(formGroup, version);
        this.addGroupToForm(formGroup, 'spTypeGroup', emptyStationGroup.group);
        const routeNetworkGroup = this.routeNetworkGroup(version);
        this.addGroupToForm(
          formGroup,
          'routeNetworkGroup',
          routeNetworkGroup.group
        );
        cleanupFn = () => {
          emptyStationGroup.cleanupFn();
          routeNetworkGroup.cleanupFn();
          this.removeGroupFromForm(formGroup, 'spTypeGroup');
          this.removeGroupFromForm(formGroup, 'routeNetworkGroup');
        };
      },
      [ServicePointType.OperatingPoint]: () => {
        this.addGroupToForm(
          formGroup,
          'spTypeGroup',
          this.emptyOperatingGroup(version)
        );
        const routeNetworkGroup = this.routeNetworkGroup(version);
        this.addGroupToForm(
          formGroup,
          'routeNetworkGroup',
          routeNetworkGroup.group
        );
        cleanupFn = () => {
          routeNetworkGroup.cleanupFn();
          this.removeGroupFromForm(formGroup, 'spTypeGroup');
          this.removeGroupFromForm(formGroup, 'routeNetworkGroup');
        };
      },
      [ServicePointType.ServicePoint]: () => {},
      [ServicePointType.FareStop]: () => {},
    };

    if (
      formGroup.controls.selectedType.value === ServicePointType.OperatingPoint
    ) {
      selectedSPTypeTransition[ServicePointType.OperatingPoint]();
    } else if (
      formGroup.controls.selectedType.value === ServicePointType.StopPoint
    ) {
      const stationGroup = this.emptyStationGroup(formGroup, version);
      this.addGroupToForm(formGroup, 'spTypeGroup', stationGroup.group);
      if (stationGroup.group.controls.stopPoint.value) {
        this.addGroupToForm(
          stationGroup.group,
          'stopPointGroup',
          this.emptyStopPointGroup(version)
        );
      }
      let freightPointGroup:
        | ReturnType<typeof this.emptyFreightPointGroup>
        | undefined;
      if (stationGroup.group.controls.freightServicePoint.value) {
        freightPointGroup = this.emptyFreightPointGroup(formGroup, version);
        this.addGroupToForm(
          stationGroup.group,
          'freightPointGroup',
          freightPointGroup.group
        );
      }
      const routeNetworkGroup = this.routeNetworkGroup(version);
      this.addGroupToForm(
        formGroup,
        'routeNetworkGroup',
        routeNetworkGroup.group
      );

      cleanupFn = () => {
        stationGroup.cleanupFn();
        routeNetworkGroup.cleanupFn();
        freightPointGroup?.cleanupFn();
        this.removeGroupFromForm(formGroup, 'spTypeGroup');
        this.removeGroupFromForm(formGroup, 'routeNetworkGroup');
      };
    }

    const selectedTypeSub =
      formGroup.controls.selectedType.valueChanges.subscribe((selectedType) => {
        if (selectedType === currentSelectedType || !selectedType) return;
        cleanupFn();
        cleanupFn = () => {};
        selectedSPTypeTransition[selectedType]();
      });

    return () => {
      selectedTypeSub.unsubscribe();
      cleanupFn();
    };
  }

  private static emptyOperatingGroup(version?: ReadServicePointVersion) {
    return new FormGroup<OperatingPointGroup>({
      operatingPointType: new FormControl(
        version?.operatingPointType ??
          version?.operatingPointTechnicalTimetableType,
        {
          nonNullable: true,
          validators: Validators.required,
        }
      ),
    });
  }

  private static emptyStationGroup(
    formGroup: FormGroup<ServicePointDetailFormGroup>,
    version?: ReadServicePointVersion
  ) {
    const stationGroup = new FormGroup<StationGroup>(
      {
        stopPoint: new FormControl(!!version?.stopPoint, {
          nonNullable: true,
        }),
        freightServicePoint: new FormControl(!!version?.freightServicePoint, {
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
            this.emptyStopPointGroup(version)
          );
        } else {
          this.removeGroupFromForm(stationGroup, 'stopPointGroup');
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
            emptyFreightPointGroup = this.emptyFreightPointGroup(
              formGroup,
              version
            );
            this.addGroupToForm(
              stationGroup,
              'freightPointGroup',
              emptyFreightPointGroup.group
            );
          } else {
            emptyFreightPointGroup?.cleanupFn();
            this.removeGroupFromForm(stationGroup, 'freightPointGroup');
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

  private static emptyStopPointGroup(version?: ReadServicePointVersion) {
    return new FormGroup<StopPointGroup>({
      stopPointType: new FormControl(version?.stopPointType, {
        nonNullable: true,
        validators: stopPointTypeRequiredValidator,
      }),
      meansOfTransport: new FormControl(version?.meansOfTransport ?? [], {
        nonNullable: true,
        validators: Validators.required,
      }),
    });
  }

  private static emptyFreightPointGroup(
    formGroup: FormGroup<ServicePointDetailFormGroup>,
    version?: ReadServicePointVersion
  ) {
    const isRequired =
      formGroup.controls.validityGroup.controls.validFrom.value?.isSameOrAfter(
        moment(),
        'day'
      ) && formGroup.controls.country.value === 'SWITZERLAND';
    const freightPointGroup = new FormGroup<FreightPointGroup>({
      sortCodeOfDestinationStation: new FormControl(
        version?.sortCodeOfDestinationStation,
        {
          nonNullable: true,
          validators: isRequired
            ? [Validators.maxLength(5), Validators.required]
            : Validators.maxLength(5),
        }
      ),
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
      routeNetworkGroup.controls.operatingPointRouteNetwork.valueChanges
        .pipe(
          filter(
            () => routeNetworkGroup.controls.operatingPointRouteNetwork.dirty
          )
        )
        .subscribe((value) => {
          if (value) {
            routeNetworkGroup.controls.operatingPointKilometer.setValue(true, {
              emitEvent: false,
            });
            routeNetworkGroup.controls.operatingPointKilometer.disable({
              emitEvent: false,
            });
            routeNetworkGroup.controls.operatingPointKilometerMaster.setValue(
              version?.number.number,
              { emitEvent: false }
            );
            routeNetworkGroup.controls.operatingPointKilometerMaster.disable({
              emitEvent: false,
            });
          } else {
            routeNetworkGroup.controls.operatingPointKilometer.setValue(false);
            routeNetworkGroup.controls.operatingPointKilometer.enable({
              emitEvent: false,
            });
            routeNetworkGroup.controls.operatingPointKilometerMaster.reset(
              {
                value: undefined,
                disabled: false,
              },
              { emitEvent: false }
            );
          }
        });

    const kilometerSub =
      routeNetworkGroup.controls.operatingPointKilometer.valueChanges.subscribe(
        (value) => {
          if (!value) {
            routeNetworkGroup.controls.operatingPointKilometerMaster.reset(
              {
                value: undefined,
                disabled: false,
              },
              { emitEvent: false }
            );
          }
        }
      );

    const operatingPointKilometerStatusSub =
      routeNetworkGroup.controls.operatingPointKilometer.statusChanges
        .pipe(
          map(() => routeNetworkGroup.controls.operatingPointKilometer.enabled),
          distinctUntilChanged(),
          filter((enabled) => enabled)
        )
        .subscribe(() => {
          if (routeNetworkGroup.controls.operatingPointRouteNetwork.value) {
            routeNetworkGroup.controls.operatingPointKilometer.disable({
              emitEvent: false,
            });
          }
        });

    const operatingPointKilometerMasterStatusSub =
      routeNetworkGroup.controls.operatingPointKilometerMaster.statusChanges
        .pipe(
          map(
            () =>
              routeNetworkGroup.controls.operatingPointKilometerMaster.enabled
          ),
          distinctUntilChanged(),
          filter((enabled) => enabled)
        )
        .subscribe(() => {
          if (routeNetworkGroup.controls.operatingPointRouteNetwork.value) {
            routeNetworkGroup.controls.operatingPointKilometerMaster.disable({
              emitEvent: false,
            });
          }
        });

    return {
      group: routeNetworkGroup,
      cleanupFn: () => {
        routeNetworkSub.unsubscribe();
        kilometerSub.unsubscribe();
        operatingPointKilometerStatusSub.unsubscribe();
        operatingPointKilometerMasterStatusSub.unsubscribe();
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

  static mapper = class Mapper {
    static getWritableServicePoint(
      form: FormGroup<ServicePointDetailFormGroup>
    ): CreateServicePointVersion {
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

      const routeNetworkGroupControls =
        formControls.routeNetworkGroup?.controls;

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

      this.mapRouteNetworkControls(routeNetworkGroupControls, writableForm);
      this.mapGeolocation(formControls, writableForm);

      const spTypeControls = formControls.spTypeGroup?.controls;
      this.mapOperatingPoint(spTypeControls, writableForm);
      this.mapStation(spTypeControls, writableForm);

      return writableForm;
    }

    private static mapStation(
      spTypeControls: StationGroup | OperatingPointGroup | undefined,
      writableForm: CreateServicePointVersion
    ) {
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
    }

    private static mapOperatingPoint(
      spTypeControls: StationGroup | OperatingPointGroup | undefined,
      writableForm: CreateServicePointVersion
    ) {
      if (
        spTypeControls &&
        'operatingPointType' in spTypeControls &&
        spTypeControls.operatingPointType.value
      ) {
        const operatingPointType = spTypeControls.operatingPointType.value;
        writableForm.operatingPointType = Object.values(
          OperatingPointType
        ).find((value) => value === operatingPointType);

        writableForm.operatingPointTechnicalTimetableType = Object.values(
          OperatingPointTechnicalTimetableType
        ).find((value) => value === operatingPointType);
      }
    }

    private static mapGeolocation(
      formControls: ServicePointDetailFormGroup,
      writableForm: CreateServicePointVersion
    ) {
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
    }

    private static mapRouteNetworkControls(
      routeNetworkGroupControls: RouteNetworkGroup | undefined,
      writableForm: CreateServicePointVersion
    ) {
      if (
        routeNetworkGroupControls?.operatingPointRouteNetwork.value === false
      ) {
        writableForm.operatingPointKilometerMasterNumber =
          routeNetworkGroupControls.operatingPointKilometerMaster.value;
      }
    }
  };
}

// todo: check if bulkimport logs work with new feature, check e2e tests
