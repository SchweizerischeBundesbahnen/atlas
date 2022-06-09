import { AbstractControl, ValidationErrors, Validators } from '@angular/forms';

export class AtlasCharsetsValidator {
  static numeric(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[0-9]*', '0-9');
  }

  static numericWithDot(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[.0-9]*', '.0-9');
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
      '(?:[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*|"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])',
      'E-Mail Format'
    );
  }

  static fiveNumbers(control: AbstractControl): ValidationErrors | null {
    return AtlasCharsetsValidator.validateAllowedCharacters(control, '[0-9]{1,5}', '0-9');
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
