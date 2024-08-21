import {AbstractControl, FormArray, ValidationErrors, ValidatorFn} from '@angular/forms';

export class UniqueEmailsValidator {

  static uniqueEmails(): ValidatorFn {
    return (formArray: AbstractControl): ValidationErrors | null => {
      if (!(formArray instanceof FormArray)) {
        throw new Error('UniqueEmailsValidator must be used with a FormArray');
      }

      const emailSet = new Set<string>();

      for (const control of formArray.controls) {
        const emailControl = control.get('mail');
        const email = emailControl?.value;
        const lowerCaseEmail = email.toLowerCase();
        if (lowerCaseEmail) {
          if (emailSet.has(lowerCaseEmail)) {
            return { duplicateEmail: { emails: [lowerCaseEmail] } };
          }
          emailSet.add(lowerCaseEmail);
        }
      }

      return null;
    };
  }

}
