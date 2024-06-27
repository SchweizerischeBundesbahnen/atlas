import {FormControl, FormGroup, Validators} from "@angular/forms";
import {JudgementType, ReadDecision} from "../../../../../../api";
import {AtlasFieldLengthValidator} from "../../../../../../core/validation/field-lengths/atlas-field-length-validator";
import {AtlasCharsetsValidator} from "../../../../../../core/validation/charsets/atlas-charsets-validator";
import {DecisionFormGroupBuilder} from "../../decision-form/decision-form-group";

export interface DecisionOverrideFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  fotJudgement: FormControl<JudgementType | null | undefined>;
  fotMotivation: FormControl<string | null | undefined>;
}

export class DecisionOverrideFormGroupBuilder {

  static buildFormGroup(existingDecision?: ReadDecision) {
    return new FormGroup<DecisionOverrideFormGroup>({
        firstName: new FormControl(existingDecision?.fotOverrider?.firstName, [Validators.required, AtlasFieldLengthValidator.length_50, AtlasCharsetsValidator.iso88591]),
        lastName: new FormControl(existingDecision?.fotOverrider?.lastName, [Validators.required, AtlasFieldLengthValidator.length_50, AtlasCharsetsValidator.iso88591]),
        fotJudgement: new FormControl(existingDecision?.fotJudgement, [Validators.required]),
        fotMotivation: new FormControl(existingDecision?.fotMotivation, [AtlasFieldLengthValidator.comments, AtlasCharsetsValidator.iso88591]),
      },
      {
        validators: DecisionFormGroupBuilder.conditionallyRequired("fotJudgement","fotMotivation"),
      });
  }
}
