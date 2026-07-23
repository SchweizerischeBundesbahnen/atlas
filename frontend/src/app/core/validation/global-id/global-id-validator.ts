import { AbstractControl, ValidationErrors } from '@angular/forms';
import { Country } from '../../../api';

export class GlobalIdValidator {
  static readonly GERMANY_COUNTRIES: Country[] = ['GERMANY', 'GERMANY_BUS'];
  static readonly AUSTRIA_COUNTRIES: Country[] = ['AUSTRIA', 'AUSTRIA_BUS'];
  static readonly GERMANY_PREFIX = 'de:';
  static readonly AUSTRIA_PREFIX = 'at:';

  static countryPrefix(control: AbstractControl): ValidationErrors | null {
    const value: string | undefined | null = control.value;
    if (!value) {
      return null;
    }
    const country = control.parent?.get('country')?.value as Country | null | undefined;
    if (!country) {
      return null;
    }
    if (GlobalIdValidator.GERMANY_COUNTRIES.includes(country) && !value.startsWith(GlobalIdValidator.GERMANY_PREFIX)) {
      return { globalIdPrefixDe: true };
    }
    if (GlobalIdValidator.AUSTRIA_COUNTRIES.includes(country) && !value.startsWith(GlobalIdValidator.AUSTRIA_PREFIX)) {
      return { globalIdPrefixAt: true };
    }
    return null;
  }
}
