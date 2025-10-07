import { FormControl } from '@angular/forms';
import { MinSelectedValidator } from './min-selected.validator';

describe('Min Selected Validator', () => {
  it('should set validation error when exactly one element is selected', () => {
    // given
    const control = new FormControl(['A']);
    control.markAsDirty();
    // when
    MinSelectedValidator.validate(control, 2);
    // then
    expect(control.errors).toBeDefined();
    const error = control.errors?.['min_selected_error'];
    expect(error).toBeDefined();
    expect(error.min).toBe(2);
    expect(error.actual).toBe(1);
  });

  it('should not set validation error', () => {
    const control = new FormControl([]);
    control.markAsDirty();
    MinSelectedValidator.validate(control, 2);
    expect(control.errors).toBeNull();
  });

  it('should remove validation error', () => {
    const control = new FormControl(['A']);
    control.markAsDirty();
    MinSelectedValidator.validate(control, 2);
    expect(control.errors?.['min_selected_error']).toBeDefined();

    control.setValue(['X', 'Y']);
    control.markAsDirty();
    MinSelectedValidator.validate(control, 2);

    expect(control.errors).toBeNull();
  });
});
