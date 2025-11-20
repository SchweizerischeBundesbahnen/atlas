import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BusinessOrganisationVersion, BusinessType } from '../../../../api';
import { BaseDetailFormGroup } from '../../../../core/model/base-detail-form-group';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import moment from 'moment';

export interface BusinessOrganisationDetailFormGroup
  extends BaseDetailFormGroup {
  descriptionDe: FormControl<string | null | undefined>;
  descriptionFr: FormControl<string | null | undefined>;
  descriptionIt: FormControl<string | null | undefined>;
  descriptionEn: FormControl<string | null | undefined>;
  abbreviationDe: FormControl<string | null | undefined>;
  abbreviationFr: FormControl<string | null | undefined>;
  abbreviationIt: FormControl<string | null | undefined>;
  abbreviationEn: FormControl<string | null | undefined>;
  organisationNumber: FormControl<number | null | undefined>;
  contactEnterpriseEmail: FormControl<string | null | undefined>;
  businessTypes: FormControl<Set<BusinessType> | null | undefined>;
}

export class BusinessOrganisationDetailFormGroupBuilder {
  static getFormGroup(version?: BusinessOrganisationVersion): FormGroup {
    return new FormGroup<BusinessOrganisationDetailFormGroup>(
      {
        descriptionDe: new FormControl(version?.descriptionDe, [
          Validators.required,
          AtlasFieldLengthValidator.length_60,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        descriptionFr: new FormControl(version?.descriptionFr, [
          Validators.required,
          AtlasFieldLengthValidator.length_60,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        descriptionIt: new FormControl(version?.descriptionIt, [
          Validators.required,
          AtlasFieldLengthValidator.length_60,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        descriptionEn: new FormControl(version?.descriptionEn, [
          Validators.required,
          AtlasFieldLengthValidator.length_60,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        abbreviationDe: new FormControl(version?.abbreviationDe, [
          Validators.required,
          AtlasFieldLengthValidator.length_10,
          AtlasCharsetsValidator.iso88591,
        ]),
        abbreviationFr: new FormControl(version?.abbreviationFr, [
          Validators.required,
          AtlasFieldLengthValidator.length_10,
          AtlasCharsetsValidator.iso88591,
        ]),
        abbreviationIt: new FormControl(version?.abbreviationIt, [
          Validators.required,
          AtlasFieldLengthValidator.length_10,
          AtlasCharsetsValidator.iso88591,
        ]),
        abbreviationEn: new FormControl(version?.abbreviationEn, [
          Validators.required,
          AtlasFieldLengthValidator.length_10,
          AtlasCharsetsValidator.iso88591,
        ]),
        organisationNumber: new FormControl(version?.organisationNumber, [
          Validators.required,
          AtlasCharsetsValidator.numeric,
          Validators.min(0),
          Validators.max(99999),
        ]),
        contactEnterpriseEmail: new FormControl(
          version?.contactEnterpriseEmail,
          [AtlasFieldLengthValidator.length_255, AtlasCharsetsValidator.email]
        ),
        businessTypes: new FormControl(version?.businessTypes),
        validFrom: new FormControl(
          version?.validFrom ? moment(version?.validFrom) : null,
          [Validators.required]
        ),
        validTo: new FormControl(
          version?.validTo ? moment(version?.validTo) : null,
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
