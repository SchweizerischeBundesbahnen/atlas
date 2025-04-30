import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../../../../core/components/base-detail/base-detail-form-group';
import { WhitespaceValidator } from '../../../../../../core/validation/whitespace/whitespace-validator';
import { DateRangeValidator } from '../../../../../../core/validation/date-range/date-range-validator';
import {
  ReadToiletVersion,
  StandardAttributeType,
  ToiletVersion,
} from '../../../../../../api';

export interface ToiletFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  designation: FormControl<string | null | undefined>;
  additionalInformation: FormControl<string | null | undefined>;
  wheelchairToilet: FormControl<StandardAttributeType | null | undefined>;
}

export class ToiletFormGroupBuilder {
  public static buildFormGroup(version?: ReadToiletVersion) {
    return new FormGroup<ToiletFormGroup>(
      {
        sloid: new FormControl(version?.sloid),
        additionalInformation: new FormControl(version?.additionalInformation, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        designation: new FormControl(version?.designation, [
          Validators.maxLength(50),
          Validators.required,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
        ]),
        wheelchairToilet: new FormControl(
          version?.wheelchairToilet ?? StandardAttributeType.ToBeCompleted
        ),
        validFrom: new FormControl(
          version?.validFrom ? moment(version.validFrom) : null,
          [Validators.required]
        ),
        validTo: new FormControl(
          version?.validTo ? moment(version.validTo) : null,
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

  static getWritableForm(
    form: FormGroup<ToiletFormGroup>,
    parentServicePointSloid: string
  ): ToiletVersion {
    return {
      sloid: form.value.sloid!,
      parentServicePointSloid: parentServicePointSloid,
      additionalInformation: form.value.additionalInformation!,
      designation: form.value.designation!,
      wheelchairToilet: form.value.wheelchairToilet!,
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
