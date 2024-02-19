import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../../../../core/components/base-detail/base-detail-form-group';
import { WhitespaceValidator } from '../../../../../../core/validation/whitespace/whitespace-validator';
import { DateRangeValidator } from '../../../../../../core/validation/date-range/date-range-validator';
import {
  ContactPointType,
  ContactPointVersion,
  ReadContactPointVersion,
  StandardAttributeType,
} from '../../../../../../api';

export interface ContactPointFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  wheelchairAccess: FormControl<StandardAttributeType | null | undefined>;
  inductionLoop: FormControl<StandardAttributeType | null | undefined>;
  openingHours: FormControl<string | null | undefined>;
  type: FormControl<ContactPointType | null | undefined>;
  designation: FormControl<string | null | undefined>;
  additionalInformation: FormControl<string | null | undefined>;
}

export class ContactPointFormGroupBuilder {
  public static buildFormGroup(version?: ReadContactPointVersion) {
    return new FormGroup<ContactPointFormGroup>(
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
        validFrom: new FormControl(version?.validFrom ? moment(version.validFrom) : null, [
          Validators.required,
        ]),
        validTo: new FormControl(version?.validTo ? moment(version.validTo) : null, [
          Validators.required,
        ]),
        wheelchairAccess: new FormControl(
          version?.wheelchairAccess ?? StandardAttributeType.ToBeCompleted,
        ),
        inductionLoop: new FormControl(
          version?.inductionLoop ?? StandardAttributeType.ToBeCompleted,
        ),
        openingHours: new FormControl(version?.openingHours, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        type: new FormControl(version?.type, [Validators.required]),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  static getWritableForm(
    form: FormGroup<ContactPointFormGroup>,
    parentServicePointSloid: string,
  ): ContactPointVersion {
    return {
      sloid: form.value.sloid!,
      parentServicePointSloid: parentServicePointSloid,
      additionalInformation: form.value.additionalInformation!,
      designation: form.value.designation!,
      type: form.value.type!,
      wheelchairAccess: form.value.wheelchairAccess!,
      inductionLoop: form.value.inductionLoop!,
      openingHours: form.value.openingHours!,
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
