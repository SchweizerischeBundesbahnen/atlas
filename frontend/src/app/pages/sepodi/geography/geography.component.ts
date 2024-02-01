import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, GeoDataService, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { debounceTime, merge, Subject } from 'rxjs';
import { MapService } from '../map/map.service';
import { MatRadioChange } from '@angular/material/radio';
import { takeUntil } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnDestroy, OnChanges {
  readonly LV95_MAX_DIGITS = LV95_MAX_DIGITS;
  readonly WGS84_MAX_DIGITS = WGS84_MAX_DIGITS;

  _form?: FormGroup<GeographyFormGroup>;
  @Input() set form(form: FormGroup<GeographyFormGroup> | undefined) {
    this._form = form;

    if (form) {
      this._geographyActive = true;
      this.updateMapInteractionMode();
      this.onChangeCoordinatesManually(this.currentCoordinates!);
      merge(form.controls.east.valueChanges, form.controls.north.valueChanges)
        .pipe(debounceTime(500), takeUntil(this.formDestroy$))
        .subscribe(() => {
          this.onChangeCoordinatesManually(this.currentCoordinates!);
          this.coordinatesChanged.emit(this.currentCoordinates);
        });
    } else {
      this._geographyActive = false;
      this.formDestroy$.next();
    }
  }

  @Input() editMode = false;
  @Output() geographyChanged = new EventEmitter<boolean>();
  @Output() coordinatesChanged = new EventEmitter<CoordinatePair>();

  private _geographyActive = false;

  get geographyActive() {
    return this._geographyActive;
  }

  set geographyActive(value: boolean) {
    this._geographyActive = value;
    this.geographyChanged.emit(value);
    this.updateMapInteractionMode();
  }

  transformedCoordinatePair?: CoordinatePair;

  private formDestroy$ = new Subject<void>();

  constructor(
    private coordinateTransformationService: CoordinateTransformationService,
    private mapService: MapService,
    private changeDetector: ChangeDetectorRef,
    private readonly geoDataService: GeoDataService,
  ) {
    this.mapService.clickedGeographyCoordinates
      .pipe(takeUntilDestroyed())
      .subscribe((coordinatePairWGS84) => {
        this.onMapClick({
          north: coordinatePairWGS84.lat,
          east: coordinatePairWGS84.lng,
          spatialReference: SpatialReference.Wgs84,
        });
      });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.editMode) {
      this.updateMapInteractionMode();
    }
    if (changes.form) {
      this.initTransformedCoordinatePair();
    }
  }

  ngOnDestroy() {
    this.mapService.exitCoordinateSelectionMode();
    this.formDestroy$.next();
    this.formDestroy$.unsubscribe();
  }

  setFormGroupValue(coordinates?: CoordinatePair) {
    if (!this._form || !coordinates) {
      return;
    }

    const maxDigits =
      this.currentSpatialReference === SpatialReference.Lv95
        ? this.LV95_MAX_DIGITS
        : this.WGS84_MAX_DIGITS;

    const roundedEast = Number(coordinates.east.toFixed(maxDigits));
    const roundedNorth = Number(coordinates.north.toFixed(maxDigits));

    this._form.patchValue({
      east: roundedEast,
      north: roundedNorth,
    });
    this._form.markAsDirty();
  }

  initTransformedCoordinatePair() {
    if (!this.currentCoordinates) return;
    this.transformedCoordinatePair = this.coordinateTransformationService.transform(
      this.currentCoordinates,
      this.transformedSpatialReference,
    );
    this.setHeightFromGeoData(this.transformedCoordinatePair!);
    this.changeDetector.detectChanges();
  }

  get transformedSpatialReference() {
    return this.currentSpatialReference === SpatialReference.Lv95
      ? SpatialReference.Wgs84
      : SpatialReference.Lv95;
  }

  get currentSpatialReference(): SpatialReference | null | undefined {
    return this._form?.controls.spatialReference.value;
  }

  get currentCoordinates(): CoordinatePair | undefined {
    if (!this._form) return;
    return {
      east: Number(this._form.value.east),
      north: Number(this._form.value.north),
      spatialReference: this.currentSpatialReference!,
    };
  }

  switchSpatialReference($event: MatRadioChange) {
    if (!$event.value) {
      return;
    }
    const previousCoordinatePair = this.currentCoordinates!;
    previousCoordinatePair.spatialReference = this.transformedSpatialReference;

    const transformedCoordinatePair = this.coordinateTransformationService.transform(
      previousCoordinatePair,
      this.currentSpatialReference!,
    );

    this.setFormGroupValue(transformedCoordinatePair);
    this.initTransformedCoordinatePair();
  }

  onChangeCoordinatesManually(coordinates: CoordinatePair) {
    if (this.currentSpatialReference === SpatialReference.Lv95) {
      coordinates = this.coordinateTransformationService.transform(
        coordinates,
        SpatialReference.Wgs84,
      )!;
    }
    if (coordinates && coordinates.north && coordinates.east) {
      this.mapService.placeMarkerAndFlyTo({ lat: coordinates.north, lng: coordinates.east });
      this.initTransformedCoordinatePair();
    }
  }

  onMapClick(coordinatesWgs84: CoordinatePair) {
    if (this.currentSpatialReference === SpatialReference.Lv95) {
      coordinatesWgs84 = this.coordinateTransformationService.transform(
        coordinatesWgs84,
        SpatialReference.Lv95,
      )!;
    }

    this.setFormGroupValue(coordinatesWgs84);
    this.initTransformedCoordinatePair();
  }

  private updateMapInteractionMode() {
    if (!this.mapService.mapInitialized.value) return;
    if (this.editMode && this.geographyActive) {
      this.mapService.enterCoordinateSelectionMode();
    } else {
      this.mapService.exitCoordinateSelectionMode();
    }
  }

  public setHeightFromGeoData(coordinatePair: CoordinatePair) {
    if (coordinatePair) {
      this.geoDataService.getLocationInformation(coordinatePair).subscribe((value) => {
        this._form?.patchValue({
          height: value.height,
        });
      });
    }
  }
}
