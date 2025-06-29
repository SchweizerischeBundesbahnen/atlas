import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../../../../core/components/base-detail/base-detail-form-group';
import { WhitespaceValidator } from '../../../../../../core/validation/whitespace/whitespace-validator';
import { DateRangeValidator } from '../../../../../../core/validation/date-range/date-range-validator';
import {
  ReadReferencePointVersion,
  ReferencePointAttributeType,
  ReferencePointVersion,
} from '../../../../../../api';

export interface CompleteReferencePointFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  designation: FormControl<string | null | undefined>;
  referencePointType: FormControl<
    ReferencePointAttributeType | null | undefined
  >;
  mainReferencePoint: FormControl<boolean | null | undefined>;
  additionalInformation: FormControl<string | null | undefined>;
}

export class ReferencePointFormGroupBuilder {
  public static buildCompleteFormGroup(version?: ReadReferencePointVersion) {
    return new FormGroup<CompleteReferencePointFormGroup>(
      {
        sloid: new FormControl(version?.sloid),
        additionalInformation: new FormControl(version?.additionalInformation, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        designation: new FormControl(version?.designation, [
          Validators.required,
          Validators.maxLength(50),
        ]),
        referencePointType: new FormControl(version?.referencePointType, [
          Validators.required,
        ]),
        mainReferencePoint: new FormControl(version?.mainReferencePoint),
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
    form: FormGroup<CompleteReferencePointFormGroup>,
    parentServicePointSloid: string
  ): ReferencePointVersion {
    return {
      sloid: form.value.sloid!,
      parentServicePointSloid: parentServicePointSloid,
      additionalInformation: form.value.additionalInformation!,
      designation: form.value.designation!,
      referencePointType: form.value.referencePointType!,
      mainReferencePoint: form.value.mainReferencePoint!,
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
