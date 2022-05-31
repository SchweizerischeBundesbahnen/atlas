import { FormControl } from '@angular/forms';
import { AtlasFieldLengthValidator } from './atlas-field-length-validator';

describe('Atlas Field Length Validator', () => {
  it('should check for max field length', () => {
    const formControl = new FormControl(
      '12345678901234567890123456789012345678901234567890123',
      AtlasFieldLengthValidator.length_50
    );
    expect(formControl.errors).toBeTruthy();
  });
});
