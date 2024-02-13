import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export class AtlasCharsetsValidator {
  static numeric(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[0-9]*', '0-9');
  }

  static numericWithDot(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[.0-9]*', '.0-9');
  }

  static colonSeperatedSid4pt(amountOfColons: number): ValidatorFn {
    return (control) => {
      const patternErrors = Validators.pattern(
        '[-._0-9a-zA-Z]*(:[-._0-9a-zA-Z]+){' + amountOfColons + '}',
      )(control);
      if (patternErrors) {
        const error: ValidationErrors = {
          colon_seperated_sid4pt: {
            sid4ptWithColons: amountOfColons,
          },
        };
        return error;
      }
      return patternErrors;
    };
  }

  static decimalWithMaxDigits(decimalDigits: number): ValidatorFn {
    return (control) => {
      const patternErrors = Validators.pattern('^-?[0-9]*\\.?[0-9]{0,' + decimalDigits + '}')(
        control,
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

  static decimalWithDigits(decimalDigits: number, fractionDigit: number): ValidatorFn {
    return (control) => {
      const patternErrors = Validators.pattern(
        '^\\d{1,' +
          decimalDigits +
          '}(\\.\\d{0,' +
          fractionDigit +
          '})?$|^\\.\\d{0,' +
          fractionDigit +
          '}$',
      )(control);
      if (patternErrors) {
        const error: ValidationErrors = {
          integer_with_fraction: {
            maxFractionDigits: fractionDigit,
            maxDecimalDigits: decimalDigits,
          },
        };
        return error;
      }
      return patternErrors;
    };
  }

  static uppercaseNumeric(control: AbstractControl) {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[A-Z0-9]*', '"A-Z & 0-9"');
  }

  static sid4pt(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(
      control,
      '[-.:_0-9a-zA-Z]*',
      '-.:_0-9a-zA-Z',
    );
  }

  static iso88591(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(
      control,
      '[\\u0000-\\u00ff]*',
      'ISO-8859-1',
    );
  }

  static alphaNumeric(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[0-9a-zA-Z]*', '0-9a-zA-Z');
  }

  static email(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(
      control,
      '^$|^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$',
      'E-Mail Format',
    );
  }

  private static validateAllowedCharacters(
    control: AbstractControl,
    pattern: string,
    allowedChars: string,
  ): ValidationErrors | null {
    const newVar = Validators.pattern(pattern)(control);
    if (newVar) {
      newVar.pattern['allowedCharacters'] = allowedChars;
    }
    return newVar;
  }
}
