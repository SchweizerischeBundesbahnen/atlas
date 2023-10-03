import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { Subscription, debounceTime, merge } from 'rxjs';
import { LatLngCoordinates, MapService } from '../map/map.service';
import { MatRadioChange } from '@angular/material/radio';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnDestroy, OnChanges {
  @Input()
  set formGroup(formGroup: FormGroup<GeographyFormGroup>) {
    this._formGroup = formGroup;

    this.spatialReferenceSubscription?.unsubscribe();
    this.spatialReferenceSubscription = merge(
      this.formGroup.controls.east.valueChanges,
      this.formGroup.controls.north.valueChanges
    )
      .pipe(debounceTime(500))
      .subscribe(() => {
        this.initTransformedCoordinatePair();
        this.onChangeCoordinatesManually({
          east: Number(this.formGroup.controls.east.value!),
          north: Number(this.formGroup.controls.north.value!),
        });
      });
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
    this.clickedGeographyCoordinatesSubscription =
      this.mapService.clickedGeographyCoordinates.subscribe((latLngCoordinates) => {
        this.onMapClick(latLngCoordinates);
      });
  }

  setFormGroupValue(coordinates: LatLngCoordinates) {
    const FIXED_NUMBER = 5;
    const roundedLat = Number(coordinates.lat.toFixed(FIXED_NUMBER));
    const roundedLng = Number(coordinates.lng.toFixed(FIXED_NUMBER));

    this.formGroup.controls.east.setValue(roundedLng);
    this.formGroup.controls.north.setValue(roundedLat);
    this.formGroup.markAsDirty();
  }

  ngOnChanges() {
    this.spatialReference = this.currentSpatialReference;
    this.initTransformedCoordinatePair();
  }

  ngOnDestroy() {
    this.spatialReferenceSubscription?.unsubscribe();
    this.clickedGeographyCoordinatesSubscription?.unsubscribe();
  }

  initTransformedCoordinatePair() {
    this.currentSpatialReferenceEvent.emit(this.currentSpatialReference);
    const isCoordinatesValid = this.isCoordinatesPairValidForTransformation(
      this.currentCoordinates
    );
    if (this.spatialReference && isCoordinatesValid) {
      this.transformedCoordinatePair = this.coordinateTransformationService.transform(
        this.currentCoordinates!,
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

  set currentSpatialReference(currentSpatialReference) {
    this.spatialReference = currentSpatialReference;
  }

  get currentCoordinates(): CoordinatePair {
    return {
      east: Number(this.formGroup.value.east!),
      north: Number(this.formGroup.value.north!),
    };
  }

  switchSpatialReference($event: MatRadioChange) {
    this.spatialReference = $event.value;

    if (!$event.value) return;

    const isCoordinatesValid = this.isCoordinatesPairValidForTransformation(
      this.currentCoordinates
    );
    if (!isCoordinatesValid) return;

    const transformedCoordinatePair = this.coordinateTransformationService.transform(
      this.currentCoordinates!,
      this.transformedSpatialReference,
      this.spatialReference
    );

    const transformedCoordinates = this.getTransformedCoordinates(
      transformedCoordinatePair!,
      this.spatialReference
    );

    this.formGroup.patchValue(transformedCoordinates, { emitEvent: false });
    this.initTransformedCoordinatePair();
  }
  getTransformedCoordinates(
    transformedCoordinatePair: CoordinatePair,
    newReference: SpatialReference
  ) {
    const maxDigits =
      newReference === SpatialReference.Lv95 ? this.LV95_MAX_DIGITS : this.WGS84_MAX_DIGITS;
    return {
      east: Number(transformedCoordinatePair.east.toFixed(maxDigits)),
      north: Number(transformedCoordinatePair.north.toFixed(maxDigits)),
    };
  }

  onChangeCoordinatesManually(coordinates: CoordinatePair) {
    const latLngCoordinates: LatLngCoordinates = {
      lat: Number(coordinates.north!),
      lng: Number(coordinates.east!),
    };

    const isCoordinatesValid = this.isLatLngCoordinatesValidForTransformation(latLngCoordinates);

    if (this.spatialReference === SpatialReference.Lv95 && isCoordinatesValid) {
      const transformed = this.coordinateTransformationService.transform(
        coordinates,
        SpatialReference.Lv95,
        SpatialReference.Wgs84
      );
      latLngCoordinates.lat = transformed.north;
      latLngCoordinates.lng = transformed.east;
    }

    if (this.isValidLatLng(latLngCoordinates) && isCoordinatesValid) {
      this.mapService.placeMarkerAndFlyTo(latLngCoordinates);
    }
  }

  onMapClick(coordinates: LatLngCoordinates) {
    if (!this.isLatLngCoordinatesValidForTransformation(coordinates)) return;

    if (this.spatialReference === SpatialReference.Lv95) {
      const transformed = this.coordinateTransformationService.transform(
        { north: coordinates.lat, east: coordinates.lng },
        SpatialReference.Wgs84,
        SpatialReference.Lv95
      );
      coordinates = { lat: transformed.north, lng: transformed.east };
    }

    this.setFormGroupValue(coordinates);
    this.initTransformedCoordinatePair();
  }

  isLatLngCoordinatesValidForTransformation(coordinates: LatLngCoordinates) {
    return this.isLatLngGreaterThanZero(coordinates) && !!coordinates.lat && !!coordinates.lng;
  }

  isLatLngGreaterThanZero(coordinates: LatLngCoordinates): boolean {
    return coordinates.lat !== 0 && coordinates.lng !== 0;
  }

  isCoordinatesPairValidForTransformation(coordinates: CoordinatePair) {
    return (
      this.isEastNorthGreaterThanZero(coordinates) && !!coordinates.north && !!coordinates.east
    );
  }

  isEastNorthGreaterThanZero(coordinates: CoordinatePair): boolean {
    return coordinates.east !== 0 && coordinates.north !== 0;
  }

  isValidLatLng(coordinates: LatLngCoordinates): boolean {
    return (
      coordinates.lat >= -90 &&
      coordinates.lat <= 90 &&
      coordinates.lng >= -180 &&
      coordinates.lng <= 180
    );
  }
}
