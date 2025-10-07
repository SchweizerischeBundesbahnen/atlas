import {
  AbstractControl,
  FormArray,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

export class MinSelectedValidator {
  static minSelected(controlName: string, min: number): ValidatorFn {
    return (group: AbstractControl): ValidationErrors | null => {
      const control = group.get(controlName);
      MinSelectedValidator.validate(control, min);
      return null;
    };
  }

  static validate(control: AbstractControl | null, min: number) {
    if (!control) return;
    if (control.pristine) {
      MinSelectedValidator.clearValidationError(control);
      return;
    }

    const count = MinSelectedValidator.count(control);

    if (count < min && count == 1) {
      const error: ValidationErrors = {
        min_selected_error: { min, actual: count },
      };
      MinSelectedValidator.populateWithValidationErrors(control, error);
    } else {
      MinSelectedValidator.clearValidationError(control);
    }
  }

  private static count(control: AbstractControl): number {
    const value = control.value;

    if (Array.isArray(value)) return value.length;

    if (control instanceof FormArray) {
      return control.controls.filter((c) => !!c.value).length;
    }

    return 0;
  }

  static populateWithValidationErrors(
    control: AbstractControl | null,
    error: ValidationErrors
  ) {
    if (!control) return;
    if (!control.errors) {
      control.setErrors(error);
    } else {
      Object.assign(control.errors, error);
    }
  }

  static clearValidationError(control: AbstractControl | null) {
    if (!control?.errors) return;
    if (control.errors.min_selected_error) {
      delete control.errors['min_selected_error'];
      if (Object.keys(control.errors).length === 0) {
        control.setErrors(null);
      }
    }
  }
}
