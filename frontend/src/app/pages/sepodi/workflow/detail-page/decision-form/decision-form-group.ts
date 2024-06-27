import {AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {JudgementType, ReadDecision} from "../../../../../api";
import {AtlasFieldLengthValidator} from "../../../../../core/validation/field-lengths/atlas-field-length-validator";
import {AtlasCharsetsValidator} from "../../../../../core/validation/charsets/atlas-charsets-validator";

export interface DecisionFormGroup {
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  organisation: FormControl<string | null | undefined>;
  personFunction: FormControl<string | null | undefined>;
  judgement: FormControl<JudgementType | null | undefined>;
  motivation: FormControl<string | null | undefined>;
}

export class DecisionFormGroupBuilder {

  static buildFormGroup(existingDecision?: ReadDecision) {
    return new FormGroup<DecisionFormGroup>({
        firstName: new FormControl(existingDecision?.examinant?.firstName, [Validators.required, AtlasFieldLengthValidator.length_50, AtlasCharsetsValidator.iso88591]),
        lastName: new FormControl(existingDecision?.examinant?.lastName, [Validators.required, AtlasFieldLengthValidator.length_50, AtlasCharsetsValidator.iso88591]),
        organisation: new FormControl(existingDecision?.examinant?.organisation, [Validators.required, AtlasFieldLengthValidator.length_50, AtlasCharsetsValidator.iso88591]),
        personFunction: new FormControl(existingDecision?.examinant?.personFunction, [Validators.required, AtlasFieldLengthValidator.length_50, AtlasCharsetsValidator.iso88591]),
        judgement: new FormControl(existingDecision?.judgement, [Validators.required]),
        motivation: new FormControl(existingDecision?.motivation, [AtlasFieldLengthValidator.comments, AtlasCharsetsValidator.iso88591]),
      },
      {
        validators: DecisionFormGroupBuilder.conditionallyRequired("judgement","motivation"),
      });
  }


  static conditionallyRequired(judgementField: string, requiredField: string): ValidatorFn {
    return (c: AbstractControl): { [key: string]: boolean } | null => {
      const judgement = c.get(judgementField)!;
      const required = c.get(requiredField)!;

      if (judgement.value === JudgementType.No && !required.value) {
        required.setErrors({decision_comment_required: true});
      } else {
        const errors = required.errors;
        delete errors?.decision_comment_required;
        required.setErrors(errors && Object.keys(errors).length === 0 ? null : errors);
      }

      return null;
    };
  }

}
