import { DecisionFormGroupBuilder } from './decision-form-group';
import { JudgementType } from '../../../../../../api';

describe('DecisionFormGroupBuilder', () => {
  it('should formGroup with motivation validator for yes', () => {
    const formGroup = buildFormGroup();

    formGroup.controls.judgement.setValue(JudgementType.Yes);
    expect(formGroup.valid).toBeTrue();
  });

  it('should formGroup with motivation validator for no', () => {
    const formGroup = buildFormGroup();

    formGroup.controls.judgement.setValue(JudgementType.No);
    expect(formGroup.valid).toBeFalse();
  });

  it('should formGroup with motivation validator for no with motivation', () => {
    const formGroup = buildFormGroup();

    formGroup.controls.judgement.setValue(JudgementType.No);
    formGroup.controls.motivation.setValue('Ich judge');
    expect(formGroup.valid).toBeTrue();
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
