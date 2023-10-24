import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import {
  ApplicationRole,
  ApplicationType,
  Category,
  CoordinatePair,
  CreateServicePointVersion,
  GeoDataService,
  OperatingPointTechnicalTimetableType,
  OperatingPointType,
  ReadServicePointVersion,
  ServicePointsService,
  SpatialReference,
  StopPointType,
} from '../../../../api';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from './service-point-detail-form-group';
import { ServicePointType } from './service-point-type';
import { MapService } from '../../map/map.service';
import { catchError, debounceTime, EMPTY, merge, Observable, of, Subject } from 'rxjs';
import { Pages } from '../../../pages';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { filter, switchMap, takeUntil } from 'rxjs/operators';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { AuthService } from '../../../../core/auth/auth.service';
import { TranslationSortingService } from '../../../../core/translation/translation-sorting.service';
import { CoordinateTransformationService } from '../../geography/coordinate-transformation.service';
import { LocationInformation } from './location-information';
import { Countries } from '../../../../core/country/Countries';

@Component({
  selector: 'app-service-point',
  templateUrl: './service-point-detail.component.html',
  styleUrls: ['./service-point-detail.component.scss'],
})
export class ServicePointDetailComponent implements OnInit, OnDestroy, DetailFormComponent {
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadServicePointVersion;
  showVersionSwitch = false;
  selectedVersionIndex!: number;
  form!: FormGroup<ServicePointDetailFormGroup>;
  isNew = true;

  preferredId?: number;

  types = Object.values(ServicePointType);

  readonly operatingPointTypeValues = (Object.values(OperatingPointType) as string[]).concat(
    Object.values(OperatingPointTechnicalTimetableType),
  );

  operatingPointTypes!: string[];

  previouslySelectedType!: ServicePointType;
  stopPointTypes = Object.values(StopPointType);
  categories = Object.values(Category);
  isSwitchVersionDisabled = false;

  currentSpatialReference!: SpatialReference;

  locationInformation!: LocationInformation;

