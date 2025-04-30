import { FormControl } from '@angular/forms';
import { WhitespaceValidator } from './whitespace-validator';

describe('Whitespace Validator', () => {
  it('should return validation error when blank', () => {
    //given
    const formControl = new FormControl('    ');
    //when
    const errors =
      WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.blank).toBeDefined();
  });

  it('should return validation error when leading whitespaces', () => {
    //given
    const formControl = new FormControl(' leading');
    //when
    const errors =
      WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.whitespaces).toBeDefined();
  });

  it('should return validation error when trailing whitespaces', () => {
    //given
    const formControl = new FormControl('trailing ');
    //when
    const errors =
      WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.whitespaces).toBeDefined();
  });

  it('should return validation error when leading and trailing whitespaces', () => {
    //given
    const formControl = new FormControl('  both ');
    //when
    const errors =
      WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.whitespaces).toBeDefined();
  });

  it('should return validation success when null', () => {
    //given
    const formControl = new FormControl(null);
    //when
    const errors =
      WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeNull();
  });

  it('should return validation success when empty', () => {
    //given
    const formControl = new FormControl('');
    //when
    const errors =
      WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeNull();
  });

  it('should return validation success when no leading nor trailing whitespaces', () => {
    //given
    const formControl = new FormControl('ok');
    //when
    const errors =
      WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeNull();
  });

  it('should return validation success with whitespaces between words', () => {
    //given
    const formControl = new FormControl('ok ok');
    //when
    const errors =
      WhitespaceValidator.blankOrEmptySpaceSurrounding(formControl);
    //then
    expect(errors).toBeNull();
  });
});
