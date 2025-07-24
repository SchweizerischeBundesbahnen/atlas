import { Observable } from 'rxjs';
import {
  CreateServicePointVersion,
  ReadServicePointVersion,
} from '../../../../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { filter, takeUntil } from 'rxjs/operators';

export interface RouteNetworkGroup {
  operatingPointRouteNetwork: FormControl<boolean>;
  operatingPointKilometer: FormControl<boolean>;
  operatingPointKilometerMaster: FormControl<number | undefined>;
}

export class RouteNetworkFormGroup {
  static routeNetworkGroup(
    destroy$: Observable<void>,
    version?: ReadServicePointVersion
  ) {
    const routeNetworkGroup = new FormGroup<RouteNetworkGroup>({
      operatingPointRouteNetwork: new FormControl(
        version?.operatingPointRouteNetwork ?? false,
        { nonNullable: true }
      ),
      operatingPointKilometer: new FormControl(
        {
          value: version?.operatingPointKilometer ?? false,
          disabled: version?.operatingPointRouteNetwork ?? false,
        },
        { nonNullable: true }
      ),
      operatingPointKilometerMaster: new FormControl(
        {
          value: version?.operatingPointKilometerMaster?.number,
          disabled: version?.operatingPointRouteNetwork ?? false,
        },
        {
          nonNullable: true,
        }
      ),
    });

    routeNetworkGroup.controls.operatingPointRouteNetwork.valueChanges
      .pipe(
        takeUntil(destroy$),
        filter(
          () => routeNetworkGroup.controls.operatingPointRouteNetwork.dirty
        )
      )
      .subscribe((checked) =>
        checked
          ? this.handleOperatingPointRouteNetworkChecked(
              routeNetworkGroup,
              version
            )
          : this.handleOperatingPointRouteNetworkUnchecked(routeNetworkGroup)
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
        filter(
          () =>
            routeNetworkGroup.controls.operatingPointKilometer.pristine &&
            routeNetworkGroup.controls.operatingPointKilometer.enabled
        )
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
        filter(
          () =>
            routeNetworkGroup.controls.operatingPointKilometerMaster.pristine &&
            routeNetworkGroup.controls.operatingPointKilometerMaster.enabled
        )
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

  private static handleOperatingPointRouteNetworkChecked(
    routeNetworkGroup: FormGroup<RouteNetworkGroup>,
    version?: ReadServicePointVersion
  ) {
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
  }

  private static handleOperatingPointRouteNetworkUnchecked(
    routeNetworkGroup: FormGroup<RouteNetworkGroup>
  ) {
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

  static readonly mapper = class Mapper {
    static mapRouteNetwork(
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