  private readonly ZOOM_LEVEL_FOR_DETAIL = 14;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private servicePointService: ServicePointsService,
    private notificationService: NotificationService,
    private mapService: MapService,
    private authService: AuthService,
    private translationSortingService: TranslationSortingService,
    private coordinateTransformationService: CoordinateTransformationService,
    private geoDataService: GeoDataService,
  ) {}

  ngOnInit() {
    this.route.parent?.data.pipe(takeUntil(this.ngUnsubscribe)).subscribe((next) => {
      this.servicePointVersions = next.servicePoint;

      this.initServicePoint();
      this.displayAndSelectServicePointOnMap();
    });

    this.initSortedOperatingPointTypes();
    this.mapService.isGeolocationActivated.next(
      !!this.form.controls.servicePointGeolocation.controls.spatialReference.value,
    );
  }

  initSortedOperatingPointTypes() {
    this.setSortedOperatingPointTypes();
    this.translationSortingService.translateService.onLangChange.subscribe(() =>
      this.setSortedOperatingPointTypes(),
    );
  }

  setSortedOperatingPointTypes() {
    this.operatingPointTypes = this.translationSortingService.sort(
      this.operatingPointTypeValues,
      'SEPODI.SERVICE_POINTS.OPERATING_POINT_TYPES.',
    );
  }

  ngOnDestroy() {
    this.mapService.deselectServicePoint();
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.servicePointVersions[newIndex];
    this.initSelectedVersion();
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }

  private initServicePoint() {
    VersionsHandlingService.addVersionNumbers(this.servicePointVersions);
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.servicePointVersions);

    if (this.preferredId) {
      this.selectedVersion =
        this.servicePointVersions.find((i) => i.id === this.preferredId) ??
        VersionsHandlingService.determineDefaultVersionByValidity(this.servicePointVersions);
      this.preferredId = undefined;
    } else {
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.servicePointVersions,
      );
    }
    this.selectedVersionIndex = this.servicePointVersions.indexOf(this.selectedVersion);
    this.initSelectedVersion();
  }

  private initSelectedVersion() {
    if (this.selectedVersion.id) {
      this.isNew = false;
    }

    this.form = ServicePointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();
    }
    this.displayAndSelectServicePointOnMap();
    this.initTypeChangeInformationDialog();
    this.initLocationInformationDisplay();
  }

  private initTypeChangeInformationDialog() {
    this.previouslySelectedType = this.form.controls.selectedType.value!;
    this.form.controls.selectedType.valueChanges.subscribe((newType) => {
      if (this.previouslySelectedType != newType) {
        if (this.previouslySelectedType != ServicePointType.ServicePoint) {
          this.dialogService
            .confirm({
              title: 'SEPODI.SERVICE_POINTS.TYPE_CHANGE_DIALOG.TITLE',
              message: 'SEPODI.SERVICE_POINTS.TYPE_CHANGE_DIALOG.MESSAGE',
            })
            .subscribe((result) => {
              if (result) {
                this.previouslySelectedType = newType!;
              } else {
                this.form.controls.selectedType.setValue(this.previouslySelectedType);
              }
            });
        } else {
          this.previouslySelectedType = newType!;
        }
      }
    });
  }

  private initLocationInformationDisplay() {
    const servicePointGeolocation = this.selectedVersion.servicePointGeolocation;
    this.locationInformation = {
      isoCountryCode: servicePointGeolocation?.isoCountryCode,
      canton: servicePointGeolocation?.swissLocation?.canton,
      municipalityName:
        servicePointGeolocation?.swissLocation?.localityMunicipality?.municipalityName,
      localityName: servicePointGeolocation?.swissLocation?.localityMunicipality?.localityName,
    };

    const geolocationControls = this.form.controls.servicePointGeolocation.controls;
    merge(geolocationControls.north.valueChanges, geolocationControls.east.valueChanges)
      .pipe(
        takeUntil(this.ngUnsubscribe),
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
        this.locationInformation.isoCountryCode = Countries.fromCountry(geoReference.country)
          ?.short;
        this.locationInformation.canton = geoReference.swissCanton;
        this.locationInformation.municipalityName = geoReference.swissMunicipalityName;
        this.locationInformation.localityName = geoReference.swissLocalityName;
      });
  }

  private displayAndSelectServicePointOnMap() {
    this.cancelMapEditMode();
    this.mapService.mapInitialized.pipe(takeUntil(this.ngUnsubscribe)).subscribe((initialized) => {
      if (initialized) {
        if (this.mapService.map.getZoom() <= this.ZOOM_LEVEL_FOR_DETAIL) {
          this.mapService.map.setZoom(this.ZOOM_LEVEL_FOR_DETAIL);
        }
        this.mapService
          .centerOn(this.selectedVersion.servicePointGeolocation?.wgs84)
          .then(() =>
            this.mapService.displayCurrentCoordinates(
              this.selectedVersion.servicePointGeolocation?.wgs84,
            ),
          );
      }
    });
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.mapService.isEditMode.next(true);
      this.isSwitchVersionDisabled = true;
      this.form.enable();
      if (this.form.controls.operatingPointRouteNetwork.value) {
        this.form.controls.operatingPointKilometer.disable();
      }
    }
  }

  showConfirmationDialog() {
    this.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        if (this.isNew) {
          this.closeSidePanel();
        } else {
          this.initSelectedVersion();
          this.form.disable();
          this.cancelMapEditMode();
        }
      }
    });
  }

  confirmLeave(): Observable<boolean> {
    if (this.form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  private confirmBoTransfer(): Observable<boolean> {
    const currentlySelectedBo = this.form.controls.businessOrganisation.value;
    const permission = this.authService.getApplicationUserPermission(ApplicationType.Sepodi);
    if (
      !this.authService.isAdmin &&
      permission.role == ApplicationRole.Writer &&
      currentlySelectedBo &&
      !AuthService.getSboidRestrictions(permission).includes(currentlySelectedBo)
    ) {
      return this.dialogService.confirm({
        title: 'DIALOG.CONFIRM_BO_TRANSFER_TITLE',
        message: 'DIALOG.CONFIRM_BO_TRANSFER',
      });
    }
    return of(true);
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const servicePointVersion = ServicePointFormGroupBuilder.getWritableServicePoint(this.form);
      this.form.disable();
      if (this.isNew) {
        this.create(servicePointVersion);
      } else {
        servicePointVersion.numberWithoutCheckDigit = this.selectedVersion.number.number;
        this.update(this.selectedVersion.id!, servicePointVersion);
      }
      this.cancelMapEditMode();
    }
  }

  private create(servicePointVersion: CreateServicePointVersion) {
    this.servicePointService
      .createServicePoint(servicePointVersion)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((servicePointVersion) => {
        this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', servicePointVersion.number.number], { relativeTo: this.route })
          .then();
      });
  }

  private update(id: number, servicePointVersion: CreateServicePointVersion) {
    this.confirmBoTransfer().subscribe((confirmed) => {
      if (confirmed) {
        this.preferredId = id;
        this.servicePointService
          .updateServicePoint(id, servicePointVersion)
          .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
          .subscribe(() => {
            this.mapService.refreshMap();
            this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.EDIT_SUCCESS');
            this.router
              .navigate(['..', this.selectedVersion.number.number], { relativeTo: this.route })
              .then();
          });
      } else {
        this.form.enable();
      }
    });
  }

  private handleError() {
    return () => {
      this.form.enable();
      if (this.form.controls.operatingPointRouteNetwork.value) {
        this.form.controls.operatingPointKilometer.disable();
      }
      return EMPTY;
    };
  }

  isFormDirty(): boolean {
    return this.form.dirty;
  }

  setSpatialReference(value: SpatialReference | null) {
    this.form.controls.servicePointGeolocation.controls.spatialReference.setValue(value);
  }

  activateGeolocation() {
    const locationControls = this.form.controls.servicePointGeolocation.controls;

    let coordinates: CoordinatePair = {
      north: Number(locationControls.north.value!),
      east: Number(locationControls.east.value!),
      spatialReference: this.currentSpatialReference,
    };

    this.setSpatialReference(this.currentSpatialReference || SpatialReference.Lv95);

    this.mapService.isGeolocationActivated.next(true);
    this.mapService.isEditMode.next(true);

    if (!this.isCoordinatesPairValidForTransformation(coordinates)) {
      return;
    }

    if (this.currentSpatialReference === SpatialReference.Lv95) {
      coordinates = this.coordinateTransformationService.transform(
        coordinates,
        SpatialReference.Wgs84,
      );
    }

    const coordinatePairWGS84 = { lat: coordinates.north, lng: coordinates.east };
    this.mapService.placeMarkerAndFlyTo(coordinatePairWGS84);
  }

  deactivateGeolocation() {
    this.setSpatialReference(null);
    this.mapService.isGeolocationActivated.next(false);
    this.cancelMapEditMode();
  }

  handleGeolocationToggle(hasGeolocation: boolean) {
    if (hasGeolocation) {
      this.activateGeolocation();
    } else {
      this.deactivateGeolocation();
    }
    this.isSwitchVersionDisabled = true;
    this.form.markAsDirty();
  }

  cancelMapEditMode() {
    this.mapService.isEditMode.next(false);
    this.isSwitchVersionDisabled = false;
    this.mapService.isGeolocationActivated.next(
      !!this.form.controls.servicePointGeolocation.controls.spatialReference.value,
    );
  }

  triggerSpatialReferenceEvent(spatialReference: SpatialReference) {
    this.currentSpatialReference = spatialReference;
  }

  isCoordinatesPairValidForTransformation(coordinates: CoordinatePair) {
    return this.isCoordinatePairNotZero(coordinates) && !!coordinates.north && !!coordinates.east;
  }

  isCoordinatePairNotZero(coordinates: CoordinatePair): boolean {
    return coordinates.north !== 0 && coordinates.east !== 0;
  }

  setOperatingPointRouteNetwork(isSelected: boolean) {
    if (isSelected) {
      this.form.controls.operatingPointRouteNetwork.setValue(true);
      this.form.controls.operatingPointKilometer.setValue(true);
      this.form.controls.operatingPointKilometer.disable();
      this.form.controls.operatingPointKilometerMaster.setValue(this.selectedVersion.number.number);
    } else {
      this.form.controls.operatingPointRouteNetwork.setValue(false);
      this.form.controls.operatingPointKilometer.setValue(false);
      this.form.controls.operatingPointKilometer.enable();
      this.form.controls.operatingPointKilometerMaster.reset();
    }
  }

  setOperatingPointKilometer(isSelected: boolean) {
    if (isSelected) {
      this.form.controls.operatingPointKilometer.setValue(true);
    } else {
      this.form.controls.operatingPointKilometer.setValue(false);
    }
  }
}
