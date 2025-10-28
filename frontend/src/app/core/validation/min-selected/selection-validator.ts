import {
  AbstractControl,
  FormArray,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

export class SelectionValidator {
  static minSelected(min: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const count = this.count(control);
      if (count < min && count > 0) {
        return {
          min_selected_error: { min, actual: count },
        };
      } else {
        return null;
      }
    };
  }

  static requiredSelected(reqSelections: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const count = this.count(control);
      if (count !== reqSelections && count > 0) {
        return {
          req_selected_error: { reqSelections, actual: count },
        };
      } else {
        return null;
      }
    };
  }

  private static count(control: AbstractControl): number {
    const value = control.value;
    if (Array.isArray(value)) return value.length;
    if (control instanceof FormArray) {
      return control.controls.filter((c) => !!c.value).length;
    }
    return 0;
  }
}
