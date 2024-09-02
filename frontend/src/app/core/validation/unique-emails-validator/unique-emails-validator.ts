import {AbstractControl, FormArray, ValidationErrors, ValidatorFn} from '@angular/forms';

export class UniqueEmailsValidator {

  static uniqueEmails(): ValidatorFn {
    return (formArray: AbstractControl): ValidationErrors | null => {
      if (!(formArray instanceof FormArray)) {
        throw new Error('UniqueEmailsValidator must be used with a FormArray');
      }

      formArray.controls.forEach(control => {
        const emailControl = control.get('mail');
        if (emailControl?.hasError('duplicateEmail')) {
          emailControl.setErrors(null);
        }
      });

      for (let i = 0; i < formArray.controls.length; i++) {
        const comparingControl = formArray.controls[i].get('mail');
        const comparingControlValue = comparingControl?.value?.toLowerCase();

        for (let j = i + 1; j < formArray.controls.length; j++) {
          const comparedControl = formArray.controls[j].get('mail');
          const comparedControlValue = comparedControl?.value?.toLowerCase();

          if (comparingControlValue && comparedControlValue && comparingControlValue === comparedControlValue) {
            const error = { duplicateEmail: comparingControlValue };
            comparingControl?.setErrors(error);
            comparedControl?.setErrors(error);
          }
        }
      }

      return null;
    };
  }
}
