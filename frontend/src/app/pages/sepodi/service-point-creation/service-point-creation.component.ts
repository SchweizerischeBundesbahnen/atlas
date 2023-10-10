import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from '../service-point-side-panel/service-point/service-point-detail-form-group';
import { AuthService } from '../../../core/auth/auth.service';
import {
  ApplicationRole,
  ApplicationType,
  CoordinatePair,
  Country,
  PermissionRestrictionType,
  SpatialReference,
} from '../../../api';
import { Countries } from '../../../core/country/Countries';
import { EMPTY, mergeWith, Observable, Subject, take } from 'rxjs';
import { filter, map, takeUntil, tap } from 'rxjs/operators';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ServicePointType } from '../service-point-side-panel/service-point/service-point-type';
import { MapService } from '../map/map.service';

@Component({
  selector: 'app-service-point-creation',
  templateUrl: './service-point-creation.component.html',
  styleUrls: ['./service-point-creation.component.scss'],
})
export class ServicePointCreationComponent implements OnInit, OnDestroy {
  public form: FormGroup<ServicePointDetailFormGroup> =
    ServicePointFormGroupBuilder.buildEmptyFormGroup();
  public countryOptions$: Observable<Country[]> = EMPTY;
  public readonly getCountryEnum = Countries.getCountryEnum;
  public servicePointTypeChanged$: Subject<ServicePointType | null | undefined> = new Subject<
    ServicePointType | null | undefined
  >();

  private destroySubscriptions$ = new Subject<void>();

  constructor(
    private readonly authService: AuthService,
    private readonly dialogService: DialogService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly mapService: MapService,
  ) {}

  ngOnInit() {
    // todo: handle isActiveGeolocation on mapService correctly
    this.mapService.isEditMode.next(true);
    this.mapService.isGeolocationActivated.next(false);

    this.countryOptions$ = this.authService.loadPermissions().pipe(
      map(() => this.getCountryOptions()),
      tap((countries) => {
        if (countries.length === 1 && countries[0] === Country.Switzerland) {
          this.form.controls.country?.setValue(Country.Switzerland);
        }
      }),
    );

    this.form.controls.country?.valueChanges
      .pipe(
        mergeWith(this.servicePointTypeChanged$),
        takeUntil(this.destroySubscriptions$),
        filter(() => !this.form.controls.servicePointGeolocation.controls.spatialReference.value),
      )
      .subscribe(() => {
        const country = this.form.controls.country?.value;
        const servicePointType = this.form.controls.selectedType.value;

        if (
          country &&
          Countries.geolocationCountries.includes(country) &&
          servicePointType &&
          [
            ServicePointType.ServicePoint,
            ServicePointType.OperatingPoint,
            ServicePointType.StopPoint,
          ].includes(servicePointType)
        ) {
          this.form.controls.servicePointGeolocation.controls.spatialReference.setValue(
            SpatialReference.Lv95,
          );
        }
      });
  }

  ngOnDestroy() {
    this.destroySubscriptions$.complete();
  }

  // activateGeolocation(coordinates: CoordinatePair) {
  //   this.mapService.isGeolocationActivated.next(true);
  //   // this.mapService.isEditMode.next(true);
  //
  //   if (!this.isCoordinatesPairValidForTransformation(coordinates)) {
  //     return;
  //   }
  //
  //   if (this.currentSpatialReference === SpatialReference.Lv95) {
  //     coordinates = this.coordinateTransformationService.transform(
  //       coordinates,
  //       SpatialReference.Wgs84,
  //     );
  //   }
  //
  //   const coordinatePairWGS84 = { lat: coordinates.north, lng: coordinates.east };
  //   this.mapService.placeMarkerAndFlyTo(coordinatePairWGS84);
  // }
  //
  // deactivateGeolocation() {
  //   this.mapService.isGeolocationActivated.next(false);
  //   this.cancelMapEditMode();
  //   this.isSwitchVersionDisabled = true;
  // }

  async onCancel(): Promise<void> {
    if (this.form.dirty) {
      this.dialogService
        .confirmLeave()
        .pipe(take(1))
        .subscribe(async (result) => {
          if (result) {
            await this.leaveCreation();
          }
        });
    } else {
      await this.leaveCreation();
    }
  }

  onSave(): void {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      // todo: send create request
      console.log('valid');
    }
  }

  private getCountryOptions(): Country[] {
    const sepodiUserPermission = this.authService.getApplicationUserPermission(
      ApplicationType.Sepodi,
    );

    let countryScope: Country[];
    if (sepodiUserPermission.role === ApplicationRole.Supervisor) {
      countryScope = Countries.filteredCountries();
    } else {
      countryScope = sepodiUserPermission.permissionRestrictions
        .filter((restriction) => restriction.type === PermissionRestrictionType.Country)
        .map((restriction) => restriction.valueAsString as Country);
    }

    let firstFive: Country[] = [
      Country.Switzerland,
      Country.GermanyBus,
      Country.AustriaBus,
      Country.ItalyBus,
      Country.FranceBus,
    ];

    if (sepodiUserPermission.role !== ApplicationRole.Supervisor) {
      firstFive = firstFive.filter((country) => countryScope.includes(country));
      countryScope = countryScope.filter((country) => !firstFive.includes(country));
    }

    countryScope.sort(
      (n1, n2) =>
        Countries.getCountryNameUicCodeFromCountry(n1) -
        Countries.getCountryNameUicCodeFromCountry(n2),
    );

    return [...firstFive, ...countryScope];
  }

  private async leaveCreation(): Promise<void> {
    await this.router.navigate(['..'], {
      relativeTo: this.route,
    });
    this.mapService.isEditMode.next(false);
  }
}
