import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { debounceTime, merge, Subscription } from 'rxjs';
import { MapService } from '../map/map.service';
import { MatRadioChange } from '@angular/material/radio';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnInit, OnDestroy, OnChanges {
  readonly LV95_MAX_DIGITS = LV95_MAX_DIGITS;
  readonly WGS84_MAX_DIGITS = WGS84_MAX_DIGITS;

  @Input() formGroup!: FormGroup<GeographyFormGroup>;
  @Input() editMode = false;

  private _geographyActive = true;

  @Output()
  geographyActiveChange = new EventEmitter<boolean>();

  @Input()
  get geographyActive() {
    return this._geographyActive;
  }

  set geographyActive(value: boolean) {
    this._geographyActive = value;
    this.geographyActiveChange.emit(value);

    if (this.editMode) {
      if (this.geographyActive) {
        this.formGroup.enable();
      } else {
        this.formGroup.disable();
      }
      this.updateMapInteractionMode();
    }
  }

  transformedCoordinatePair?: CoordinatePair;

  private spatialReferenceSubscription!: Subscription;
  private clickedGeographyCoordinatesSubscription!: Subscription;

  constructor(
    private coordinateTransformationService: CoordinateTransformationService,
    private mapService: MapService,
  ) {}

  ngOnInit() {
    this.clickedGeographyCoordinatesSubscription?.unsubscribe();
    this.clickedGeographyCoordinatesSubscription =
      this.mapService.clickedGeographyCoordinates.subscribe((coordinatePairWGS84) => {
        this.onMapClick({
          north: coordinatePairWGS84.lat,
          east: coordinatePairWGS84.lng,
          spatialReference: SpatialReference.Wgs84,
        });
      });
  }

  ngOnChanges(): void {
    this.updateMapInteractionMode();
    this.initTransformedCoordinatePair();

    this.spatialReferenceSubscription?.unsubscribe();
    this.spatialReferenceSubscription = merge(
      this.formGroup!.controls.east.valueChanges,
      this.formGroup!.controls.north.valueChanges,
    )
      .pipe(debounceTime(500))
      .subscribe(() => {
        this.onChangeCoordinatesManually({
          east: Number(this.formGroup!.controls.east.value),
          north: Number(this.formGroup!.controls.north.value),
          spatialReference: this.currentSpatialReference!,
        });
      });
  }

  setFormGroupValue(coordinates: CoordinatePair) {
    const maxDigits =
      this.currentSpatialReference === SpatialReference.Lv95
        ? this.LV95_MAX_DIGITS
        : this.WGS84_MAX_DIGITS;

    const roundedEast = Number(coordinates.east.toFixed(maxDigits));
    const roundedNorth = Number(coordinates.north.toFixed(maxDigits));

    this.formGroup!.controls.east.setValue(roundedEast);
    this.formGroup!.controls.north.setValue(roundedNorth);
    this.formGroup!.markAsDirty();
  }

  ngOnDestroy() {
    this.spatialReferenceSubscription?.unsubscribe();
    this.clickedGeographyCoordinatesSubscription?.unsubscribe();
    this.mapService.exitCoordinateSelectionMode();
  }

  initTransformedCoordinatePair() {
    if (
      !this.coordinateTransformationService.isCoordinatesPairValidForTransformation(
        this.currentCoordinates,
      )
    ) {
      return;
    }
    this.transformedCoordinatePair = this.coordinateTransformationService.transform(
      this.currentCoordinates,
      this.transformedSpatialReference,
    );
  }

  get transformedSpatialReference() {
    return this.currentSpatialReference === SpatialReference.Lv95
      ? SpatialReference.Wgs84
      : SpatialReference.Lv95;
  }

  get currentSpatialReference(): SpatialReference {
    return this.formGroup!.controls.spatialReference.value!;
  }

  get currentCoordinates(): CoordinatePair {
    return {
      east: Number(this.formGroup!.value.east),
      north: Number(this.formGroup!.value.north),
      spatialReference: this.currentSpatialReference,
    };
  }

  switchSpatialReference($event: MatRadioChange) {
    const previousCoordinatePair = this.currentCoordinates;
    previousCoordinatePair.spatialReference = this.transformedSpatialReference;
    if (
      !$event.value ||
      !this.coordinateTransformationService.isCoordinatesPairValidForTransformation(
        previousCoordinatePair,
      )
    ) {
      return;
    }

    const transformedCoordinatePair = this.coordinateTransformationService.transform(
      previousCoordinatePair,
      this.currentSpatialReference,
    );

    this.setFormGroupValue(transformedCoordinatePair);
    this.initTransformedCoordinatePair();
  }

  onChangeCoordinatesManually(coordinates: CoordinatePair) {
    if (!this.coordinateTransformationService.isValidCoordinatePair(coordinates)) {
      return;
    }

    if (this.currentSpatialReference === SpatialReference.Lv95) {
      coordinates = this.coordinateTransformationService.transform(
        coordinates,
        SpatialReference.Wgs84,
      );
    }

    if (this.coordinateTransformationService.isCoordinatesPairValidForTransformation(coordinates)) {
      const coordinatePairWGS84 = { lat: coordinates.north, lng: coordinates.east };
      this.mapService.placeMarkerAndFlyTo(coordinatePairWGS84);
    }
    this.initTransformedCoordinatePair();
  }

  onMapClick(coordinates: CoordinatePair) {
    if (
      !this.coordinateTransformationService.isCoordinatesPairValidForTransformation(coordinates)
    ) {
      return;
    }

    if (this.currentSpatialReference === SpatialReference.Lv95) {
      coordinates = this.coordinateTransformationService.transform(
        coordinates,
        SpatialReference.Lv95,
      );
    }

    this.setFormGroupValue(coordinates);
    this.initTransformedCoordinatePair();
  }

  private updateMapInteractionMode() {
    this.mapService.mapInitialized.subscribe((initialized) => {
      if (initialized) {
        if (this.editMode && this.geographyActive) {
          this.mapService.enterCoordinateSelectionMode();
        } else {
          this.mapService.exitCoordinateSelectionMode();
        }
      }
    });
  }
}
