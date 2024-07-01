import {StopPointWorkflowDetailFormGroupBuilder} from "./stop-point-workflow-detail-form-group";
import {JudgementType, ReadStopPointWorkflow} from "../../../../../api";

describe('StopPointWorkflowDetailFormGroupBuilder', () => {

  it('should create formgroup without examinants but default one', () => {
    const workflow: ReadStopPointWorkflow = {
      versionId: 1,
      sloid: 'ch:1:sloid:8000',
      workflowComment: "No comment"
    };

    const formGroup = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(workflow);
    expect(formGroup.controls.examinants.length).toBe(1);
  });

  it('should create formgroup with examinants without judgement', () => {
    const workflow: ReadStopPointWorkflow = {
      versionId: 1,
      sloid: 'ch:1:sloid:8000',
      workflowComment: "No comment",
      examinants: [
        {
          mail: 'test@mail.ch',
          organisation: 'test',
        }
      ]
    };

    const formGroup = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(workflow);

    expect(formGroup.controls.examinants.length).toBe(1);
    expect(formGroup.controls.examinants.at(0).controls.judgement.value).toBeFalsy();
    expect(formGroup.controls.examinants.at(0).controls.judgementIcon.value).toBe('bi-hourglass-split');
  });

  it('should create formgroup with examinants with judgement', () => {
    const workflow: ReadStopPointWorkflow = {
      versionId: 1,
      sloid: 'ch:1:sloid:8000',
      workflowComment: "No comment",
      examinants: [
        {
          mail: 'test@mail.ch',
          organisation: 'test',
          judgement: JudgementType.Yes
        }
      ]
    };

    const formGroup = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(workflow);

    expect(formGroup.controls.examinants.length).toBe(1);
    expect(formGroup.controls.examinants.at(0).controls.judgement.value).toBe(JudgementType.Yes);
    expect(formGroup.controls.examinants.at(0).controls.judgementIcon.value).toBe('bi-check-lg');
  });
})
