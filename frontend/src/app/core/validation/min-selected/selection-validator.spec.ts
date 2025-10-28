import { FormArray, FormControl } from '@angular/forms';
import { SelectionValidator } from './selection-validator';

describe('Selection Validator', () => {
  it('should set min selected error when not enough selected', () => {
    const control = new FormControl(['A'], SelectionValidator.minSelected(2));
    control.markAsDirty();
    expect(control.errors).toBeDefined();
    const error = control.errors?.['min_selected_error'];
    expect(error).toBeDefined();
    expect(error.min).toBe(2);
    expect(error.actual).toBe(1);
  });

  it('should set required selection error when not the right amount of elements selected', () => {
    const control = new FormControl(
      ['A'],
      SelectionValidator.requiredSelected(2)
    );
    control.markAsDirty();
    expect(control.errors).toBeDefined();
    const error = control.errors?.['req_selected_error'];
    expect(error).toBeDefined();
    expect(error.reqSelections).toBe(2);
    expect(error.actual).toBe(1);
  });

  it('should not set min selected error when nothing selected', () => {
    const control = new FormControl([], SelectionValidator.minSelected(2));
    control.markAsDirty();
    expect(control.errors).toBeNull();
  });

  it('should not set required selection error when nothing selected', () => {
    const control = new FormControl([], SelectionValidator.requiredSelected(2));
    control.markAsDirty();
    expect(control.errors).toBeNull();
  });

  it('should set error at 1 then clear when count > 1', () => {
    const formArray = new FormArray(
      [new FormControl(true)],
      SelectionValidator.minSelected(2)
    );
    formArray.markAsDirty();
    expect(formArray.errors?.['min_selected_error']).toBeDefined();
    expect(formArray.errors?.['min_selected_error'].actual).toBe(1);

    formArray.push(new FormControl(false));
    formArray.markAsDirty();
    expect(formArray.errors).toBeNull();
  });
});
