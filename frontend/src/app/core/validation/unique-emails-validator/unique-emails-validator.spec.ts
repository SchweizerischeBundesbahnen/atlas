import {FormArray, FormControl, FormGroup} from '@angular/forms';
import {UniqueEmailsValidator} from './unique-emails-validator';

describe('UniqueEmailsValidator', () => {

  it('should return null for an empty FormArray', () => {
    const formArray = new FormArray([]);
    const result = UniqueEmailsValidator.uniqueEmails()(formArray);
    expect(result).toBeNull();
  });

  it('should return null if all emails are unique', () => {
    const formArray = new FormArray([
      new FormGroup({ mail: new FormControl('test1@example.com') }),
      new FormGroup({ mail: new FormControl('test2@example.com') }),
      new FormGroup({ mail: new FormControl('test3@example.com') }),
    ]);

    const result = UniqueEmailsValidator.uniqueEmails()(formArray);
    expect(result).toBeNull();
  });

  it('should set notUniqueEmail error on all controls with duplicate emails', () => {
    const formArray = new FormArray([
      new FormGroup({ mail: new FormControl('test@example.com') }),
      new FormGroup({ mail: new FormControl('test@example.com') }),
      new FormGroup({ mail: new FormControl('test2@example.com') }),
    ]);

    UniqueEmailsValidator.uniqueEmails()(formArray);

    const control1 = formArray.at(0).get('mail');
    const control2 = formArray.at(1).get('mail');
    expect(control1?.hasError('notUniqueEmail')).toBe(true);
    expect(control2?.hasError('notUniqueEmail')).toBe(true);
  });

  it('should clear notUniqueEmail error when emails are made unique', () => {
    const formArray = new FormArray([
      new FormGroup({ mail: new FormControl('test@example.com') }),
      new FormGroup({ mail: new FormControl('test@example.com') }),
    ]);

    UniqueEmailsValidator.uniqueEmails()(formArray);
    expect(formArray.at(0).get('mail')?.hasError('notUniqueEmail')).toBe(true);
    expect(formArray.at(1).get('mail')?.hasError('notUniqueEmail')).toBe(true);

    formArray.at(1).get('mail')?.setValue('unique@example.com');

    UniqueEmailsValidator.uniqueEmails()(formArray);

    expect(formArray.at(0).get('mail')?.hasError('notUniqueEmail')).toBe(false);
    expect(formArray.at(1).get('mail')?.hasError('notUniqueEmail')).toBe(false);
  });

  it('should handle case-insensitive email comparison', () => {
    const formArray = new FormArray([
      new FormGroup({ mail: new FormControl('Test@Example.com') }),
      new FormGroup({ mail: new FormControl('test@example.com') }),
    ]);

    UniqueEmailsValidator.uniqueEmails()(formArray);

    expect(formArray.at(0).get('mail')?.hasError('notUniqueEmail')).toBe(true);
    expect(formArray.at(1).get('mail')?.hasError('notUniqueEmail')).toBe(true);
  });

  it('should return null if emails are not duplicates but some are empty', () => {
    const formArray = new FormArray([
      new FormGroup({ mail: new FormControl('') }),
      new FormGroup({ mail: new FormControl('test@example.com') }),
      new FormGroup({ mail: new FormControl('another@example.com') }),
    ]);

    const result = UniqueEmailsValidator.uniqueEmails()(formArray);
    expect(result).toBeNull();
  });

  it('should throw an error if used with something other than FormArray', () => {
    const formGroup = new FormGroup({
      mail: new FormControl('test@example.com')
    });

    expect(() => UniqueEmailsValidator.uniqueEmails()(formGroup)).toThrowError('UniqueEmailsValidator must be used with a FormArray');
  });

  it('should handle multiple duplicates and clear errors when fixed', () => {
    const formArray = new FormArray([
      new FormGroup({ mail: new FormControl('test@example.com') }),
      new FormGroup({ mail: new FormControl('test@example.com') }),
      new FormGroup({ mail: new FormControl('test@example.com') }),
    ]);

    UniqueEmailsValidator.uniqueEmails()(formArray);
    formArray.controls.forEach(control => {
      expect(control.get('mail')?.hasError('notUniqueEmail')).toBe(true);
    });

    formArray.at(2).get('mail')?.setValue('unique@example.com');

    UniqueEmailsValidator.uniqueEmails()(formArray);

    expect(formArray.at(0).get('mail')?.hasError('notUniqueEmail')).toBe(true);
    expect(formArray.at(1).get('mail')?.hasError('notUniqueEmail')).toBe(true);
    expect(formArray.at(2).get('mail')?.hasError('notUniqueEmail')).toBe(false);
  });
});
