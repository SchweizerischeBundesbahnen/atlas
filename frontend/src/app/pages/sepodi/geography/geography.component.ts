import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
  Input,
  input,
  OnChanges,
  OnDestroy,
  output,
  SimpleChanges,
} from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CoordinatePair, SpatialReference } from '../../../api';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { debounceTime, merge, Subject } from 'rxjs';
import { MapService } from '../map/map.service';
import { MatRadioButton, MatRadioChange, MatRadioGroup } from '@angular/material/radio';
import { filter, take, takeUntil } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AtlasInfoIconComponent } from '@atlas/form';
import { AtlasSlideToggleComponent } from '../../../core/form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { RemoveCharsDirective } from '../../../core/form-components/text-field/remove-chars.directive';
import { DecimalNumberPipe } from '../../../core/pipe/decimal-number.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { LocationGeoInternalService } from '../../../api/service/location/location-geo-internal.service';

export const LV95_MAX_DIGITS = 5;
export const WGS84_MAX_DIGITS = 11;

@Component({
  selector: 'atlas-sepodi-geography',
  templateUrl: './geography.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [
    AtlasInfoIconComponent,
    AtlasSlideToggleComponent,
    ReactiveFormsModule,
    MatRadioGroup,
    MatRadioButton,
    TextFieldComponent,
    RemoveCharsDirective,
    DecimalNumberPipe,
    TranslatePipe,
  ],
})
export class GeographyComponent implements OnDestroy, OnChanges {
  private readonly coordinateTransformationService = inject(CoordinateTransformationService);
  private readonly mapService = inject(MapService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  private readonly locationGeoInternalService = inject(LocationGeoInternalService);
  private readonly destroyRef = inject(DestroyRef);

  readonly LV95_MAX_DIGITS = LV95_MAX_DIGITS;
  readonly WGS84_MAX_DIGITS = WGS84_MAX_DIGITS;

  _form?: FormGroup<GeographyFormGroup>;

  @Input() set form(form: FormGroup<GeographyFormGroup> | undefined) {
    this._form = form;
    if (form) {
      this._geographyActive = true;
      this.updateMapInteractionMode();
      this.onChangeCoordinatesManually(this.currentCoordinates!, false);
      merge(form.controls.east.valueChanges, form.controls.north.valueChanges)
        .pipe(
          debounceTime(500),
          filter(() => form.dirty),
          takeUntil(this.formDestroy$)
        )
        .subscribe(() => {
          this.onChangeCoordinatesManually(this.currentCoordinates!, true);
          this.coordinatesChanged.emit(this.currentCoordinates!);
        });
    } else {
      this._geographyActive = false;
      this.formDestroy$.next();
    }
  }

  readonly editMode = input(false);
  readonly geographyOptional = input(true);
  readonly geographyChanged = output<boolean>();
  readonly coordinatesChanged = output<CoordinatePair>();

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

  private readonly formDestroy$ = new Subject<void>();

  /** Cancels an interaction mode update that is still waiting for the map, so only the last one wins. */
  private readonly mapInteractionModeSuperseded$ = new Subject<void>();

  constructor() {
    this.mapService.clickedGeographyCoordinates.pipe(takeUntilDestroyed()).subscribe((coordinatePairWGS84) => {
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
    this.mapInteractionModeSuperseded$.next();
    this.mapInteractionModeSuperseded$.unsubscribe();
    this.formDestroy$.next();
    this.formDestroy$.unsubscribe();
  }

  setFormGroupValue(coordinates?: CoordinatePair) {
    if (!this._form || !coordinates) {
      return;
    }

    const maxDigits =
      this.currentSpatialReference === SpatialReference.Lv95 ? this.LV95_MAX_DIGITS : this.WGS84_MAX_DIGITS;

    const roundedEast = Number(coordinates.east.toFixed(maxDigits));
    const roundedNorth = Number(coordinates.north.toFixed(maxDigits));

    this._form.markAsDirty();
    this._form.patchValue({
      east: roundedEast,
      north: roundedNorth,
    });
  }

  initTransformedCoordinatePair() {
    if (!this.currentCoordinates) return;
    this.transformedCoordinatePair = this.coordinateTransformationService.transform(
      this.currentCoordinates,
      this.transformedSpatialReference
    );
    this.changeDetector.detectChanges();
  }

  get transformedSpatialReference() {
    return this.currentSpatialReference === SpatialReference.Lv95 ? SpatialReference.Wgs84 : SpatialReference.Lv95;
  }

  get currentSpatialReference(): SpatialReference | null | undefined {
    return this._form?.controls.spatialReference.value;
  }

  get currentCoordinates(): CoordinatePair | undefined {
    return this._form
      ? {
          east: Number(this._form.value.east),
          north: Number(this._form.value.north),
          spatialReference: this.currentSpatialReference!,
        }
      : undefined;
  }

  switchSpatialReference($event: MatRadioChange) {
    if (!$event.value) {
      return;
    }
    const previousCoordinatePair = this.currentCoordinates!;
    previousCoordinatePair.spatialReference = this.transformedSpatialReference;

    const transformedCoordinatePair = this.coordinateTransformationService.transform(
      previousCoordinatePair,
      this.currentSpatialReference!
    );

    this.setFormGroupValue(transformedCoordinatePair);
    this.initTransformedCoordinatePair();
  }

  onChangeCoordinatesManually(coordinates: CoordinatePair, updateHeight: boolean) {
    if (this.currentSpatialReference === SpatialReference.Lv95) {
      coordinates = this.coordinateTransformationService.transform(coordinates, SpatialReference.Wgs84)!;
    }
    if (coordinates?.north && coordinates?.east) {
      this.setHeightFromGeoData(coordinates, updateHeight);
      this.mapService.placeMarkerAndFlyTo({
        lat: coordinates.north,
        lng: coordinates.east,
      });
      this.initTransformedCoordinatePair();
    }
  }

  onMapClick(coordinatesWgs84: CoordinatePair) {
    if (this.currentSpatialReference === SpatialReference.Lv95) {
      coordinatesWgs84 = this.coordinateTransformationService.transform(coordinatesWgs84, SpatialReference.Lv95)!;
    }
    this.setHeightFromGeoData(coordinatesWgs84, true);
    this.setFormGroupValue(coordinatesWgs84);
    this.initTransformedCoordinatePair();
  }

  private updateMapInteractionMode() {
    // The map reports readiness asynchronously, and a click is only handled once coordinate
    // selection mode is entered. Waiting instead of giving up keeps the map usable on machines that
    // render it slowly, e.g. a CI container without a GPU that falls back to a software renderer.
    this.mapInteractionModeSuperseded$.next();
    this.mapService.mapInitialized
      .pipe(
        filter((initialized) => initialized),
        take(1),
        takeUntil(this.mapInteractionModeSuperseded$),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => {
        if (this.editMode() && this.geographyActive) {
          this.mapService.enterCoordinateSelectionMode();
        } else {
          this.mapService.exitCoordinateSelectionMode();
        }
      });
  }

  public setHeightFromGeoData(coordinatePair: CoordinatePair, updateHeight: boolean) {
    if (coordinatePair && updateHeight) {
      this.locationGeoInternalService.getHeight(coordinatePair).subscribe((value) => {
        this._form?.patchValue({
          height: value.height,
        });
      });
    }
  }
}
