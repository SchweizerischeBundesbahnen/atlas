import {AbstractControl, FormArray, ValidationErrors, ValidatorFn} from '@angular/forms';

export class UniqueEmailsValidator {

  static uniqueEmails(): ValidatorFn {
    return (formArray: AbstractControl): ValidationErrors | null => {
      if (!(formArray instanceof FormArray)) {
        throw new Error('UniqueEmailsValidator must be used with a FormArray');
      }

      const emailMap = new Map<string, AbstractControl[]>();

      for (const control of formArray.controls) {
        const emailControl = control.get('mail');
        const email = emailControl?.value;
        if (email) {
          const lowerCaseEmail = email.toLowerCase();

          if (!emailMap.has(lowerCaseEmail)) {
            emailMap.set(lowerCaseEmail, []);
          }
          emailMap.get(lowerCaseEmail)!.push(control);
        }
      }

      for (const [email, controls] of emailMap.entries()) {
        if (controls.length > 1) {
          for (const control of controls) {
            const emailControl = control.get('mail');
            if (emailControl) {
              const error: ValidationErrors = {
                duplicateEmail: email
              };
              emailControl.setErrors(error);
            }
          }
        } else {
          const emailControl = controls[0].get('mail');
          if (emailControl && emailControl.hasError('duplicateEmail')) {
            emailControl.setErrors(null);
            emailControl.updateValueAndValidity({ emitEvent: false });
          }
        }
      }

      return null;
    };
  }

}
