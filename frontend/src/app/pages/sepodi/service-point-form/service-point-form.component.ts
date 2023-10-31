import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { ServicePointDetailFormGroup } from '../service-point-side-panel/service-point/service-point-detail-form-group';
import { ServicePointType } from '../service-point-side-panel/service-point/service-point-type';
import { TranslationSortingService } from '../../../core/translation/translation-sorting.service';
import { debounceTime, merge, Subject, Subscription, take } from 'rxjs';
import {
  Category,
  CoordinatePair,
  GeoDataService,
  OperatingPointTechnicalTimetableType,
  OperatingPointType,
  ReadServicePointVersion,
  SpatialReference,
  StopPointType,
} from '../../../api';
import { LocationInformation } from '../service-point-side-panel/service-point/location-information';
import { filter, switchMap, takeUntil } from 'rxjs/operators';
import { Countries } from '../../../core/country/Countries';
import { GeographyFormGroup } from '../geography/geography-form-group';
import { DialogService } from '../../../core/components/dialog/dialog.service';

@Component({
  selector: 'service-point-form',
  templateUrl: './service-point-form.component.html',
  styleUrls: ['./service-point-form.component.scss'],
})
export class ServicePointFormComponent implements OnInit, OnDestroy {
  @Output()
  geolocationToggleChange: EventEmitter<CoordinatePair> = new EventEmitter<CoordinatePair>();

  @Output()
  selectedServicePointTypeChange: EventEmitter<ServicePointType | null | undefined> =
    new EventEmitter<ServicePointType | null | undefined>();

  @Input() set form(form: FormGroup<ServicePointDetailFormGroup>) {
    this.formSubscriptionDestroy$.complete();
    this.formSubscriptionDestroy$ = new Subject<void>();
    this._form = form;

    const geolocationControls = form.controls.servicePointGeolocation.controls;
    this.initGeolocationControlListeners(geolocationControls);

    this._currentSelectedServicePointType = form.controls.selectedType.value;
    this.initTypeChangeInformationDialog(form.controls.selectedType);
  }
  get form(): FormGroup<ServicePointDetailFormGroup> | undefined {
    return this._form;
  }

  private _form?: FormGroup<ServicePointDetailFormGroup>;
  private _currentSelectedServicePointType: ServicePointType | null | undefined;

  @Input() set currentVersion(version: ReadServicePointVersion) {
    this._currentVersion = version;

    this.initLocationInformationDisplay();
  }
  get currentVersion(): ReadServicePointVersion | undefined {
    return this._currentVersion;
  }

  private _currentVersion?: ReadServicePointVersion;
  private activeSpatialReference?: SpatialReference;

  public servicePointTypes = Object.values(ServicePointType);
  public operatingPointTypes: string[] = [];
  public stopPointTypes = Object.values(StopPointType);
  public categories = Object.values(Category);
  public locationInformation?: LocationInformation;

  private langChangeSubscription?: Subscription;
  private formSubscriptionDestroy$: Subject<void> = new Subject<void>();

  constructor(
    private readonly translationSortingService: TranslationSortingService,
    private readonly geoDataService: GeoDataService,
    private readonly dialogService: DialogService,
  ) {}

  ngOnInit(): void {
    this.initSortedOperatingPointTypes();
  }

  ngOnDestroy() {
    this.langChangeSubscription?.unsubscribe();
  }

  private initSortedOperatingPointTypes(): void {
    this.setSortedOperatingPointTypes();
    this.langChangeSubscription =
      this.translationSortingService.translateService.onLangChange.subscribe(
        this.setSortedOperatingPointTypes,
      );
  }

  private setSortedOperatingPointTypes = (): void => {
    this.operatingPointTypes = this.translationSortingService.sort(
      [
        ...Object.values(OperatingPointType),
        ...Object.values(OperatingPointTechnicalTimetableType),
      ],
      'SEPODI.SERVICE_POINTS.OPERATING_POINT_TYPES.',
    );
  };

