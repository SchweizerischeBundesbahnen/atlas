import {
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
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
import { ServicePointCreationSideService } from '../service-point-side-panel/service-point/service-point-creation/service-point-creation.component';
import { filter, switchMap, takeUntil } from 'rxjs/operators';
import { Countries } from 'src/app/core/country/Countries';
import { LocationInformation } from '../service-point-side-panel/service-point/location-information';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'sepodi-geography',
  templateUrl: './geography.component.html',
})
export class GeographyComponent implements OnInit, OnDestroy, OnChanges {
  readonly LV95_MAX_DIGITS = LV95_MAX_DIGITS;
  readonly WGS84_MAX_DIGITS = WGS84_MAX_DIGITS;

  // todo: check that form is undefined when geography = false
  _form?: FormGroup<GeographyFormGroup>;
  @Input() set form(form: FormGroup<GeographyFormGroup> | undefined) {
    this._form = form;
    if (!form) return;
    merge(form.controls.east.valueChanges, form.controls.north.valueChanges)
      .pipe(takeUntil(this.destroySubscriptions$), debounceTime(500))
      .subscribe(() => {
        this.onChangeCoordinatesManually({
          east: Number(form.controls.east.value),
          north: Number(form.controls.north.value),
          spatialReference: this.currentSpatialReference!,
        });
      });
  }

  @Input() editMode = false;

  private _geographyActive = false;

  get geographyActive() {
    return this._geographyActive;
  }

  set geographyActive(value: boolean) {
    this._geographyActive = value;
    this.sharedService.geographyChanged.next(value);
    this.updateMapInteractionMode();
  }
  @Output()
  locationInformationChange = new EventEmitter<LocationInformation>();
  locationInformation?: LocationInformation;

  transformedCoordinatePair?: CoordinatePair;

  private destroySubscriptions$ = new Subject<void>();

  constructor(
    private coordinateTransformationService: CoordinateTransformationService,
    private mapService: MapService,
    private changeDetector: ChangeDetectorRef,
    private sharedService: ServicePointCreationSideService,
    private geoDataService: GeoDataService,
  ) {}

  ngOnInit() {
    this.sharedService.geographyChanged
      .pipe(takeUntil(this.destroySubscriptions$))
      .subscribe((enabled) => {
        if (enabled) {
          this._geographyActive = true;
          this.updateMapInteractionMode();
        } else {
          this._geographyActive = false;
        }
      });

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

    this._form.controls.east.setValue(roundedEast);
    this._form.controls.north.setValue(roundedNorth);
    this._form.markAsDirty();
  }

  ngOnDestroy() {
    this.mapService.exitCoordinateSelectionMode();
    this.destroySubscriptions$.next();
    this.destroySubscriptions$.complete();
  }

  initTransformedCoordinatePair() {
    if (!this.currentCoordinates) return;
    this.transformedCoordinatePair = this.coordinateTransformationService.transform(
      this.currentCoordinates,
      this.transformedSpatialReference,
    );
    this.initGeolocationControlListeners(this.formGroup);
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

  private initGeolocationControlListeners(geolocationControls: FormGroup<GeographyFormGroup>) {
    merge(
      geolocationControls.controls.north.valueChanges,
      geolocationControls.controls.east.valueChanges,
    )
      .pipe(
        takeUntil(this.destroySubscriptions$),
        debounceTime(100),
        filter(() => {
          return !!(
            geolocationControls.controls.east.value &&
            geolocationControls.controls.north.value &&
            geolocationControls.controls.spatialReference.value
          );
        }),
        switchMap(() =>
          this.geoDataService.getLocationInformation({
            east: geolocationControls.controls.east.value!,
            north: geolocationControls.controls.north.value!,
            spatialReference: geolocationControls.controls.spatialReference.value!,
          }),
        ),
      )
      .subscribe((geoReference) => {
        this.locationInformation = {
          isoCountryCode: Countries.fromCountry(geoReference.country)?.short,
          canton: geoReference.swissCanton,
          municipalityName: geoReference.swissMunicipalityName,
          localityName: geoReference.swissLocalityName,
        };

        this.locationInformationChange.emit(this.locationInformation);
        geolocationControls.controls.height.setValue(geoReference.height);
      });
  }
}
