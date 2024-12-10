import {BaseDetailFormGroup} from '../../../../core/components/base-detail/base-detail-form-group';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ReadSublineVersionV2, SublineConcessionType, SublineType, SublineVersionV2} from '../../../../api';
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {WhitespaceValidator} from "../../../../core/validation/whitespace/whitespace-validator";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {DateRangeValidator} from "../../../../core/validation/date-range/date-range-validator";
import moment from 'moment';

export interface SublineFormGroup extends BaseDetailFormGroup {
  swissSublineNumber: FormControl<string | null | undefined>;
  mainlineSlnid: FormControl<string | null | undefined>;
  slnid: FormControl<string | null | undefined>;
  status: FormControl<string | null | undefined>;
  sublineType: FormControl<SublineType | null | undefined>;
  sublineConcessionType: FormControl<SublineConcessionType | null | undefined>;
  businessOrganisation: FormControl<string | null | undefined>;
  longName: FormControl<string | null | undefined>;
  description: FormControl<string | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
}

export class SublineFormGroupBuilder {
  static buildFormGroup(version?: ReadSublineVersionV2): FormGroup {
    const formGroup = new FormGroup<SublineFormGroup>(
      {
        swissSublineNumber: new FormControl(version?.swissSublineNumber, [
          AtlasFieldLengthValidator.length_50,
          AtlasCharsetsValidator.sid4pt,
        ]),
        mainlineSlnid: new FormControl(version?.mainlineSlnid, [Validators.required]),
        slnid: new FormControl(version?.slnid),
        status: new FormControl(version?.status),
        sublineType: new FormControl(version?.sublineType, [Validators.required]),
        sublineConcessionType: new FormControl(version?.sublineConcessionType),
        businessOrganisation: new FormControl(version?.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        longName: new FormControl(version?.longName, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        description: new FormControl(version?.description, [
          Validators.required,
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        validFrom: new FormControl(version?.validFrom ? moment(version.validFrom) : null,
          [Validators.required],
        ),
        validTo: new FormControl(version?.validTo ? moment(version.validTo) : null, [
          Validators.required,
        ]),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
    this.initConcessionTypeConditionalValidation(formGroup)
    return formGroup;
  }

  static initConcessionTypeConditionalValidation(formGroup: FormGroup<SublineFormGroup>) {
    formGroup.controls.sublineType.valueChanges.subscribe(newType => {
      if (newType === SublineType.Concession) {
        formGroup.controls.sublineConcessionType.setValidators([Validators.required]);
        formGroup.controls.swissSublineNumber.addValidators([Validators.required]);
      } else {
        formGroup.controls.sublineConcessionType.clearValidators();
        formGroup.controls.sublineConcessionType.setValue(undefined);

        formGroup.controls.swissSublineNumber.removeValidators([Validators.required]);
        formGroup.controls.swissSublineNumber.setValue(undefined);
      }
      formGroup.controls.sublineConcessionType.updateValueAndValidity();
      formGroup.controls.swissSublineNumber.updateValueAndValidity();
      formGroup.updateValueAndValidity();
    })
  }
}
