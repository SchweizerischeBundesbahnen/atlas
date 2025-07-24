import {
  AbstractControl,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import {
  OperatingPointGroup,
  ServicePointDetailFormGroup,
} from './service-point-detail-form-group';
import { mergeWith, Observable, Subject } from 'rxjs';
import {
  CreateServicePointVersion,
  MeanOfTransport,
  ReadServicePointVersion,
  StopPointType,
} from '../../../../../../api';
import { AtLeastOneValidator } from '../../../../../../core/validation/boolean-cross-validator/at-least-one-validator';
import {
  addControlToFormNoEvent,
  removeControlFromFormNoEvent,
} from '../../../../../../core/util/forms';
import { takeUntil } from 'rxjs/operators';
import moment from 'moment/moment';

export interface StationGroup {
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

export class StationFormGroup {
  private static stopPointTypeRequiredValidator = (
    control: AbstractControl<StopPointType | null | undefined>
  ) => {
    if (!control.value || control.value === 'UNKNOWN') {
      return {
        requiredAndNotUnknown: true,
      };
    }
    return null;
  };

  static stationGroup(
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

    stationGroup.controls.stopPoint.valueChanges
      .pipe(takeUntil(destroy$))
      .subscribe((checked) =>
        checked
          ? this.handleStopPointChecked(stationGroup, version)
          : this.handleStopPointUnchecked(stationGroup)
      );

    const freightPointDestroy$ = new Subject<void>();
    stationGroup.controls.freightServicePoint.valueChanges
      .pipe(takeUntil(destroy$))
      .subscribe((checked) =>
        checked
          ? this.handleFreightPointChecked(
              stationGroup,
              formGroup,
              freightPointDestroy$,
              destroy$,
              version
            )
          : this.handleFreightPointUnchecked(freightPointDestroy$, stationGroup)
      );

    return stationGroup;
  }

  private static handleFreightPointUnchecked(
    freightPointDestroy$: Subject<void>,
    stationGroup: FormGroup<StationGroup>
  ) {
    freightPointDestroy$.next();
    removeControlFromFormNoEvent(stationGroup, 'freightPointGroup');
  }

  private static handleFreightPointChecked(
    stationGroup: FormGroup<StationGroup>,
    formGroup: FormGroup<ServicePointDetailFormGroup>,
    freightPointDestroy$: Observable<void>,
    destroy$: Observable<void>,
    version?: ReadServicePointVersion
  ) {
    addControlToFormNoEvent(
      stationGroup,
      'freightPointGroup',
      this.freightPointGroup(
        formGroup,
        freightPointDestroy$.pipe(takeUntil(destroy$), mergeWith(destroy$)),
        version
      )
    );
  }

  private static handleStopPointChecked(
    stationGroup: FormGroup<StationGroup>,
    version?: ReadServicePointVersion
  ) {
    addControlToFormNoEvent(
      stationGroup,
      'stopPointGroup',
      this.stopPointGroup(version)
    );
  }

  private static handleStopPointUnchecked(
    stationGroup: FormGroup<StationGroup>
  ) {
    removeControlFromFormNoEvent(stationGroup, 'stopPointGroup');
  }

  static stopPointGroup(version?: ReadServicePointVersion) {
    return new FormGroup<StopPointGroup>({
      stopPointType: new FormControl(version?.stopPointType, {
        nonNullable: true,
        validators: this.stopPointTypeRequiredValidator,
      }),
      meansOfTransport: new FormControl(version?.meansOfTransport ?? [], {
        nonNullable: true,
        validators: Validators.required,
      }),
    });
  }

  static freightPointGroup(
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
        const required =
          country === 'SWITZERLAND' &&
          formGroup.controls.validityGroup.controls.validFrom.value?.isSameOrAfter(
            moment(),
            'day'
          );
        this.updateSortCodeValidation(freightPointGroup, required);
      });

    formGroup.controls.validityGroup.controls.validFrom.valueChanges
      .pipe(takeUntil(destroy$))
      .subscribe((validFrom) => {
        const required =
          validFrom?.isSameOrAfter(moment(), 'day') &&
          formGroup.controls.country.value === 'SWITZERLAND';
        this.updateSortCodeValidation(freightPointGroup, required);
      });

    return freightPointGroup;
  }

  private static updateSortCodeValidation(
    freightPointGroup: FormGroup<FreightPointGroup>,
    required?: boolean
  ) {
    if (required) {
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

  static readonly mapper = class Mapper {
    static mapStation(
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
  };
}
