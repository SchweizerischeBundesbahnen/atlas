import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CoordinatePair, GeoDataService, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { debounceTime, merge, Observable, Subject } from 'rxjs';
import { MapService } from '../map/map.service';
import { MatRadioChange } from '@angular/material/radio';
import { map, takeUntil } from 'rxjs/operators';
import { LocationInformation } from '../service-point-side-panel/service-point/location-information';
import { Countries } from '../../../core/country/Countries';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnInit, OnDestroy, OnChanges {
  readonly LV95_MAX_DIGITS = LV95_MAX_DIGITS;
  readonly WGS84_MAX_DIGITS = WGS84_MAX_DIGITS;

  currentLocationInfo$?: Observable<LocationInformation>;

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
          const coordinatePair = this.currentCoordinates;
          if (coordinatePair?.north && coordinatePair.east) {
            this.currentLocationInfo$ = this.requestCurrentLocationInformation(coordinatePair);
          }
        });
    } else {
      this._geographyActive = false;
      this.formDestroy$.next();
    }
  }

  @Input() editMode = false;
  @Output() geographyChanged = new EventEmitter<boolean>();

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

  private destroySubscriptions$ = new Subject<void>();
  private formDestroy$ = new Subject<void>();

  constructor(
    private coordinateTransformationService: CoordinateTransformationService,
    private mapService: MapService,
    private changeDetector: ChangeDetectorRef,
    private readonly geoDataService: GeoDataService,
  ) {}

  ngOnInit() {
    this.mapService.clickedGeographyCoordinates
      .pipe(takeUntil(this.destroySubscriptions$))
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
    this.destroySubscriptions$.next();
    this.destroySubscriptions$.unsubscribe();
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
    this.mapService.mapInitialized
      .pipe(takeUntil(this.destroySubscriptions$))
      .subscribe((initialized) => {
        if (initialized) {
          if (this.editMode && this.geographyActive) {
            this.mapService.enterCoordinateSelectionMode();
          } else {
            this.mapService.exitCoordinateSelectionMode();
          }
        }
      });
  }

  private requestCurrentLocationInformation(coordinatePair: CoordinatePair) {
    return this.geoDataService.getLocationInformation(coordinatePair).pipe(
      map((geoReference) => ({
        isoCountryCode: Countries.fromCountry(geoReference.country)?.short,
        canton: geoReference.swissCanton,
        municipalityName: geoReference.swissMunicipalityName,
        localityName: geoReference.swissLocalityName,
      })),
    );
  }
}
