import { AbstractControl, ValidationErrors, Validators } from '@angular/forms';

export class AtlasFieldLengthValidator {
  static length_10(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(10, control);
  }

  static length_50(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(50, control);
  }

  static length_60(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(60, control);
  }

  static length_100(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(100, control);
  }

  static length_255(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(255, control);
  }

  static comments(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(1500, control);
  }

  private static maxLength(lenght: number, control: AbstractControl) {
    return Validators.maxLength(lenght)(control);
  }
}
