import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnInit, OnDestroy {
  @Input() disabled = false;
  @Input() formGroup!: FormGroup<GeographyFormGroup>;

  transformedCoordinatePair!: CoordinatePair;
  private spatialReferenceSubscription!: Subscription;

  constructor(private coordinateTransformationService: CoordinateTransformationService) {}

  ngOnInit() {
    this.initTransformedCoordinatePair(this.currentSpatialReference);
    this.spatialReferenceSubscription =
      this.formGroup.controls.spatialReference.valueChanges.subscribe((changedReference) => {
        this.initTransformedCoordinatePair(changedReference!);
      });
  }

  ngOnDestroy() {
    this.spatialReferenceSubscription.unsubscribe();
  }

  private initTransformedCoordinatePair(currentSpactialReference: SpatialReference) {
    this.transformedCoordinatePair = this.coordinateTransformationService.transform(
      {
        east: this.formGroup.value.east!,
        north: this.formGroup.value.north!,
      },
      currentSpactialReference,
      this.transformedSpatialReference
    );
  }

  get transformedSpatialReference() {
    return this.currentSpatialReference === SpatialReference.Lv95
      ? SpatialReference.Wgs84
      : SpatialReference.Lv95;
  }

  get currentSpatialReference() {
    return this.formGroup.controls.spatialReference.value!;
  }
}
