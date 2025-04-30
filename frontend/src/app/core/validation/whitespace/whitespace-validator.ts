import { AbstractControl, ValidationErrors } from '@angular/forms';

export class WhitespaceValidator {
  static blankOrEmptySpaceSurrounding(
    control: AbstractControl
  ): ValidationErrors | null {
    if (control.value?.length && control.value?.trim().length === 0) {
      return { blank: control.value };
    }
    if (control.value?.startsWith(' ') || control.value?.endsWith(' ')) {
      return { whitespaces: control.value };
    }
    return null;
  }
}
