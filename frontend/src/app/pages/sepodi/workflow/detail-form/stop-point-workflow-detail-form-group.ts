import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Client} from "../../../../api";
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";

export interface StopPointWorkflowDetailFormGroup {
  ccEmails: FormControl<Array<string> | null | undefined>;
  workflowComment: FormControl<string | null | undefined>;
  examinants: FormControl<Array<Client> | null | undefined>;
}

export class StopPointWorkflowDetailFormGroupBuilder {

  static buildFormGroup(): FormGroup<StopPointWorkflowDetailFormGroup> {
    return new FormGroup<StopPointWorkflowDetailFormGroup>(
      {
        ccEmails: new FormControl([]),
        workflowComment: new FormControl('', [Validators.required, AtlasFieldLengthValidator.comments]),
        examinants: new FormControl([])
      }
    );
  }

}
