import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Geolocation, SpatialReference } from '../../../api';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { LV95_MAX_DIGITS, WGS84_MAX_DIGITS } from './geography.component';

export interface GeographyFormGroup {
  east: FormControl<number | null | undefined>;
  north: FormControl<number | null | undefined>;
  height: FormControl<number | null | undefined>;
  spatialReference: FormControl<SpatialReference | null | undefined>;
}

export class GeographyFormGroupBuilder {
  static buildFormGroup(geolocation?: Geolocation) {
    const formGroup = new FormGroup<GeographyFormGroup>({
      east: new FormControl(
        this.getCoordinates(geolocation)?.east,
        this.getValidatorsForCoordinates(geolocation?.spatialReference, 'EAST'),
      ),
      north: new FormControl(
        this.getCoordinates(geolocation)?.north,
        this.getValidatorsForCoordinates(geolocation?.spatialReference, 'NORTH'),
      ),
      height: new FormControl(geolocation?.height, [
        AtlasCharsetsValidator.integerWithFraction(5, 4),
      ]),
      spatialReference: new FormControl(geolocation?.spatialReference ?? SpatialReference.Lv95),
    });
    this.initConditionalLocationValidators(formGroup);
    return formGroup;
  }

  private static getCoordinates(geolocation?: Geolocation) {
    if (geolocation?.spatialReference === SpatialReference.Wgs84) {
      return geolocation?.wgs84;
    }
    return geolocation?.lv95;
  }

  private static initConditionalLocationValidators(formGroup: FormGroup<GeographyFormGroup>) {
    formGroup.controls.spatialReference.valueChanges.subscribe((newSpatialReference) => {
      formGroup.controls.east.setValidators(
        this.getValidatorsForCoordinates(newSpatialReference, 'EAST'),
      );
      formGroup.controls.north.setValidators(
        this.getValidatorsForCoordinates(newSpatialReference, 'NORTH'),
      );
    });
  }

  private static getValidatorsForCoordinates(
    spatialReference: SpatialReference | null = SpatialReference.Lv95,
    northOrEast: 'NORTH' | 'EAST',
  ) {
    if (spatialReference === SpatialReference.Lv95) {
      return [Validators.required, AtlasCharsetsValidator.decimalWithDigits(LV95_MAX_DIGITS)];
    }
    if (spatialReference === SpatialReference.Wgs84) {
      const minMax = northOrEast === 'NORTH' ? 90 : 180;
      return [
        Validators.required,
        Validators.min(-minMax),
        Validators.max(minMax),
        AtlasCharsetsValidator.decimalWithDigits(WGS84_MAX_DIGITS),
      ];
    }
    return [];
  }
}
