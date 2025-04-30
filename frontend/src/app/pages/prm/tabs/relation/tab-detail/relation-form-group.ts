import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../../../core/components/base-detail/base-detail-form-group';
import {
  ReadRelationVersion,
  RelationVersion,
  StandardAttributeType,
  StepFreeAccessAttributeType,
  TactileVisualAttributeType,
} from '../../../../../api';
import { DateRangeValidator } from '../../../../../core/validation/date-range/date-range-validator';

export interface RelationFormGroup extends BaseDetailFormGroup {
  tactileVisualMarks: FormControl<
    TactileVisualAttributeType | null | undefined
  >;
  contrastingAreas: FormControl<StandardAttributeType | null | undefined>;
  stepFreeAccess: FormControl<StepFreeAccessAttributeType | null | undefined>;
}

export class RelationFormGroupBuilder {
  public static buildFormGroup(version?: ReadRelationVersion) {
    return new FormGroup<RelationFormGroup>(
      {
        tactileVisualMarks: new FormControl({
          value:
            version?.tactileVisualMarks ??
            TactileVisualAttributeType.ToBeCompleted,
          disabled: true,
        }),
        contrastingAreas: new FormControl({
          value:
            version?.contrastingAreas ?? StandardAttributeType.ToBeCompleted,
          disabled: true,
        }),
        stepFreeAccess: new FormControl({
          value:
            version?.stepFreeAccess ??
            StepFreeAccessAttributeType.ToBeCompleted,
          disabled: true,
        }),
        validFrom: new FormControl(
          {
            value: version?.validFrom ? moment(version.validFrom) : null,
            disabled: true,
          },
          [Validators.required]
        ),
        validTo: new FormControl(
          {
            value: version?.validTo ? moment(version.validTo) : null,
            disabled: true,
          },
          [Validators.required]
        ),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }

  static getWritableForm(form: FormGroup<RelationFormGroup>): RelationVersion {
    return {
      tactileVisualMarks: form.value.tactileVisualMarks!,
      contrastingAreas: form.value.contrastingAreas!,
      stepFreeAccess: form.value.stepFreeAccess!,
      validFrom: form.value.validFrom!.toDate(),
      validTo: form.value.validTo!.toDate(),
      creationDate: form.value.creationDate!,
      creator: form.value.creator!,
      editionDate: form.value.editionDate!,
      editor: form.value.editor!,
      etagVersion: form.value.etagVersion!,
    };
  }
}
