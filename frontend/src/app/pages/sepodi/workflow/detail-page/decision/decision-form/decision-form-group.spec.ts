import { describe, expect, it } from 'vitest';
import { DecisionFormGroupBuilder } from './decision-form-group';
import { JudgementType } from '../../../../../../api';
import { expectBlankValueToBeInvalid } from '../../../../../../core/validation/whitespace/whitespace-validator-test-util';

describe('DecisionFormGroupBuilder', () => {
  it('should formGroup with motivation validator for yes', () => {
    const formGroup = buildFormGroup();

    formGroup.controls.judgement.setValue(JudgementType.Yes);
    expect(formGroup.valid).toBe(true);
  });

  it('should formGroup with motivation validator for no', () => {
    const formGroup = buildFormGroup();

    formGroup.controls.judgement.setValue(JudgementType.No);
    expect(formGroup.valid).toBe(false);
  });

  it('should formGroup with motivation validator for no with motivation', () => {
    const formGroup = buildFormGroup();

    formGroup.controls.judgement.setValue(JudgementType.No);
    formGroup.controls.motivation.setValue('Ich judge');
    expect(formGroup.valid).toBe(true);
  });

  it('should reject a blank (whitespace-only) firstName', () => {
    const formGroup = buildFormGroup();

    formGroup.controls.judgement.setValue(JudgementType.Yes);
    expectBlankValueToBeInvalid(formGroup.controls.firstName);
  });

  it('should reject a blank (whitespace-only) lastName', () => {
    const formGroup = buildFormGroup();

    formGroup.controls.judgement.setValue(JudgementType.Yes);
    expectBlankValueToBeInvalid(formGroup.controls.lastName);
  });

  function buildFormGroup() {
    const formGroup = DecisionFormGroupBuilder.buildFormGroup();
    formGroup.patchValue({
      firstName: 'Dänu',
      lastName: 'GymOne',
      organisation: 'Fitness',
      personFunction: 'Trainer',
    });
    return formGroup;
  }
});
