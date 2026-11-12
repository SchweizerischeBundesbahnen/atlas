import { describe, it } from 'vitest';
import {
  expectBlankValueToBeInvalid,
  expectNonBlankValueToBeValid,
} from '../../../../core/validation/whitespace/whitespace-validator-test-util';
import { StopPointRejectWorkflowFormGroupBuilder } from './stop-point-reject-workflow-form-group';

describe('StopPointRejectWorkflowFormGroupBuilder', () => {
  it('should be invalid with a blank (whitespace-only) firstName', () => {
    const formGroup = StopPointRejectWorkflowFormGroupBuilder.initFormGroup();

    expectBlankValueToBeInvalid(formGroup.controls.firstName);
  });

  it('should be invalid with a blank (whitespace-only) lastName', () => {
    const formGroup = StopPointRejectWorkflowFormGroupBuilder.initFormGroup();

    expectBlankValueToBeInvalid(formGroup.controls.lastName);
  });

  it('should be valid with a non-blank firstName/lastName', () => {
    const formGroup = StopPointRejectWorkflowFormGroupBuilder.initFormGroup();

    expectNonBlankValueToBeValid(formGroup.controls.firstName, 'John');
    expectNonBlankValueToBeValid(formGroup.controls.lastName, 'Doe');
  });
});
