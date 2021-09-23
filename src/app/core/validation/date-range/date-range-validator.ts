import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import moment, { Moment } from 'moment';

export class DateRangeValidator {
  static fromGreaterThenTo(validFrom: string, validTo: string): ValidatorFn {
    return (c: AbstractControl): { [key: string]: boolean } | null => {
      const validFromForm = c.get(validFrom);
      const validToForm = c.get(validTo);
      DateRangeValidator.validate(validFromForm, validToForm);
      return null;
    };
  }

  static validate(validFromForm: AbstractControl | null, validToForm: AbstractControl | null) {
    const validFromValue = validFromForm?.value;
    const validToValue = validToForm?.value;
    if (validFromValue !== null && validToValue !== null && validFromValue.isAfter(validToValue)) {
      const error: ValidationErrors = {
        date_range_error: {
          date: { validFrom: validFromValue, validTo: validToValue },
        },
      };
      DateRangeValidator.populateWithValidationErrors(validFromForm, error);
      DateRangeValidator.populateWithValidationErrors(validToForm, error);
    } else {
      DateRangeValidator.clearValidationError(validFromForm);
      DateRangeValidator.clearValidationError(validToForm);
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
