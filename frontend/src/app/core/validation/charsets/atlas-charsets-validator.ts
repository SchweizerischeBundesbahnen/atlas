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
