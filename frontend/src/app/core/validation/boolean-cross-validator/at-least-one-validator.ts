import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class AtLeastOneValidator {
  static of(firstFieldName: string, secondFieldName: string): ValidatorFn {
    return (c: AbstractControl): { [key: string]: boolean } | null => {
      const firstField = c.get(firstFieldName);
      const secondField = c.get(secondFieldName);
      AtLeastOneValidator.validate(firstField, secondField);
      return null;
    };
  }

  static validate(firstField: AbstractControl | null, secondField: AbstractControl | null) {
    const firstFieldValue = firstField?.value;
    const secondFieldValue = secondField?.value;
    if (!firstFieldValue && !secondFieldValue) {
      const error: ValidationErrors = {
        at_least_one: 'One must be checked',
      };
      AtLeastOneValidator.populateWithValidationErrors(firstField, error);
      AtLeastOneValidator.populateWithValidationErrors(secondField, error);
    } else {
      AtLeastOneValidator.clearValidationError(firstField);
      AtLeastOneValidator.clearValidationError(secondField);
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
    if (controlForm?.errors?.at_least_one) {
      delete controlForm.errors['at_least_one'];
      if (Object.keys(controlForm.errors).length === 0) {
        controlForm.setErrors(null);
      }
    }
  }
}
