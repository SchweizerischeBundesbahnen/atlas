import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import {
  GeographyFormGroup,
  GeographyFormGroupBuilder,
} from '../../geography/geography-form-group';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { ReadSectorVersion } from '../../../../api/model/readSectorVersion';

export interface SectorDetailFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  designation: FormControl<string | null | undefined>;
  trafficPointSloid: FormControl<string | null | undefined>;
  length: FormControl<number | null | undefined>;
  edgeHeight: FormControl<number | null | undefined>;
  sectorGeolocation?: FormGroup<GeographyFormGroup>;
}

export class SectorFormGroupBuilder {
  static buildFormGroup(
    sectorVersion?: ReadSectorVersion
  ): FormGroup<SectorDetailFormGroup> {
    return new FormGroup<SectorDetailFormGroup>(
      {
        sloid: new FormControl(sectorVersion?.sloid),
        trafficPointSloid: new FormControl(sectorVersion?.trafficPointSloid),
        designation: new FormControl(sectorVersion?.designation, [
          Validators.required,
          Validators.maxLength(8),
        ]),
        length: new FormControl(sectorVersion?.length, [
          AtlasCharsetsValidator.decimalWithDigits(6, 3),
          Validators.min(0),
        ]),
        edgeHeight: new FormControl(sectorVersion?.edgeHeight, [
          AtlasCharsetsValidator.numeric,
          Validators.max(999),
        ]),
        sectorGeolocation: GeographyFormGroupBuilder.buildFormGroup(
          sectorVersion?.sectorGeolocation
        ),
        validFrom: new FormControl(
          sectorVersion?.validFrom ? moment(sectorVersion.validFrom) : null,
          [Validators.required]
        ),
        validTo: new FormControl(
          sectorVersion?.validTo ? moment(sectorVersion.validTo) : null,
          [Validators.required]
        ),
        etagVersion: new FormControl(sectorVersion?.etagVersion),
        creationDate: new FormControl(sectorVersion?.creationDate),
        editionDate: new FormControl(sectorVersion?.editionDate),
        editor: new FormControl(sectorVersion?.editor),
        creator: new FormControl(sectorVersion?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }
}
