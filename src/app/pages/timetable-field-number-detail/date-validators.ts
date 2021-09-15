import { AbstractControl, ValidatorFn } from '@angular/forms';

export class DateValidators {
  static dateLessThan(validFrom: string, validTo: string): ValidatorFn {
    return (c: AbstractControl): { [key: string]: boolean } | null => {
      const validFromForm = c.get(validFrom);
      const validToForm = c.get(validTo);
      if (validFromForm?.value !== null && validToForm?.value !== null) {
        if (validFromForm?.value > validToForm?.value) {
          validFromForm?.setErrors({
            date_range_error: { date: validToForm?.value },
          });
          validToForm?.setErrors({
            date_range_error: { date: validToForm?.value },
          });
        } else {
          validFromForm?.setErrors(null);
          validToForm?.setErrors(null);
        }
      }
      return null;
    };
  }
}
