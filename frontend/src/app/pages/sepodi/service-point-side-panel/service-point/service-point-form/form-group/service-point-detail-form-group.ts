import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  Category,
  Country,
  CreateServicePointVersion,
  OperatingPointTechnicalTimetableType,
  OperatingPointType,
  ReadServicePointVersion,
  Status,
} from '../../../../../../api';
import moment from 'moment';
import { AtlasFieldLengthValidator } from '../../../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../../../../core/validation/date-range/date-range-validator';
import {
  GeographyFormGroup,
  GeographyFormGroupBuilder,
} from '../../../../geography/geography-form-group';
import { ServicePointType } from '../../service-point-type';
import { Moment } from 'moment/moment';
import { filter, takeUntil } from 'rxjs/operators';
import { mergeWith, Observable, Subject } from 'rxjs';
import {
  addControlToFormNoEvent,
  removeControlFromFormNoEvent,
} from '../../../../../../core/util/forms';
import {
  RouteNetworkFormGroup,
  RouteNetworkGroup,
} from './route-network-form-group';
import { StationFormGroup, StationGroup } from './station-form-group';

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

interface ValidityGroup {
  validFrom: FormControl<Moment | null>;
  validTo: FormControl<Moment | null>;
}

export interface OperatingPointGroup {
  operatingPointType: FormControl<string | undefined>;
}

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

    formGroup.controls.selectedType.valueChanges
      .pipe(
        takeUntil(formDestroy$),
        filter(() => formGroup.controls.selectedType.dirty)
      )
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
    removeControlFromFormNoEvent(formGroup, 'spTypeGroup');
    removeControlFromFormNoEvent(formGroup, 'routeNetworkGroup');
    switch (selectedType) {
      case 'OPERATING_POINT': {
        addControlToFormNoEvent(
          formGroup,
          'spTypeGroup',
          this.operatingGroup(version)
        );
        addControlToFormNoEvent(
          formGroup,
          'routeNetworkGroup',
          RouteNetworkFormGroup.routeNetworkGroup(selectedTypeDestroy$, version)
        );
        break;
      }
      case 'STOP_POINT': {
        const stationGroup = StationFormGroup.stationGroup(
          formGroup,
          selectedTypeDestroy$,
          version
        );
        addControlToFormNoEvent(formGroup, 'spTypeGroup', stationGroup);
        if (stationGroup.controls.stopPoint.value) {
          addControlToFormNoEvent(
            stationGroup,
            'stopPointGroup',
            StationFormGroup.stopPointGroup(version)
          );
        }
        if (stationGroup.controls.freightServicePoint.value) {
          addControlToFormNoEvent(
            stationGroup,
            'freightPointGroup',
            StationFormGroup.freightPointGroup(
              formGroup,
              selectedTypeDestroy$,
              version
            )
          );
        }
        addControlToFormNoEvent(
          formGroup,
          'routeNetworkGroup',
          RouteNetworkFormGroup.routeNetworkGroup(selectedTypeDestroy$, version)
        );
        break;
      }
    }
  }

  private static operatingGroup(version?: ReadServicePointVersion) {
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

      RouteNetworkFormGroup.mapper.mapRouteNetwork(
        routeNetworkGroupControls,
        writableForm
      );
      this.mapGeolocation(formControls, writableForm);

      const spTypeControls = formControls.spTypeGroup?.controls;
      this.mapOperatingPoint(spTypeControls, writableForm);
      StationFormGroup.mapper.mapStation(spTypeControls, writableForm);

      return writableForm;
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
  };
}
