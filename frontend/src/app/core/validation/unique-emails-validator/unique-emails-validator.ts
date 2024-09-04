import {AbstractControl, FormArray, ValidationErrors, ValidatorFn} from '@angular/forms';

export class UniqueEmailsValidator {

  private static readonly MAIL = 'mail';
  private static readonly NOT_UNIQUE_EMAIL = 'notUniqueEmail';

  static uniqueEmails(): ValidatorFn {
    return (formArray: AbstractControl): ValidationErrors | null => {
      if (!(formArray instanceof FormArray)) {
        throw new Error('UniqueEmailsValidator must be used with a FormArray');
      }

      formArray.controls.forEach(control => {
        const emailControl = control.get(UniqueEmailsValidator.MAIL);
        if (emailControl) {
          emailControl.setErrors(null);
          emailControl.updateValueAndValidity({ onlySelf: true, emitEvent: false });
        }
      });

      for (let i = 0; i < formArray.controls.length; i++) {
        const comparingControl = formArray.controls[i].get(UniqueEmailsValidator.MAIL);
        const comparingControlValue = comparingControl?.value?.toLowerCase();

        for (let j = i + 1; j < formArray.controls.length; j++) {
          const comparedControl = formArray.controls[j].get(UniqueEmailsValidator.MAIL);
          const comparedControlValue = comparedControl?.value?.toLowerCase();

          if (comparingControlValue && comparedControlValue && comparingControlValue === comparedControlValue) {
            const error = { [UniqueEmailsValidator.NOT_UNIQUE_EMAIL]: comparingControlValue };
            comparingControl?.setErrors(error);
            comparedControl?.setErrors(error);
          }
        }
      }

      return null;
    };
  }
}
