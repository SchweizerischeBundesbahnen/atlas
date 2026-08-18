import { describe, expect, it } from 'vitest';
import { DossierFormGroupBuilder } from './dossier-detail-form-group';
import { SwissCanton } from '../../../../api/model/swissCanton';

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

  it('should map etagVersion of dossier and question back to TthDossier', () => {
    const formGroup = DossierFormGroupBuilder.buildFormGroup({
      statementIds: [1],
      swissCanton: SwissCanton.Bern,
      topic: 'Takt',
      etagVersion: 1,
      questions: [{ id: 5, question: 'What is the status?', etagVersion: 2 }],
    });

    const dossier = DossierFormGroupBuilder.getDossier(formGroup);

    expect(dossier.etagVersion).toBe(1);
    expect(dossier.questions[0].etagVersion).toBe(2);
  });
});
