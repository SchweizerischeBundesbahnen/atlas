import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { Subscription } from 'rxjs';
import { MapService } from '../map/map.service';
import { MatRadioChange } from '@angular/material/radio';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnInit, OnDestroy {
  @Input() disabled = false;
  @Input() formGroup!: FormGroup<GeographyFormGroup>;
  initFormGroup!: FormGroup<GeographyFormGroup>;

  readonly LV95_MAX_DIGITS = LV95_MAX_DIGITS;
  readonly WGS84_MAX_DIGITS = WGS84_MAX_DIGITS;

  transformedCoordinatePair?: CoordinatePair;
  private spatialReferenceSubscription!: Subscription;
  private clickedGeographyCoordinatesSubscription!: Subscription;

  constructor(
    private coordinateTransformationService: CoordinateTransformationService,
    private mapService: MapService
  ) {}

  ngOnInit() {
    this.initFormGroup = this.formGroup;
    this.initTransformedCoordinatePair();
    this.spatialReferenceSubscription = this.formGroup.valueChanges.subscribe(() => {
      this.initTransformedCoordinatePair();
    });
    this.clickedGeographyCoordinatesSubscription =
      this.mapService.clickedGeographyCoordinates.subscribe((data) => {
        if (data.north != 0 && data.east != 0) {
          const lat = Number(data.east.toFixed(4));
          const lng = Number(data.north.toFixed(4));

          this.formGroup.controls.east.setValue(lat);
          this.formGroup.controls.north.setValue(lng);
          this.formGroup.markAsDirty();
        } else {
          this.formGroup = this.initFormGroup;
        }
      });
  }

  ngOnDestroy() {
    this.spatialReferenceSubscription.unsubscribe();
    this.clickedGeographyCoordinatesSubscription.unsubscribe();
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
