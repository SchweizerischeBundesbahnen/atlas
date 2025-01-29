import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  LineConcessionType,
  LineType,
  LineVersionV2,
  OfferCategory,
} from '../../../../api';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import moment from 'moment/moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';

export interface LineDetailFormGroup extends BaseDetailFormGroup {
  swissLineNumber: FormControl<string | null | undefined>;
  lineType: FormControl<LineType | null | undefined>;
  offerCategory: FormControl<OfferCategory | null | undefined>;
  businessOrganisation: FormControl<string | null | undefined>;
  number: FormControl<string | null | undefined>;
  shortNumber: FormControl<string | null | undefined>;
  lineConcessionType: FormControl<LineConcessionType | null | undefined>;
  longName: FormControl<string | null | undefined>;
  description: FormControl<string | null | undefined>;
  comment: FormControl<string | null | undefined>;
}

export class LineFormGroupBuilder {
  static buildFormGroup(
    version?: LineVersionV2
  ): FormGroup<LineDetailFormGroup> {
    return new FormGroup<LineDetailFormGroup>(
      {
        swissLineNumber: new FormControl(version?.swissLineNumber, [
          Validators.required,
          Validators.maxLength(50),
          AtlasCharsetsValidator.sid4pt,
        ]),
        lineType: new FormControl(version?.lineType, [Validators.required]),
        offerCategory: new FormControl(version?.offerCategory, [
          Validators.required,
        ]),
        businessOrganisation: new FormControl(version?.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
        ]),
        number: new FormControl(version?.number, [
          Validators.maxLength(8),
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
          Validators.required,
        ]),
        shortNumber: new FormControl(version?.shortNumber, [
          Validators.maxLength(8),
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        lineConcessionType: new FormControl(version?.lineConcessionType, [
          Validators.required,
        ]),
        longName: new FormControl(version?.longName, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        description: new FormControl(version?.description, [
          AtlasFieldLengthValidator.length_255,
          Validators.minLength(2),
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
          Validators.required,
        ]),
        validFrom: new FormControl(
          version?.validFrom ? moment(version.validFrom) : null,
          [Validators.required]
        ),
        validTo: new FormControl(
          version?.validTo ? moment(version.validTo) : null,
          [Validators.required]
        ),
        comment: new FormControl(version?.comment, [
          AtlasFieldLengthValidator.comments,
          AtlasCharsetsValidator.iso88591,
        ]),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }
}
