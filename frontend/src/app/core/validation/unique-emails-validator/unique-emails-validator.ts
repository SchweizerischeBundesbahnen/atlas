import {AbstractControl, FormArray, ValidationErrors, ValidatorFn} from '@angular/forms';

export class UniqueEmailsValidator {

  static uniqueEmails(): ValidatorFn {
    return (formArray: AbstractControl): ValidationErrors | null => {
      if (!(formArray instanceof FormArray)) {
        throw new Error('UniqueEmailsValidator must be used with a FormArray');
      }
      const emailMap = this.createMapOfEmails(formArray as FormArray);
      this.setOrClearValidationErrors(emailMap);
      return null;
    };
  }

  private static createMapOfEmails(formArray: FormArray): Map<string, AbstractControl[]> {
    const emailMap = new Map<string, AbstractControl[]>();
    for (const examinantControl of formArray.controls) {
      this.addControlFromArrayToMap(examinantControl, emailMap);
    }
    return emailMap;
  }

  private static addControlFromArrayToMap(control: AbstractControl, emailMap: Map<string, AbstractControl[]>) {
    const emailControl = control.get('mail');
    const emailControlValue = emailControl?.value;
    if (emailControlValue) {
      const lowerCaseEmailControlValue = emailControlValue.toLowerCase();
      if (!emailMap.has(lowerCaseEmailControlValue)) {
        emailMap.set(lowerCaseEmailControlValue, []);
      }
      emailMap.get(lowerCaseEmailControlValue)!.push(control);
    }
  }

  private static setOrClearValidationErrors(emailMap: Map<string, AbstractControl[]>) {
    for (const [email, controls] of emailMap.entries()) {
      if (controls.length > 1) {
        this.setDuplicateEmailValidationErrorOnEmailControl(controls, email);
      } else {
        this.removeDuplicateEmailValidationErrorOnEmailControl(controls[0]);
      }
    }
  }

  private static setDuplicateEmailValidationErrorOnEmailControl(controls: AbstractControl[], email: string) {
    for (const control of controls) {
      const emailControl = control.get('mail');
      if (emailControl) {
        const error: ValidationErrors = { duplicateEmail: email };
        emailControl.setErrors(error);
      }
    }
  }

  private static removeDuplicateEmailValidationErrorOnEmailControl(control: AbstractControl) {
    const emailControl = control.get('mail');
    if (emailControl && emailControl.hasError('duplicateEmail')) {
      emailControl.setErrors(null);
      emailControl.updateValueAndValidity({ emitEvent: false });
    }
  }

}
