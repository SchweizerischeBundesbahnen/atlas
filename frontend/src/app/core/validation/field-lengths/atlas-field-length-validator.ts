import { AbstractControl, ValidationErrors, Validators } from '@angular/forms';

export class AtlasFieldLengthValidator {
  static small(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(50, control);
  }

  static mid(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(255, control);
  }

  static comments(control: AbstractControl): ValidationErrors | null {
    return AtlasFieldLengthValidator.maxLength(1500, control);
  }

  private static maxLength(lenght: number, control: AbstractControl) {
    return Validators.maxLength(lenght)(control);
  }
}
