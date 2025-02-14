import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { ReadStopPointWorkflow } from 'src/app/api';
import { UniqueEmailsValidator } from '../../../../../core/validation/unique-emails-validator/unique-emails-validator';
import { ExaminantFormGroup } from '../detail-form/stop-point-workflow-detail-form-group';

export interface AddExaminantsFormGroup {
  ccEmails: FormControl<Array<string> | null | undefined>;
  examinants: FormArray<FormGroup<ExaminantFormGroup>>;
}

export class AddExaminantsFormGroupBuilder {
  static buildFormGroup(
    workflow?: ReadStopPointWorkflow
  ): FormGroup<AddExaminantsFormGroup> {
    return new FormGroup<AddExaminantsFormGroup>({
      ccEmails: new FormControl(workflow?.ccEmails ?? []),
      examinants: new FormArray<FormGroup<ExaminantFormGroup>>([], {
        validators: UniqueEmailsValidator.uniqueEmails(),
      }),
    });
  }
}
