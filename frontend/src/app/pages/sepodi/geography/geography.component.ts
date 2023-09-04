import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { Subscription } from 'rxjs';
import { MapService } from '../map/map.service';

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnInit, OnDestroy {
  @Input() disabled = false;
  @Input() formGroup!: FormGroup<GeographyFormGroup>;

  transformedCoordinatePair?: CoordinatePair;
  private spatialReferenceSubscription!: Subscription;
  private geographyCoordinatesSubscription!: Subscription;

  constructor(
    private coordinateTransformationService: CoordinateTransformationService,
    private mapService: MapService
  ) {}

  ngOnInit() {
    this.initTransformedCoordinatePair();
    this.spatialReferenceSubscription = this.formGroup.valueChanges.subscribe(() => {
      this.initTransformedCoordinatePair();
    });
    this.geographyCoordinatesSubscription = this.mapService.clickedCoordinates.subscribe((data) => {
      if (data.length != 0) {
        this.formGroup.controls.east.setValue(data[0]);
        this.formGroup.controls.north.setValue(data[1]);
      }
    });
    console.log('formgroup ', this.formGroup);
  }

  ngOnDestroy() {
    this.spatialReferenceSubscription.unsubscribe();
    this.geographyCoordinatesSubscription.unsubscribe();
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
}
