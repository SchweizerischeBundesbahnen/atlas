import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from '../service-point-side-panel/service-point/service-point-detail-form-group';
import { AuthService } from '../../../core/auth/auth.service';
import { ApplicationRole, ApplicationType, Country, PermissionRestrictionType } from '../../../api';
import { Countries } from '../../../core/country/Countries';
import { EMPTY, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-service-point-creation',
  templateUrl: './service-point-creation.component.html',
  styleUrls: ['./service-point-creation.component.scss'],
})
export class ServicePointCreationComponent implements OnInit {
  public form: FormGroup<ServicePointDetailFormGroup> =
    ServicePointFormGroupBuilder.buildEmptyFormGroup();
  public countryOptions$: Observable<Country[]> = EMPTY;

  constructor(private readonly authService: AuthService) {}

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
      countryScope = this.filteredCountries();
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
        this.getCountryNameUicCodeFromCountry(n1) - this.getCountryNameUicCodeFromCountry(n2),
    );

    return [...firstFive, ...countryScope];
  }

  // todo: duplicate
  private getCountryNameUicCodeFromCountry(country: Country): number {
    const countryName = Countries.fromCountry(country);
    if (!countryName) return -1;
    return countryName.uicCode;
  }
  // todo: duplicate
  private filteredCountries(): Country[] {
    return Object.values(Country).filter(
      (country) =>
        country !== Country.Canada &&
        country !== Country.Congo &&
        country !== Country.SouthAfrica &&
        country !== Country.Australia &&
        country !== Country.Liechtenstein &&
        country !== Country.Sudan &&
        country !== Country.Tschad &&
        country !== Country.Libyen &&
        country !== Country.Monaco &&
        country !== Country.Niger &&
        country !== Country.Nigeria &&
        country !== Country.Jemen &&
        country !== Country.Switzerland &&
        country !== Country.GermanyBus &&
        country !== Country.AustriaBus &&
        country !== Country.ItalyBus &&
        country !== Country.FranceBus,
    );
  }
  // todo: duplicate
  public readonly getCountryEnum = (country: Country) =>
    Countries.fromCountry(country)?.enumCountry;
}
