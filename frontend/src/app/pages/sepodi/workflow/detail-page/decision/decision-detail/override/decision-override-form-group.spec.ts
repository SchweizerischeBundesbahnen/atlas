import { describe, it } from 'vitest';
import {
  expectBlankValueToBeInvalid,
  expectNonBlankValueToBeValid,
} from '../../../../../../../core/validation/whitespace/whitespace-validator-test-util';
import { DecisionOverrideFormGroupBuilder } from './decision-override-form-group';

describe('DecisionOverrideFormGroupBuilder', () => {
  it('should be invalid with a blank (whitespace-only) firstName', () => {
    const formGroup = DecisionOverrideFormGroupBuilder.buildFormGroup();

    expectBlankValueToBeInvalid(formGroup.controls.firstName);
  });

  it('should be invalid with a blank (whitespace-only) lastName', () => {
    const formGroup = DecisionOverrideFormGroupBuilder.buildFormGroup();

    expectBlankValueToBeInvalid(formGroup.controls.lastName);
  });

  it('should be valid with a non-blank firstName/lastName', () => {
    const formGroup = DecisionOverrideFormGroupBuilder.buildFormGroup();

    expectNonBlankValueToBeValid(formGroup.controls.firstName, 'John');
    expectNonBlankValueToBeValid(formGroup.controls.lastName, 'Doe');
  });
});
