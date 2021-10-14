import { Injectable } from '@angular/core';
import { ValidationError } from './validation-error';
import { ValidationErrors } from '@angular/forms';
import { DATE_PATTERN } from '../date/date.service';

@Injectable({
  providedIn: 'root',
})
export class ValidationService {
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
    if (validationError?.value.date) {
      const validFrom = validationError.value.date.validFrom;
      const validTo = validationError.value.date.validTo;
      return validFrom.format(DATE_PATTERN) + ' - ' + validTo.format(DATE_PATTERN);
    }
    if (validationError?.value.min) {
      return validationError.value.min.format(DATE_PATTERN);
    }
    if (validationError?.value.max) {
      return validationError.value.max.format(DATE_PATTERN);
    }
  }
}
