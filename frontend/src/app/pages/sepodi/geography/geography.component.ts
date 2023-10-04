import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {CoordinatePair, SpatialReference} from '../../../api';
import {GeographyFormGroup} from './geography-form-group';
import {CoordinateTransformationService} from './coordinate-transformation.service';
import {Subscription} from 'rxjs';
import {MatRadioChange} from '@angular/material/radio';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnInit, OnDestroy {
  @Input() disabled = false;
  @Input() formGroup!: FormGroup<GeographyFormGroup>;

  readonly LV95_MAX_DIGITS = LV95_MAX_DIGITS;
  readonly WGS84_MAX_DIGITS = WGS84_MAX_DIGITS;

  transformedCoordinatePair?: CoordinatePair;
  private spatialReferenceSubscription!: Subscription;

  constructor(private coordinateTransformationService: CoordinateTransformationService) {}

  ngOnInit() {
    this.initTransformedCoordinatePair();
    this.spatialReferenceSubscription = this.formGroup.valueChanges.subscribe(() => {
      this.initTransformedCoordinatePair();
    });
  }

  ngOnDestroy() {
    this.spatialReferenceSubscription.unsubscribe();
  }

  private initTransformedCoordinatePair() {
    if (
      this.formGroup.value.spatialReference &&
      this.currentCoordinates.east &&
      this.currentCoordinates.north
    ) {
      this.transformedCoordinatePair = this.coordinateTransformationService.transform(
        this.currentCoordinates,
        this.currentSpatialReference,
        this.transformedSpatialReference
      );
    }
  }

  get transformedSpatialReference() {
    return this.currentSpatialReference === SpatialReference.Lv95
      ? SpatialReference.Wgs84
      : SpatialReference.Lv95;
  }

  get currentSpatialReference() {
    return this.formGroup.controls.spatialReference.value!;
  }

  get currentCoordinates(): CoordinatePair {
    return {
      east: Number(this.formGroup.value.east!),
      north: Number(this.formGroup.value.north!),
    };
  }

  switchSpatialReference($event: MatRadioChange) {
    const newReference: SpatialReference = $event.value;
    if (newReference) {
      const transformedCoordinatePair = this.coordinateTransformationService.transform(
        this.currentCoordinates,
        this.transformedSpatialReference,
        newReference
      );
      this.formGroup.controls.east.setValue(
        Number(
          transformedCoordinatePair.east.toFixed(
            newReference == SpatialReference.Lv95 ? this.LV95_MAX_DIGITS : this.WGS84_MAX_DIGITS
          )
        )
      );
      this.formGroup.controls.north.setValue(
        Number(
          transformedCoordinatePair.north.toFixed(
            newReference == SpatialReference.Lv95 ? this.LV95_MAX_DIGITS : this.WGS84_MAX_DIGITS
          )
        )
      );
    }
  }
}
