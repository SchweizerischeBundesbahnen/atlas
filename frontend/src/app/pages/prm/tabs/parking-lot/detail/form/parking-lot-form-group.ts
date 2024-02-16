import {FormControl, FormGroup, Validators} from '@angular/forms';
import moment from 'moment';
import {BaseDetailFormGroup} from '../../../../../../core/components/base-detail/base-detail-form-group';
import {WhitespaceValidator} from '../../../../../../core/validation/whitespace/whitespace-validator';
import {DateRangeValidator} from '../../../../../../core/validation/date-range/date-range-validator';
import {BooleanOptionalAttributeType, ParkingLotVersion, ReadParkingLotVersion,} from '../../../../../../api';

export interface ParkingLotFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  designation: FormControl<string | null | undefined>;
  additionalInformation: FormControl<string | null | undefined>;
  placesAvailable: FormControl<BooleanOptionalAttributeType | null | undefined>;
  prmPlacesAvailable: FormControl<BooleanOptionalAttributeType | null | undefined>;
}

export class ParkingLotFormGroupBuilder {
  public static buildFormGroup(version?: ReadParkingLotVersion) {
    return new FormGroup<ParkingLotFormGroup>(
      {
        sloid: new FormControl(version?.sloid),
        additionalInformation: new FormControl(version?.additionalInformation, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        designation: new FormControl(version?.designation, [
          Validators.maxLength(50),
        ]),
        placesAvailable:new FormControl(version?.placesAvailable ?? BooleanOptionalAttributeType.ToBeCompleted,[Validators.required]),
        prmPlacesAvailable:new FormControl(version?.prmPlacesAvailable ?? BooleanOptionalAttributeType.ToBeCompleted,[Validators.required]),
        validFrom: new FormControl(version?.validFrom ? moment(version.validFrom) : null, [
          Validators.required,
        ]),
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
  }

  static getWritableForm(
    form: FormGroup<ParkingLotFormGroup>,
    parentServicePointSloid: string,
  ): ParkingLotVersion {
    return {
      sloid: form.value.sloid!,
      parentServicePointSloid: parentServicePointSloid,
      additionalInformation: form.value.additionalInformation!,
      designation: form.value.designation!,
      placesAvailable: form.value.placesAvailable!,
      prmPlacesAvailable: form.value.prmPlacesAvailable!,
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
