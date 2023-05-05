import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Moment } from 'moment/moment';

export class NewHearingYearValidator {
  static fromAndToOneYearBefore(
    timetableYear: string,
    hearingFrom: string,
    hearingTo: string
  ): ValidatorFn {
    return (c: AbstractControl): { [key: string]: boolean } | null => {
      const timetableYearForm = c.get(timetableYear);
      const validFromForm = c.get(hearingFrom);
      const validToForm = c.get(hearingTo);
      NewHearingYearValidator.validate(timetableYearForm, validFromForm, validToForm);
      return null;
    };
  }

  static validate(
    timetableYearForm: AbstractControl | null,
    validFromForm: AbstractControl | null,
    validToForm: AbstractControl | null
  ) {
    const timetableYearFormValue: number | undefined = timetableYearForm?.value;
    const validFromValue: Moment | undefined = validFromForm?.value;
    const validToValue: Moment | undefined = validToForm?.value;

    if (
      timetableYearFormValue &&
      validFromValue &&
      validFromValue?.year() !== timetableYearFormValue - 1
    ) {
      const error: ValidationErrors = {
        one_year_before: {
          number: timetableYearFormValue - 1,
        },
      };
      NewHearingYearValidator.populateWithValidationErrors(validFromForm, error);
    } else {
      NewHearingYearValidator.clearValidationError(validFromForm);
    }

    if (
      timetableYearFormValue &&
      validToValue &&
      validToValue?.year() !== timetableYearFormValue - 1
    ) {
      const error: ValidationErrors = {
        one_year_before: {
          number: timetableYearFormValue - 1,
        },
      };
      NewHearingYearValidator.populateWithValidationErrors(validToForm, error);
    } else {
      NewHearingYearValidator.clearValidationError(validToForm);
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
    if (controlForm?.errors?.one_year_before) {
      delete controlForm.errors['one_year_before'];
      if (Object.keys(controlForm.errors).length === 0) {
        controlForm.setErrors(null);
      }
    }
  }
}
