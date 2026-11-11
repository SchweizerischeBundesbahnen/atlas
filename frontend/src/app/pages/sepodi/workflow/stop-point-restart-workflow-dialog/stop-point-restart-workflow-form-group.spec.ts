import { describe, it } from 'vitest';
import {
  expectBlankValueToBeInvalid,
  expectNonBlankValueToBeValid,
} from '../../../../core/validation/whitespace/whitespace-validator-test-util';
import { StopPointRestartWorkflowFormGroupBuilder } from './stop-point-restart-workflow-form-group';

describe('StopPointRestartWorkflowFormGroupBuilder', () => {
  it('should be invalid with a blank (whitespace-only) firstName', () => {
    const formGroup = StopPointRestartWorkflowFormGroupBuilder.initFormGroup();

    expectBlankValueToBeInvalid(formGroup.controls.firstName);
  });

  it('should be invalid with a blank (whitespace-only) lastName', () => {
    const formGroup = StopPointRestartWorkflowFormGroupBuilder.initFormGroup();

    expectBlankValueToBeInvalid(formGroup.controls.lastName);
  });

  it('should be valid with a non-blank firstName/lastName', () => {
    const formGroup = StopPointRestartWorkflowFormGroupBuilder.initFormGroup();

    expectNonBlankValueToBeValid(formGroup.controls.firstName, 'John');
    expectNonBlankValueToBeValid(formGroup.controls.lastName, 'Doe');
  });
});
