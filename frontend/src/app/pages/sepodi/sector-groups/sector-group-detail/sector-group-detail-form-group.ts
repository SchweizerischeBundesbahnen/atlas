import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { SelectionValidator } from '../../../../core/validation/min-selected/selection-validator';
import { ReadSectorGroupVersion } from '../../../../api/model/readSectorGroupVersion';

export interface SectorGroupDetailFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  designation: FormControl<string | null | undefined>;
  trafficPointSloid: FormControl<string | null | undefined>;
  length: FormControl<number | null | undefined>;
  sectorSloids?: FormControl<string[] | null>;
}

export class SectorGroupFormGroupBuilder {
  private static createBaseControls(
    sectorGroupVersion?: ReadSectorGroupVersion
  ) {
    return {
      sloid: new FormControl(sectorGroupVersion?.sloid),
      trafficPointSloid: new FormControl(sectorGroupVersion?.trafficPointSloid),
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
        sectorGroupVersion?.validTo ? moment(sectorGroupVersion.validTo) : null,
        [Validators.required]
      ),
      etagVersion: new FormControl(sectorGroupVersion?.etagVersion),
      creationDate: new FormControl(sectorGroupVersion?.creationDate),
      editionDate: new FormControl(sectorGroupVersion?.editionDate),
      editor: new FormControl(sectorGroupVersion?.editor),
      creator: new FormControl(sectorGroupVersion?.creator),
    };
  }

  static buildFormGroupUpdate(
    sectorGroupVersion?: ReadSectorGroupVersion
  ): FormGroup<SectorGroupDetailFormGroup> {
    const controls = this.createBaseControls(sectorGroupVersion);
    return new FormGroup<SectorGroupDetailFormGroup>(
      controls as SectorGroupDetailFormGroup,
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }

  static buildFormGroupCreate(): FormGroup<SectorGroupDetailFormGroup> {
    const controls: SectorGroupDetailFormGroup = {
      ...this.createBaseControls(),
      sectorSloids: new FormControl(
        [],
        [Validators.required, SelectionValidator.minSelected(2)]
      ),
    };

    return new FormGroup<SectorGroupDetailFormGroup>(controls, [
      DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo'),
    ]);
  }
}
