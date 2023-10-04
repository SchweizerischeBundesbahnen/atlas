import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { Subscription, debounceTime, merge } from 'rxjs';
import { MapService } from '../map/map.service';
import { MatRadioChange } from '@angular/material/radio';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnDestroy {
  @Input()
  set formGroup(formGroup: FormGroup<GeographyFormGroup>) {
    this._formGroup = formGroup;
  }

  get formGroup() {
    return this._formGroup;
  }

  _formGroup!: FormGroup<GeographyFormGroup>;
  spatialReference!: SpatialReference;

  @Output() currentSpatialReferenceEvent = new EventEmitter();

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
    this.initTransformedCoordinatePair();
    this.spatialReference = this.currentSpatialReference;
    this.clickedGeographyCoordinatesSubscription =
      this.mapService.clickedGeographyCoordinates.subscribe((coordinatePairWGS84) => {
        this.onMapClick({ north: coordinatePairWGS84.lat, east: coordinatePairWGS84.lng });
      });

    this.spatialReferenceSubscription = merge(
      this.formGroup.controls.east.valueChanges,
      this.formGroup.controls.north.valueChanges
    )
      .pipe(debounceTime(500))
      .subscribe(() => {
        this.onChangeCoordinatesManually({
          east: Number(this.formGroup.controls.east.value!),
          north: Number(this.formGroup.controls.north.value!),
        });
      });
  }

  setFormGroupValue(coordinates: CoordinatePair) {
    const maxDigits =
      this.spatialReference === SpatialReference.Lv95
        ? this.LV95_MAX_DIGITS
        : this.WGS84_MAX_DIGITS;

    const roundedEast = Number(coordinates.east.toFixed(maxDigits));
    const roundedNorth = Number(coordinates.north.toFixed(maxDigits));

    this.formGroup.controls.east.setValue(roundedEast);
    this.formGroup.controls.north.setValue(roundedNorth);
    this.formGroup.markAsDirty();
  }

  ngOnDestroy() {
    this.spatialReferenceSubscription?.unsubscribe();
    this.clickedGeographyCoordinatesSubscription?.unsubscribe();
  }

  initTransformedCoordinatePair() {
    if (!this.isCoordinatesPairValidForTransformation(this.currentCoordinates)) return;

    this.spatialReference = this.currentSpatialReference;
    this.currentSpatialReferenceEvent.emit(this.spatialReference);

    if (this.spatialReference) {
      this.transformedCoordinatePair = this.coordinateTransformationService.transform(
        this.currentCoordinates!,
        this.spatialReference,
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
    if (!$event.value) return;

    this.spatialReference = $event.value;

    if (!this.isCoordinatesPairValidForTransformation(this.currentCoordinates)) return;

    const transformedCoordinatePair = this.coordinateTransformationService.transform(
      this.currentCoordinates!,
      this.transformedSpatialReference,
      this.spatialReference
    );

    const maxDigits =
      this.spatialReference === SpatialReference.Lv95
        ? this.LV95_MAX_DIGITS
        : this.WGS84_MAX_DIGITS;

    const roundedNorth = Number(transformedCoordinatePair.north.toFixed(maxDigits));
    const roundedEast = Number(transformedCoordinatePair.east.toFixed(maxDigits));

    this.formGroup.controls.north.setValue(roundedNorth);
    this.formGroup.controls.east.setValue(roundedEast);
    this.initTransformedCoordinatePair();
  }

  onChangeCoordinatesManually(coordinates: CoordinatePair) {
    if (!this.isCoordinatesPairValidForTransformation(coordinates)) return;

    if (this.spatialReference === SpatialReference.Lv95) {
      const transformed = this.coordinateTransformationService.transform(
        coordinates,
        SpatialReference.Lv95,
        SpatialReference.Wgs84
      );
      coordinates = transformed;
    }

    if (this.isValidCoordinatePair(coordinates)) {
      const coordinatePairWGS84 = { lat: coordinates.north, lng: coordinates.east };
      this.mapService.placeMarkerAndFlyTo(coordinatePairWGS84);
    }
    this.initTransformedCoordinatePair();
  }

  onMapClick(coordinates: CoordinatePair) {
    if (!this.isCoordinatesPairValidForTransformation(coordinates)) return;

    if (this.spatialReference === SpatialReference.Lv95) {
      const transformed = this.coordinateTransformationService.transform(
        coordinates,
        SpatialReference.Wgs84,
        SpatialReference.Lv95
      );
      coordinates = transformed;
    }

    this.setFormGroupValue(coordinates);
    this.initTransformedCoordinatePair();
  }

  isCoordinatesPairValidForTransformation(coordinates: CoordinatePair) {
    return (
      this.isCoordinatesGreaterThanZero(coordinates) && !!coordinates.north && !!coordinates.east
    );
  }

  isCoordinatesGreaterThanZero(coordinates: CoordinatePair): boolean {
    return coordinates.east !== 0 && coordinates.north !== 0;
  }

  isValidCoordinatePair(coordinates: CoordinatePair): boolean {
    return (
      coordinates.north >= -90 &&
      coordinates.north <= 90 &&
      coordinates.east >= -180 &&
      coordinates.east <= 180
    );
  }
}
