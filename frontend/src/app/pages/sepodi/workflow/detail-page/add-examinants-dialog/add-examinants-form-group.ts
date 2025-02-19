import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
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
    return new FormGroup<AddExaminantsFormGroup>(
      {
        ccEmails: new FormControl(workflow?.ccEmails ?? []),
        examinants: new FormArray<FormGroup<ExaminantFormGroup>>([], {
          validators: UniqueEmailsValidator.uniqueEmails(),
        }),
      },
      [this.examinantOrCcMailValidator()]
    );
  }

  static examinantOrCcMailValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const formGroup = control as FormGroup<AddExaminantsFormGroup>;

      const numberOfExaminants = formGroup.controls.examinants.length;
      const numberOfCcMails = formGroup.controls.ccEmails.value?.length ?? 0;

      if (numberOfExaminants + numberOfCcMails === 0) {
        return {
          EXAMINANT_OR_CC_MAIL: true,
        };
      }
      return null;
    };
  }
}
