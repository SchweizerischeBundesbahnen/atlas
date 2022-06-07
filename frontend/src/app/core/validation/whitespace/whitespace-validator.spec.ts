import { UntypedFormControl } from '@angular/forms';
import { WhitespaceValidator } from './whitespace-validator';

describe('Whitespace Validator', () => {
  it('should return validation error when blank', () => {
    //given
    const formControl = new UntypedFormControl('    ');
    //when
    const errors = WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.blank).toBeDefined();
  });

  it('should return validation error when leading whitespaces', () => {
    //given
    const formControl = new UntypedFormControl(' leading');
    //when
    const errors = WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.whitespaces).toBeDefined();
  });

  it('should return validation error when trailing whitespaces', () => {
    //given
    const formControl = new UntypedFormControl('trailing ');
    //when
    const errors = WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.whitespaces).toBeDefined();
  });

  it('should return validation error when leading and trailing whitespaces', () => {
    //given
    const formControl = new UntypedFormControl('  both ');
    //when
    const errors = WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.whitespaces).toBeDefined();
  });

  it('should return validation success when null', () => {
    //given
    const formControl = new UntypedFormControl(null);
    //when
    const errors = WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeNull();
  });

  it('should return validation success when empty', () => {
    //given
    const formControl = new UntypedFormControl('');
    //when
    const errors = WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeNull();
  });

  it('should return validation success when no leading nor trailing whitespaces', () => {
    //given
    const formControl = new UntypedFormControl('ok');
    //when
    const errors = WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeNull();
  });

  it('should return validation success with whitespaces between words', () => {
    //given
    const formControl = new UntypedFormControl('ok ok');
    //when
    const errors = WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeNull();
  });
});
