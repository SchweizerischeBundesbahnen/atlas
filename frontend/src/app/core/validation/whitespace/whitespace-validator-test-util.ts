import { AbstractControl } from '@angular/forms';
import { expect } from 'vitest';

/**
 * Shared assertions for form controls that use WhitespaceValidator.blankOrEmptySpaceSurrounding,
 * used across the person name form group specs to avoid duplicating the same test bodies.
 */
export function expectBlankValueToBeInvalid(control: AbstractControl): void {
  control.setValue('   ');

  expect(control.valid).toBe(false);
}

export function expectNonBlankValueToBeValid(control: AbstractControl, value: string): void {
  control.setValue(value);

  expect(control.valid).toBe(true);
}
