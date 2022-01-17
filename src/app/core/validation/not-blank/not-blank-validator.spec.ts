import { FormControl } from '@angular/forms';
import { NotBlankValidator } from './not-blank-validator';

describe('NotBlank Validator', () => {
  it('should return validation error when blank', () => {
    //given
    const formControl = new FormControl('    ');
    //when
    const errors = NotBlankValidator.notBlank(formControl);
    //then
    expect(errors).toBeDefined();
    expect(errors?.notBlank).toBeDefined();
  });

  it('should return validation success when null', () => {
    //given
    const formControl = new FormControl(null);
    //when
    const errors = NotBlankValidator.notBlank(formControl);
    //then
    expect(errors).toBeNull();
  });

  it('should return validation success when value contains characters ', () => {
    //given
    const formControl = new FormControl('    asdf');
    //when
    const errors = NotBlankValidator.notBlank(formControl);
    //then
    expect(errors).toBeNull();
  });
});
