import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { CreateSectorGroupVersion } from '../../../../api/model/createSectorGroupVersion';

export interface SectorGroupDetailFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  designation: FormControl<string | null | undefined>;
  trafficPointSloid: FormControl<string | null | undefined>;
  length: FormControl<number | null | undefined>;
  sectorSloids: FormControl<Set<string> | null | undefined>;
}

export class SectorGroupFormGroupBuilder {
  static buildFormGroup(
    sectorGroupVersion?: CreateSectorGroupVersion
  ): FormGroup<SectorGroupDetailFormGroup> {
    return new FormGroup<SectorGroupDetailFormGroup>(
      {
        sloid: new FormControl(sectorGroupVersion?.sloid),
        trafficPointSloid: new FormControl(
          sectorGroupVersion?.trafficPointSloid
        ),
        designation: new FormControl(sectorGroupVersion?.designation, [
          Validators.required,
          Validators.maxLength(8),
        ]),
        length: new FormControl(sectorGroupVersion?.length, [
          AtlasCharsetsValidator.decimalWithDigits(6, 3),
          Validators.min(0),
        ]),
        validFrom: new FormControl(
          sectorGroupVersion?.validFrom
            ? moment(sectorGroupVersion.validFrom)
            : null,
          [Validators.required]
        ),
        validTo: new FormControl(
          sectorGroupVersion?.validTo
            ? moment(sectorGroupVersion.validTo)
            : null,
          [Validators.required]
        ),
        sectorSloids: new FormControl(sectorGroupVersion?.sectorSloids, [
          Validators.required,
          Validators.minLength(2),
        ]),
        etagVersion: new FormControl(sectorGroupVersion?.etagVersion),
        creationDate: new FormControl(sectorGroupVersion?.creationDate),
        editionDate: new FormControl(sectorGroupVersion?.editionDate),
        editor: new FormControl(sectorGroupVersion?.editor),
        creator: new FormControl(sectorGroupVersion?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }
}
