import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from '../service-point-detail-form-group';
import { AuthService } from '../../../../../core/auth/auth.service';
import {
  ApplicationRole,
  ApplicationType,
  Country,
  Permission,
  PermissionRestrictionType,
  ServicePointsService,
} from '../../../../../api';
import { Countries } from '../../../../../core/country/Countries';
import { BehaviorSubject, catchError, EMPTY, mergeWith, Observable, Subject, take } from 'rxjs';
import { map, takeUntil, tap } from 'rxjs/operators';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ServicePointType } from '../service-point-type';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { GeographyFormGroupBuilder } from '../../../geography/geography-form-group';

export class ServicePointCreationSideService {
  geographyChanged = new BehaviorSubject<boolean>(false);

  set geography(value: boolean) {
    if (this.geographyChanged.value !== value) {
      this.geographyChanged.next(value);
    }
  }
}

@Component({
  selector: 'app-service-point-creation',
  templateUrl: './service-point-creation.component.html',
  styleUrls: ['./service-point-creation.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [ServicePointCreationSideService],
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
    private readonly servicePointService: ServicePointsService,
    private readonly notificationService: NotificationService,
    private readonly sharedService: ServicePointCreationSideService,
  ) {}

  ngOnInit() {
    this.sharedService.geographyChanged
      .pipe(takeUntil(this.destroySubscriptions$))
      .subscribe((enabled) => (enabled ? this.onGeographyEnabled() : this.onGeographyDisabled()));

    this.countryOptions$ = this.authService.loadPermissions().pipe(
      map(() => this.getCountryOptions()),
      tap((countries) => {
        if (countries.length === 1 && countries[0] === Country.Switzerland) {
          this.form.controls.country?.setValue(Country.Switzerland);
        }
      }),
    );

    this.form.controls.country?.valueChanges
      .pipe(mergeWith(this.servicePointTypeChanged$), takeUntil(this.destroySubscriptions$))
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
          this.sharedService.geography = true;
        }
      });

    this.form.controls.country?.valueChanges
      .pipe(takeUntil(this.destroySubscriptions$))
      .subscribe((country) => {
        if (!country) return;
        if (Countries.geolocationCountries.includes(country)) {
          this.form.controls.number.disable();
          this.form.controls.number.reset();
        } else {
          this.form.controls.number.enable();
        }
      });
  }

  ngOnDestroy() {
    this.destroySubscriptions$.complete();
  }

  private onGeographyEnabled() {
    ServicePointFormGroupBuilder.addGroupToForm(
      this.form,
      'servicePointGeolocation',
      GeographyFormGroupBuilder.buildFormGroup(),
    );
  }

  private onGeographyDisabled() {
    ServicePointFormGroupBuilder.removeGroupFromForm(this.form, 'servicePointGeolocation');
  }

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
    console.log('before save', this.form);
    if (this.form.valid) {
      const servicePointVersion = ServicePointFormGroupBuilder.getWritableServicePoint(this.form);
      const controlsAlreadyDisabled = Object.keys(this.form.controls).filter(
        (key) => this.form.get(key)?.disabled,
      );
      this.form.disable({ emitEvent: false });
      this.servicePointService
        .createServicePoint(servicePointVersion)
        .pipe(catchError(() => this.handleError(controlsAlreadyDisabled)))
        .subscribe(async (servicePointVersion) => {
          this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.ADD_SUCCESS');
          await this.router.navigate([servicePointVersion.number.number], {
            relativeTo: this.route,
          });
        });
    }
  }

  private readonly handleError = (excludedControls: string[]) => {
    Object.keys(this.form.controls).forEach((key) => {
      if (!excludedControls.includes(key)) {
        this.form.get(key)?.enable({ emitEvent: false });
      }
    });
    return EMPTY;
  };

  private getCountryOptions(): Country[] {
    const sepodiUserPermission = this.authService.getApplicationUserPermission(
      ApplicationType.Sepodi,
    );
    return this.getCountryScope(sepodiUserPermission);
  }

  private getCountryScope(sepodiUserPermission: Permission): Country[] {
    if (this.isSupervisorOrAdmin(sepodiUserPermission)) {
      const countryScope = Countries.filteredCountries().sort(Countries.compareFn);
      return [...Countries.geolocationCountries, ...countryScope];
    } else {
      let countryScope = sepodiUserPermission.permissionRestrictions
        .filter((restriction) => restriction.type === PermissionRestrictionType.Country)
        .map((restriction) => restriction.valueAsString as Country);
      const geolocationCountries = Countries.geolocationCountries.filter((country) =>
        countryScope.includes(country),
      );
      countryScope = countryScope
        .filter((country) => !geolocationCountries.includes(country))
        .sort(Countries.compareFn);
      return [...geolocationCountries, ...countryScope];
    }
  }

  private readonly isSupervisorOrAdmin = (sepodiUserPermission: Permission) =>
    sepodiUserPermission.role === ApplicationRole.Supervisor || this.authService.isAdmin;

  private async leaveCreation(): Promise<void> {
    await this.router.navigate(['..'], {
      relativeTo: this.route,
    });
  }
}
