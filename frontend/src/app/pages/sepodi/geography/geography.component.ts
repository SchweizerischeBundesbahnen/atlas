import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { Subscription, debounceTime } from 'rxjs';
import { MapService } from '../map/map.service';
import { MatRadioChange } from '@angular/material/radio';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

interface LatLngCoordinates {
  lat: number;
  lng: number;
}

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnInit, OnDestroy {
  @Input() disabled = false;
  @Input() formGroup!: FormGroup<GeographyFormGroup>;
  spatialReference: SpatialReference = SpatialReference.Lv95;

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
    this.spatialReferenceSubscription = this.formGroup.valueChanges
      .pipe(debounceTime(500))
      .subscribe((value) => {
        this.spatialReference = this.currentSpatialReference;
        this.onChangeCoordinatesManually({ east: value.east!, north: value.north! });
        this.initTransformedCoordinatePair();
      });

    this.clickedGeographyCoordinatesSubscription =
      this.mapService.clickedGeographyCoordinates.subscribe((latLngCoordinates) => {
        this.onMapClick(latLngCoordinates);
      });
  }

  setFormGroupValue(lat: number, lng: number) {
    const roundedLat = Number(lat.toFixed(4));
    const roundedLng = Number(lng.toFixed(4));

    this.formGroup.controls.east.setValue(roundedLng);
    this.formGroup.controls.north.setValue(roundedLat);
    this.formGroup.markAsDirty();
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
    if ($event.value) {
      const newReference: SpatialReference = $event.value;
      this.spatialReference = newReference;
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

  onChangeCoordinatesManually(coordinates: CoordinatePair) {
    let latLngCoordinates: LatLngCoordinates = {
      lat: Number(coordinates.north!),
      lng: Number(coordinates.east!),
    };

    if (this.spatialReference === SpatialReference.Lv95) {
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

    if (this.isValidLatLng(latLngCoordinates.lat, latLngCoordinates.lng)) {
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
    }
  }

  isLatLngGreaterThanZero(coordinates: LatLngCoordinates): boolean {
    return coordinates.lat !== 0 && coordinates.lng !== 0;
  }

  isValidLatLng(lat: number, lng: number): boolean {
    return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
  }
}
