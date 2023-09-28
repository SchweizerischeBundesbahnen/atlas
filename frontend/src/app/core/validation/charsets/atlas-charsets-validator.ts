import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { WGS84_MAX_DIGITS } from 'src/app/pages/sepodi/geography/geography.component';

export class AtlasCharsetsValidator {
  static numeric(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[0-9]*', '0-9');
  }

  static numericWithDot(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[.0-9]*', '.0-9');
  }

  static decimalWithDigits(decimalDigits: number): ValidatorFn {
    return (control) => {
      const patternErrors = Validators.pattern('^-?[0-9]*\\.?[0-9]{0,' + decimalDigits + '}')(
        control
      );
      if (patternErrors) {
        const error: ValidationErrors = {
          decimal_number: {
            maxDecimalDigits: decimalDigits,
          },
        };
        return error;
      }
      return patternErrors;
    };
  }

  static isLngValid(lng: number) {
    return Number(lng) >= -180 && Number(lng) <= 180;
  }

  static isLatValid(lat: number) {
    return Number(lat) >= -90 && Number(lat) <= 90;
  }

  static wgs84Coordinates(isLng: boolean): ValidatorFn {
    return (control) => {
      const patternErrors = Validators.pattern('^-?[0-9]*\\.?[0-9]{0,' + WGS84_MAX_DIGITS + '}')(
        control
      );

      if (
        (isLng && !this.isLngValid(control.value)) ||
        (!isLng && !this.isLatValid(control.value))
      ) {
        const error_lat: ValidationErrors = {
          WGS84: {},
        };
        return error_lat;
      }
      if (patternErrors) {
        const error: ValidationErrors = {
          decimal_number: {
            maxDecimalDigits: 11,
          },
        };
        return error;
      }
      return patternErrors;
    };
  }

  static sid4pt(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(
      control,
      '[-.:_0-9a-zA-Z]*',
      '-.:_0-9a-zA-Z'
    );
  }

  static iso88591(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(
      control,
      '[\\u0000-\\u00ff]*',
      'ISO-8859-1'
    );
  }

  static alphaNumeric(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[0-9a-zA-Z]*', '0-9a-zA-Z');
  }

  static email(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(
      control,
      '^$|^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$',
      'E-Mail Format'
    );
  }

  private static validateAllowedCharacters(
    control: AbstractControl,
    pattern: string,
    allowedChars: string
  ): ValidationErrors | null {
    const newVar = Validators.pattern(pattern)(control);
    if (newVar) {
      newVar.pattern['allowedCharacters'] = allowedChars;
    }
    return newVar;
  }
}
