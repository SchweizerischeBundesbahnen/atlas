import { Component, Input, OnChanges, OnDestroy, OnInit } from '@angular/core';
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
          east: this.formGroup.controls.east.value!,
          north: this.formGroup.controls.north.value!,
        });
      });
  }
  get formGroup() {
    return this._formGroup;
  }
  @Input() disabled = false;
  @Input() spatialReference!: SpatialReference;

  _formGroup!: FormGroup<GeographyFormGroup>;

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
    this.spatialReference = this.currentSpatialReference;
    this.clickedGeographyCoordinatesSubscription =
      this.mapService.clickedGeographyCoordinates.subscribe((latLngCoordinates) => {
        this.onMapClick(latLngCoordinates);
      });
  }

  setFormGroupValue(lat: number, lng: number) {
    const FIXED_NUMBER = 5;
    const roundedLat = Number(lat.toFixed(FIXED_NUMBER));
    const roundedLng = Number(lng.toFixed(FIXED_NUMBER));

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

  private initTransformedCoordinatePair() {
    if (
      this.spatialReference &&
      !this.isLatLngNaN(this.currentCoordinates!) &&
      this.isEastNorthGreaterThanZero(this.currentCoordinates!)
    ) {
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

  get currentCoordinates(): CoordinatePair {
    return {
      east: Number(this.formGroup.value.east),
      north: Number(this.formGroup.value.north),
    };
  }

  switchSpatialReference($event: MatRadioChange) {
    if ($event.value && this.isEastNorthGreaterThanZero(this.currentCoordinates!)) {
      const newReference: SpatialReference = $event.value;
      let transformedCoordinatePair;
      this.spatialReference = newReference;
      transformedCoordinatePair = this.coordinateTransformationService.transform(
        this.currentCoordinates!,
        this.transformedSpatialReference,
        newReference
      );
      const transformedCoordinates = this.getTransformedCoordinates(
        transformedCoordinatePair!,
        newReference
      );
      this.formGroup.patchValue(transformedCoordinates, { emitEvent: false });
      this.initTransformedCoordinatePair();
    }
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
    let latLngCoordinates: LatLngCoordinates = {
      lat: Number(coordinates.north!),
      lng: Number(coordinates.east!),
    };

    if (
      this.spatialReference === SpatialReference.Lv95 &&
      this.isLatLngGreaterThanZero(latLngCoordinates)
    ) {
      const { north, east } = this.coordinateTransformationService.transform(
        { north: latLngCoordinates.lat, east: latLngCoordinates.lng },
        SpatialReference.Lv95,
        SpatialReference.Wgs84
      );

      latLngCoordinates = {
        lat: north,
        lng: east,
      };
    }

    if (this.isValidLatLng(latLngCoordinates) && this.isLatLngGreaterThanZero(latLngCoordinates)) {
      this.mapService.placeMarkerAndFlyTo(latLngCoordinates);
    }
  }

  onMapClick(coordinates: LatLngCoordinates) {
    if (this.isLatLngGreaterThanZero(coordinates)) {
      let latLngCoordinates = coordinates;
      if (this.spatialReference === SpatialReference.Lv95) {
        const { north, east } = this.coordinateTransformationService.transform(
          { north: coordinates.lat, east: coordinates.lng },
          SpatialReference.Wgs84,
          SpatialReference.Lv95
        );
        latLngCoordinates = { lat: north, lng: east };
      }
      this.setFormGroupValue(latLngCoordinates.lat, latLngCoordinates.lng);
      this.initTransformedCoordinatePair();
    }
  }

  isLatLngGreaterThanZero(coordinates: LatLngCoordinates): boolean {
    return coordinates.lat !== 0 && coordinates.lng !== 0;
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

  isLatLngNaN(coordinates: CoordinatePair): boolean {
    return isNaN(coordinates.east) || isNaN(coordinates.north);
  }
}
