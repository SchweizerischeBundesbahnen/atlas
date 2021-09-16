import { AbstractControl, ValidatorFn } from '@angular/forms';

export class DateValidators {
  static dateLessThan(validFrom: string, validTo: string): ValidatorFn {
    return (c: AbstractControl): { [key: string]: boolean } | null => {
      const validFromForm = c.get(validFrom);
      const validToForm = c.get(validTo);
      DateValidators.validateDates(validFromForm, validToForm);
      return null;
    };
  }

  static validateDates(validFromForm: AbstractControl | null, validToForm: AbstractControl | null) {
    if (validFromForm?.value !== null && validToForm?.value !== null) {
      if (validFromForm?.value > validToForm?.value) {
        const error = {
          date_range_error: {
            date: {
              validFrom: validFromForm?.value,
              validTo: validToForm?.value,
            },
          },
        };
        if (!validFromForm?.errors) {
          validFromForm?.setErrors(error);
        } else {
          Object.assign(validFromForm?.errors, error);
        }
        if (!validToForm?.errors) {
          validToForm?.setErrors(error);
        } else {
          Object.assign(validToForm?.errors, error);
        }
      } else {
        if (validFromForm?.errors?.date_range_error) {
          delete validFromForm.errors['date_range_error'];
          if (Object.keys(validFromForm.errors).length === 0) {
            validFromForm.setErrors(null);
          }
        }
        if (validToForm?.errors?.date_range_error) {
          delete validToForm.errors['date_range_error'];
          if (Object.keys(validToForm.errors).length === 0) {
            validToForm.setErrors(null);
          }
        }
      }
    }
  }
}
