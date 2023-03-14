import { Injectable } from '@angular/core';
import { ValidationError } from './validation-error';
import { FormControl, FormGroup, ValidationErrors } from '@angular/forms';
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
          params: {
            date: this.displayDate(controlErrors[keyError]),
            length: controlErrors[keyError]['requiredLength'],
            allowedChars: controlErrors[keyError]['allowedCharacters'],
            max: controlErrors[keyError]['max'],
            min: controlErrors[keyError]['min'],
          },
        });
      });
    }

    // On a date parsing error, we only need that error and can ignore required etc.
    const matDatePickerParseErrors = result.filter(
      (errors) => errors.error === 'VALIDATION.MATDATEPICKERPARSE'
    );
    if (matDatePickerParseErrors.length > 0) {
      return matDatePickerParseErrors;
    }

    return result;
  }

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  displayDate(validationError: any) {
    if (validationError?.date) {
      const validFrom = validationError.date.validFrom;
      const validTo = validationError.date.validTo;
      return validFrom.format(DATE_PATTERN) + ' - ' + validTo.format(DATE_PATTERN);
    }
    if (validationError?.min && typeof validationError?.min.format === 'function') {
      return validationError.min.format(DATE_PATTERN);
    }
    if (validationError?.max && typeof validationError?.max.format === 'function') {
      return validationError.max.format(DATE_PATTERN);
    }
  }

  public static validateForm(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach((field) => {
      const control = formGroup.get(field);
      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        ValidationService.validateForm(control);
      }
    });
  }
}
