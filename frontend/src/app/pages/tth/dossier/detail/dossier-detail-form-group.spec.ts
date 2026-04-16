import { describe, expect, it } from 'vitest';
import { DossierFormGroupBuilder } from './dossier-detail-form-group';

describe('DossierDetailFormGroup', () => {
  it('should map form question to TthDossier correctly', () => {
    const formGroup = DossierFormGroupBuilder.buildFormGroup();
    const dossier = DossierFormGroupBuilder.getDossier(formGroup);
    expect(dossier.questions.length).toBe(1);
  });

  it('should map form question to TthDossier correctly when filled', () => {
    const formGroup = DossierFormGroupBuilder.buildFormGroup();
    formGroup.controls.question.setValue('What is the status?');
    const dossierQuestions = DossierFormGroupBuilder.getDossier(formGroup).questions;
    expect(dossierQuestions.length).toBe(1);
    expect(dossierQuestions[0].question).toEqual('What is the status?');
    expect(dossierQuestions[0].answerToCanton).toBeNull();
  });
});
