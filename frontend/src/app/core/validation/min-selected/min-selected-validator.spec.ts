import { FormArray, FormControl } from '@angular/forms';
import { MinSelectedValidator } from './min-selected.validator';

describe('Min Selected Validator ', () => {
  it('should set validation error when exactly one element is selected', () => {
    const control = new FormControl(['A']);
    control.markAsDirty();
    MinSelectedValidator.validate(control, 2);
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

  it('should clear only its own error', () => {
    const control = new FormControl(['A', 'B']);
    control.markAsDirty();

    control.setErrors({
      min_selected_error: { min: 2, actual: 1 },
      required: true,
    });

    MinSelectedValidator.validate(control, 2);

    expect(control.errors).toBeDefined();
    expect(control.errors?.['min_selected_error']).toBeUndefined();
    expect(control.errors?.['required']).toBeTrue();
  });

  it('should merge min_selected_error with existing errors', () => {
    const control = new FormControl(['A']);
    control.setErrors({ required: true });
    control.markAsDirty();
    MinSelectedValidator.validate(control, 3);
    expect(control.errors?.['required']).toBeTrue();
    const minErr = control.errors?.['min_selected_error'];
    expect(minErr).toBeDefined();
    expect(minErr.min).toBe(3);
    expect(minErr.actual).toBe(1);
  });

  it('should not throw when called with null control', () => {
    expect(() => MinSelectedValidator.validate(null, 2)).not.toThrow();
  });

  it('should clear existing min_selected_error', () => {
    const control = new FormControl(['A']);
    control.setErrors({ min_selected_error: { min: 2, actual: 1 } });
    control.markAsPristine();
    MinSelectedValidator.validate(control, 2);
    expect(control.errors).toBeNull();
  });

  it('should set error at 1 then clear when count > 1', () => {
    const formArray = new FormArray([new FormControl(true)]);
    formArray.markAsDirty();

    MinSelectedValidator.validate(formArray as unknown as FormControl, 2);
    expect(formArray.errors?.['min_selected_error']).toBeDefined();
    expect(formArray.errors?.['min_selected_error'].actual).toBe(1);

    formArray.push(new FormControl(false));
    formArray.markAsDirty();

    MinSelectedValidator.validate(formArray as unknown as FormControl, 2);
    expect(formArray.errors).toBeNull();
  });
});
