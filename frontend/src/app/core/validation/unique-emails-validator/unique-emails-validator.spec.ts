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

  it('should return a validation error if there are duplicate emails', () => {
    const formArray = new FormArray([
      new FormGroup({ mail: new FormControl('test@example.com') }),
      new FormGroup({ mail: new FormControl('test@example.com') }),
      new FormGroup({ mail: new FormControl('test2@example.com') }),
    ]);

    const result = UniqueEmailsValidator.uniqueEmails()(formArray);
    expect(result).toEqual({ duplicateEmail: 'test@example.com' });
  });

  it('should handle case-insensitive email comparison', () => {
    const formArray = new FormArray([
      new FormGroup({ mail: new FormControl('Test@Example.com') }),
      new FormGroup({ mail: new FormControl('test@example.com') }),
    ]);

    const result = UniqueEmailsValidator.uniqueEmails()(formArray);
    expect(result).toEqual({ duplicateEmail: 'test@example.com' });
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
});
