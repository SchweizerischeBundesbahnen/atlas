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
import { filter, map, takeUntil } from 'rxjs/operators';
import { distinctUntilChanged, mergeWith, Observable, Subject } from 'rxjs';
import {
  addGroupToForm,
  removeGroupFromForm,
} from '../../../../core/util/forms';

export interface ServicePointDetailFormGroup {
  country: FormControl<Country | null>;
  number: FormControl<number | undefined>;
  abbreviation: FormControl<string | undefined>;
  status: FormControl<Status | undefined>;
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

export class ServicePointFormGroupBuilder {
  static buildEmptyFormGroup(formDestroy$: Observable<void>) {
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

    this.handleServicePointTypes(formGroup, formDestroy$);
    return formGroup;
  }

  static buildFormGroup(
    version: ReadServicePointVersion,
    formDestroy$: Observable<void>
  ) {
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
      status: new FormControl(version.status, { nonNullable: true }),
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

    this.handleServicePointTypes(formGroup, formDestroy$, version);
    formGroup.disable({ emitEvent: false });

    return formGroup;
  }

  private static handleServicePointTypes(
    formGroup: FormGroup<ServicePointDetailFormGroup>,
    formDestroy$: Observable<void>,
    version?: ReadServicePointVersion
  ) {
    const selectedTypeSwitch$ = new Subject<void>();
    const selectedTypeDestroy$ = selectedTypeSwitch$.pipe(
      takeUntil(formDestroy$),
      mergeWith(formDestroy$)
    );

    this.handleSelectedType(
      formGroup,
      selectedTypeDestroy$,
      version,
      formGroup.controls.selectedType.value
    );

    // todo: filter !pristine events
    formGroup.controls.selectedType.valueChanges
      .pipe(takeUntil(formDestroy$))
      .subscribe((selectedType) => {
        if (!selectedType) return;
        selectedTypeSwitch$.next();
        this.handleSelectedType(
          formGroup,
          selectedTypeDestroy$,
          version,
          selectedType
        );
      });
  }

  private static handleSelectedType(
    formGroup: FormGroup<ServicePointDetailFormGroup>,
    selectedTypeDestroy$: Observable<void>,
    version?: ReadServicePointVersion,
    selectedType?: ServicePointType
  ) {
    removeGroupFromForm(formGroup, 'spTypeGroup');
    removeGroupFromForm(formGroup, 'routeNetworkGroup');
    switch (selectedType) {
      case 'OPERATING_POINT': {
        addGroupToForm(
          formGroup,
          'spTypeGroup',
          this.emptyOperatingGroup(version)
        );
        const routeNetworkGroup = this.routeNetworkGroup(
          selectedTypeDestroy$,
          version
        );
        addGroupToForm(formGroup, 'routeNetworkGroup', routeNetworkGroup);
        break;
      }
      case 'STOP_POINT': {
        const stationGroup = this.emptyStationGroup(
          formGroup,
          selectedTypeDestroy$,
          version
        );
        addGroupToForm(formGroup, 'spTypeGroup', stationGroup);
        if (stationGroup.controls.stopPoint.value) {
          addGroupToForm(
            stationGroup,
            'stopPointGroup',
            this.emptyStopPointGroup(version)
          );
        }
        if (stationGroup.controls.freightServicePoint.value) {
          const freightPointGroup = this.emptyFreightPointGroup(
            formGroup,
            selectedTypeDestroy$,
            version
          );
          addGroupToForm(stationGroup, 'freightPointGroup', freightPointGroup);
        }
        const routeNetworkGroup = this.routeNetworkGroup(
          selectedTypeDestroy$,
          version
        );
        addGroupToForm(formGroup, 'routeNetworkGroup', routeNetworkGroup);
        break;
      }
      default: {
      }
    }
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
    destroy$: Observable<void>,
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

    const handleStopPointChecked = () => {
      addGroupToForm(
        stationGroup,
        'stopPointGroup',
        this.emptyStopPointGroup(version)
      );
    };

    const handleStopPointUnchecked = () => {
      removeGroupFromForm(stationGroup, 'stopPointGroup');
    };

    stationGroup.controls.stopPoint.valueChanges
      .pipe(takeUntil(destroy$))
      .subscribe((checked) =>
        checked ? handleStopPointChecked() : handleStopPointUnchecked()
      );

    const freightPointDestroy$ = new Subject<void>();
    const handleFreightPointChecked = () => {
      const emptyFreightPointGroup = this.emptyFreightPointGroup(
        formGroup,
        freightPointDestroy$.pipe(takeUntil(destroy$), mergeWith(destroy$)),
        version
      );
      addGroupToForm(stationGroup, 'freightPointGroup', emptyFreightPointGroup);
    };

    const handleFreightPointUnchecked = () => {
      freightPointDestroy$.next();
      removeGroupFromForm(stationGroup, 'freightPointGroup');
    };

    stationGroup.controls.freightServicePoint.valueChanges
      .pipe(takeUntil(destroy$))
      .subscribe((checked) =>
        checked ? handleFreightPointChecked() : handleFreightPointUnchecked()
      );

    return stationGroup;
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
    destroy$: Observable<void>,
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

    formGroup.controls.country.valueChanges
      .pipe(takeUntil(destroy$))
      .subscribe((country) => {
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
      });

    formGroup.controls.validityGroup.controls.validFrom.valueChanges
      .pipe(takeUntil(destroy$))
      .subscribe((validFrom) => {
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
      });

    return freightPointGroup;
  }

  private static routeNetworkGroup(
    destroy$: Observable<void>,
    version?: ReadServicePointVersion
  ) {
    // todo: define initial disabled state of operatingPointKilometer and operatingPointKilometerMaster
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

    const handleOperatingPointRouteNetworkChecked = () => {
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
    };

    const handleOperatingPointRouteNetworkUnchecked = () => {
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
    };

    routeNetworkGroup.controls.operatingPointRouteNetwork.valueChanges
      .pipe(
        takeUntil(destroy$),
        filter(
          () => routeNetworkGroup.controls.operatingPointRouteNetwork.dirty
        )
      )
      .subscribe((checked) =>
        checked
          ? handleOperatingPointRouteNetworkChecked()
          : handleOperatingPointRouteNetworkUnchecked()
      );

    routeNetworkGroup.controls.operatingPointKilometer.valueChanges
      .pipe(takeUntil(destroy$))
      .subscribe((value) => {
        if (!value) {
          routeNetworkGroup.controls.operatingPointKilometerMaster.reset(
            {
              value: undefined,
              disabled: false,
            },
            { emitEvent: false }
          );
        }
      });

    routeNetworkGroup.controls.operatingPointKilometer.statusChanges
      .pipe(
        takeUntil(destroy$),
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

    routeNetworkGroup.controls.operatingPointKilometerMaster.statusChanges
      .pipe(
        takeUntil(destroy$),
        map(
          () => routeNetworkGroup.controls.operatingPointKilometerMaster.enabled
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

    return routeNetworkGroup;
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

  static readonly mapper = class Mapper {
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
        throw Error('required fields are not defined');
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
        status: formControls.status.value,
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
