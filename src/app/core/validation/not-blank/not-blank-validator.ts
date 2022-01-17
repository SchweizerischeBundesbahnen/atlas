import { AbstractControl, ValidationErrors } from '@angular/forms';

export class NotBlankValidator {
  static notBlank(control: AbstractControl): ValidationErrors | null {
    if (control.value && control.value.trim().length === 0) {
      return {
        notBlank: control.value,
      };
    }
    return null;
  }
}