  onGeolocationToggleChange(hasGeolocation: boolean): void {
    const spatialRefCtrl = this.spatialRefCtrl;
    if (!spatialRefCtrl) return;
    if (hasGeolocation) {
      const locationControls = this.locationControls;
      if (!locationControls) return;

      const coordinates: CoordinatePair = {
        north: Number(locationControls.north.value),
        east: Number(locationControls.east.value),
        spatialReference: this.activeSpatialReference!,
      };

      this.spatialRefCtrl?.setValue(this.activeSpatialReference ?? SpatialReference.Lv95);
      this.geolocationToggleChange.emit(coordinates);
    } else {
      this.activeSpatialReference = this.spatialRefCtrl?.value;
      this.spatialRefCtrl?.setValue(null);
      this.geolocationToggleChange.emit();
    }
    this.form?.markAsDirty();
  }

  get spatialRefCtrl(): AbstractControl | undefined {
    return this.form?.controls.servicePointGeolocation.controls.spatialReference;
  }

  get locationControls(): GeographyFormGroup | undefined {
    return this.form?.controls.servicePointGeolocation.controls;
  }

  private initLocationInformationDisplay() {
    const servicePointGeolocation = this.currentVersion?.servicePointGeolocation;
    this.locationInformation = {
      isoCountryCode: servicePointGeolocation?.isoCountryCode,
      canton: servicePointGeolocation?.swissLocation?.canton,
      municipalityName:
        servicePointGeolocation?.swissLocation?.localityMunicipality?.municipalityName,
      localityName: servicePointGeolocation?.swissLocation?.localityMunicipality?.localityName,
    };
  }

  private initGeolocationControlListeners(geolocationControls: GeographyFormGroup) {
    merge(geolocationControls.north.valueChanges, geolocationControls.east.valueChanges)
      .pipe(
        takeUntil(this.formSubscriptionDestroy$),
        debounceTime(500),
        filter(
          () =>
            !!(
              geolocationControls.east.value &&
              geolocationControls.north.value &&
              geolocationControls.spatialReference.value
            ),
        ),
        switchMap(() =>
          this.geoDataService.getLocationInformation({
            east: geolocationControls.east.value!,
            north: geolocationControls.north.value!,
            spatialReference: geolocationControls.spatialReference.value!,
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
      });
  }

  private initTypeChangeInformationDialog(
    selectedTypeCtrl: FormControl<ServicePointType | null | undefined>,
  ) {
    selectedTypeCtrl.valueChanges
      .pipe(takeUntil(this.formSubscriptionDestroy$))
      .subscribe((newType) => {
        if (this._currentSelectedServicePointType != newType) {
          if (this._currentSelectedServicePointType != ServicePointType.ServicePoint) {
            this.dialogService
              .confirm({
                title: 'SEPODI.SERVICE_POINTS.TYPE_CHANGE_DIALOG.TITLE',
                message: 'SEPODI.SERVICE_POINTS.TYPE_CHANGE_DIALOG.MESSAGE',
              })
              .pipe(take(1))
              .subscribe((result) => {
                if (result) {
                  this._currentSelectedServicePointType = newType;
                  this.selectedServicePointTypeChange.emit(this._currentSelectedServicePointType);
                } else {
                  selectedTypeCtrl.setValue(this._currentSelectedServicePointType);
                }
              });
          } else {
            this._currentSelectedServicePointType = newType;
            this.selectedServicePointTypeChange.emit(this._currentSelectedServicePointType);
          }
        }
      });
  }

  setOperatingPointRouteNetwork(isSelected: boolean) {
    if (!this.form) return;
    if (isSelected) {
      this.form.controls.operatingPointRouteNetwork.setValue(true);
      this.form.controls.operatingPointKilometer.setValue(true);
      this.form.controls.operatingPointKilometer.disable();
      this.form.controls.operatingPointKilometerMaster.setValue(this.currentVersion?.number.number);
      this.form.controls.operatingPointKilometerMaster.disable();
    } else {
      this.form.controls.operatingPointRouteNetwork.setValue(false);
      this.form.controls.operatingPointKilometer.setValue(false);
      this.form.controls.operatingPointKilometer.enable();
      this.form.controls.operatingPointKilometerMaster.reset();
      this.form.controls.operatingPointKilometerMaster.enable();
    }
  }

  setOperatingPointKilometer(isSelected: boolean) {
    if (!this.form) return;
    if (isSelected) {
      this.form.controls.operatingPointKilometer.setValue(true);
    } else {
      this.form.controls.operatingPointKilometer.setValue(false);
    }
  }
}
