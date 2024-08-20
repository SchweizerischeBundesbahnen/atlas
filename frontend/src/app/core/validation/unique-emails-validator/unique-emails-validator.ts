import {AbstractControl, FormArray, ValidationErrors, ValidatorFn} from '@angular/forms';

export class UniqueEmailsValidator {
  static uniqueEmails(): ValidatorFn {
    return (formArray: AbstractControl): ValidationErrors | null => {
      if (!(formArray instanceof FormArray)) {
        throw new Error('UniqueEmailsValidator must be used with a FormArray');
      }

      const formArrayControls = formArray.controls;
      const emailSet = new Set<string>();
      const duplicateEmails: string[] = [];

      for (const control of formArrayControls) {
        const emailControl = control.get('mail');
        const email = emailControl?.value;

        if (typeof email === 'string') {
          const lowerCaseEmail = email.toLowerCase();
          if (emailSet.has(lowerCaseEmail)) {
            duplicateEmails.push(lowerCaseEmail);
          } else {
            emailSet.add(lowerCaseEmail);
          }
        }
      }

      if (duplicateEmails.length > 0) {
        const error: ValidationErrors = {
          duplicateEmail: { emails: duplicateEmails },
        };
        return error;
      }

      return null;
    };
  }
}
