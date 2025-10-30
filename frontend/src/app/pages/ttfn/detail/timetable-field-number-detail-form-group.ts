import { BaseDetailFormGroup } from '../../../core/components/base-detail/base-detail-form-group';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  MeanOfTransport,
  Status,
  TimetableFieldNumberVersion,
} from '../../../api';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import { SelectionValidator } from '../../../core/validation/min-selected/selection-validator';
import moment from 'moment/moment';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';

export interface TimetableFieldNumberDetailFormGroup
  extends BaseDetailFormGroup {
  swissTimetableFieldNumber: FormControl<string | undefined>;
  ttfnid: FormControl<string | null | undefined>;
  businessOrganisation: FormControl<string | undefined>;
  number: FormControl<string | undefined>;
  status: FormControl<Status | null | undefined>;
  descriptionOutwardLine1: FormControl<string | undefined>;
  descriptionOutwardLine2: FormControl<string | null | undefined>;
  descriptionOutwardLine3: FormControl<string | null | undefined>;
  descriptionReturnLine1: FormControl<string | undefined>;
  descriptionReturnLine2: FormControl<string | null | undefined>;
  descriptionReturnLine3: FormControl<string | null | undefined>;
  meanOfTransport: FormControl<MeanOfTransport[] | undefined>;
}

export const DESCRIPTION_MAX_LENGTH = 70;

export class TimetableFieldNumberDetailFormGroupBuilder {
  static readonly DESCRIPTION_VALIDATORS = [
    Validators.maxLength(DESCRIPTION_MAX_LENGTH),
    WhitespaceValidator.blankOrEmptySpaceSurrounding,
  ];

  static getFormGroup(
    version?: TimetableFieldNumberVersion
  ): FormGroup<TimetableFieldNumberDetailFormGroup> {
    return new FormGroup<TimetableFieldNumberDetailFormGroup>(
      {
        swissTimetableFieldNumber: new FormControl(
          version?.swissTimetableFieldNumber,
          {
            nonNullable: true,
            validators: [
              AtlasFieldLengthValidator.length_50,
              AtlasCharsetsValidator.sid4pt,
            ],
          }
        ),
        ttfnid: new FormControl(version?.ttfnid),
        businessOrganisation: new FormControl(version?.businessOrganisation, {
          nonNullable: true,
          validators: [
            Validators.required,
            AtlasFieldLengthValidator.length_50,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        }),
        number: new FormControl(version?.number, {
          nonNullable: true,
          validators: [
            Validators.required,
            AtlasFieldLengthValidator.length_50,
            AtlasCharsetsValidator.numericWithDot,
          ],
        }),
        status: new FormControl(version?.status),
        descriptionOutwardLine1: new FormControl(
          version?.descriptionOutwardLine1,
          {
            nonNullable: true,
            validators: [
              ...TimetableFieldNumberDetailFormGroupBuilder.DESCRIPTION_VALIDATORS,
              Validators.required,
            ],
          }
        ),
        descriptionOutwardLine2: new FormControl(
          version?.descriptionOutwardLine2,
          {
            nonNullable: true,
            validators:
              TimetableFieldNumberDetailFormGroupBuilder.DESCRIPTION_VALIDATORS,
          }
        ),
        descriptionOutwardLine3: new FormControl(
          version?.descriptionOutwardLine3,
          {
            nonNullable: true,
            validators:
              TimetableFieldNumberDetailFormGroupBuilder.DESCRIPTION_VALIDATORS,
          }
        ),
        descriptionReturnLine1: new FormControl(
          version?.descriptionReturnLine1,
          {
            nonNullable: true,
            validators:
              TimetableFieldNumberDetailFormGroupBuilder.DESCRIPTION_VALIDATORS,
          }
        ),
        descriptionReturnLine2: new FormControl(
          version?.descriptionReturnLine2,
          {
            nonNullable: true,
            validators:
              TimetableFieldNumberDetailFormGroupBuilder.DESCRIPTION_VALIDATORS,
          }
        ),
        descriptionReturnLine3: new FormControl(
          version?.descriptionReturnLine3,
          {
            nonNullable: true,
            validators:
              TimetableFieldNumberDetailFormGroupBuilder.DESCRIPTION_VALIDATORS,
          }
        ),
        meanOfTransport: new FormControl(
          version?.meanOfTransport ? [version.meanOfTransport] : [],
          {
            nonNullable: true,
            validators: [
              Validators.required,
              SelectionValidator.requiredSelected(1),
            ],
          }
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
}
