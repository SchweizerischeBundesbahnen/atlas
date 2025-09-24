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
    version?: ReadSectorVersion
  ): FormGroup<SectorDetailFormGroup> {
    return new FormGroup<SectorDetailFormGroup>(
      {
        sloid: new FormControl(version?.sloid),
        trafficPointSloid: new FormControl(version?.trafficPointSloid),
        designation: new FormControl(version?.designation, [
          Validators.required,
          Validators.maxLength(8),
        ]),
        length: new FormControl(version?.length, [
          AtlasCharsetsValidator.decimalWithDigits(6, 3),
          Validators.min(0),
        ]),
        edgeHeight: new FormControl(version?.edgeHeight, [
          AtlasCharsetsValidator.numeric,
          Validators.max(999),
        ]),
        sectorGeolocation: GeographyFormGroupBuilder.buildFormGroup(
          version?.sectorGeolocation!
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
