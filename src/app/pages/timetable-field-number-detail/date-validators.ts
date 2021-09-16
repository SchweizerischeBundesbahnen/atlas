import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

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
    const validFromValue = validFromForm?.value;
    const validToValue = validToForm?.value;
    if (validFromValue !== null && validToValue !== null && validFromValue > validToValue) {
      const error: ValidationErrors = {
        date_range_error: {
          date: { validFrom: validFromValue, validTo: validToValue },
        },
      };
      DateValidators.populateWithValidationErrors(validFromForm, error);
      DateValidators.populateWithValidationErrors(validToForm, error);
    } else {
      DateValidators.clearValidationError(validFromForm);
      DateValidators.clearValidationError(validToForm);
    }
  }

  static populateWithValidationErrors(
    controlForm: AbstractControl | null,
    error: ValidationErrors
  ) {
    if (!controlForm?.errors) {
      controlForm?.setErrors(error);
    } else {
      Object.assign(controlForm?.errors, error);
    }
  }

  static clearValidationError(controlForm: AbstractControl | null) {
    if (controlForm?.errors?.date_range_error) {
      delete controlForm.errors['date_range_error'];
      if (Object.keys(controlForm.errors).length === 0) {
        controlForm.setErrors(null);
      }
    }
  }
}
