import { Injectable } from '@angular/core';
import { ValidationError } from './validation-error';
import { ValidationErrors } from '@angular/forms';

@Injectable({
  providedIn: 'root',
})
export class ValidationService {
  DATE_PATTERN = 'DD.MM.yyyy';

  getValidation(controlErrors: ValidationErrors | null) {
    const result: ValidationError[] = [];
    if (controlErrors) {
      Object.keys(controlErrors).forEach((keyError) => {
        result.push({
          error: 'VALIDATION.' + keyError.toUpperCase(),
          value: controlErrors[keyError],
        });
      });
    }
    return result;
  }

  displayDate(validationError: ValidationError) {
    const pattern = this.DATE_PATTERN;
    if (validationError?.value.date) {
      const validFrom = validationError.value.date.validFrom;
      const validTo = validationError.value.date.validTo;
      return validFrom.format(pattern) + ' - ' + validTo.format(pattern);
    }
    if (validationError?.value.min) {
      return validationError.value.min.format(pattern);
    }
    if (validationError?.value.max) {
      return validationError.value.max.format(pattern);
    }
  }
}
