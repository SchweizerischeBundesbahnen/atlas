import { AbstractControl, ValidationErrors, Validators } from '@angular/forms';

export class CharsetsValidator {
  static numeric(control: AbstractControl): ValidationErrors | null {
    return CharsetsValidator.validateAllowedCharacters(control, '[0-9]*', '0-9');
  }

  static numericWithDot(control: AbstractControl): ValidationErrors | null {
    return CharsetsValidator.validateAllowedCharacters(control, '[.0-9]*', '.0-9');
  }

  static sid4pt(control: AbstractControl): ValidationErrors | null {
    return CharsetsValidator.validateAllowedCharacters(
      control,
      '[-.:_0-9a-zA-Z]*',
      '-.:_0-9a-zA-Z'
    );
  }

  static iso88591(control: AbstractControl): ValidationErrors | null {
    return CharsetsValidator.validateAllowedCharacters(control, '[\\u0000-\\u00ff]*', 'ISO-5589-1');
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
