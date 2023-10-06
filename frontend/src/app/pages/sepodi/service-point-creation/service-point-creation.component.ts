import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from '../service-point-side-panel/service-point/service-point-detail-form-group';
import { AuthService } from '../../../core/auth/auth.service';
import { ApplicationRole, ApplicationType, Country, PermissionRestrictionType } from '../../../api';
import { Countries } from '../../../core/country/Countries';
import { EMPTY, Observable, take } from 'rxjs';
import { map } from 'rxjs/operators';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-service-point-creation',
  templateUrl: './service-point-creation.component.html',
  styleUrls: ['./service-point-creation.component.scss'],
})
export class ServicePointCreationComponent implements OnInit {
  public form: FormGroup<ServicePointDetailFormGroup> =
    ServicePointFormGroupBuilder.buildEmptyFormGroup();
  public countryOptions$: Observable<Country[]> = EMPTY;
  public readonly getCountryEnum = Countries.getCountryEnum;

  constructor(
    private readonly authService: AuthService,
    private readonly dialogService: DialogService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit() {
    this.countryOptions$ = this.authService
      .loadPermissions()
      .pipe(map(() => this.getCountryOptions()));
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

  async onCancel(): Promise<void> {
    if (this.form.dirty) {
      this.dialogService
        .confirmLeave()
        .pipe(take(1))
        .subscribe(async (result) => {
          if (result) {
            await this.router.navigate(['..'], {
              relativeTo: this.route,
            });
          }
        });
    } else {
      await this.router.navigate(['..'], {
        relativeTo: this.route,
      });
    }
  }

  onSave(): void {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      // todo: send create request
      console.log('valid');
    }
  }
}
