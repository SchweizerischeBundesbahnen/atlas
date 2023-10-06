import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import {
  ApplicationRole,
  ApplicationType,
  CoordinatePair,
  CreateServicePointVersion,
  ReadServicePointVersion,
  ServicePointsService,
  SpatialReference,
} from '../../../../api';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from './service-point-detail-form-group';
import { MapService } from '../../map/map.service';
import { BehaviorSubject, catchError, EMPTY, Observable, of, Subject, take } from 'rxjs';
import { Pages } from '../../../pages';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { AuthService } from '../../../../core/auth/auth.service';
import { CoordinateTransformationService } from '../../geography/coordinate-transformation.service';
import { ServicePointAbbreviationAllowList } from './service-point-abbreviation-allow-list';

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
  hasAbbreviation = false;
  isAbbreviationAllowed = false;

  isLatestVersionSelected = false;
  preferredId?: number;
  isSwitchVersionDisabled = false;

  public isGeographyOn = false;
  public isFormEnabled$ = new BehaviorSubject<boolean>(false);
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
    private coordinateTransformationService: CoordinateTransformationService,
  ) {}

  ngOnInit() {
    this.route.parent?.data.pipe(takeUntil(this.ngUnsubscribe)).subscribe((next) => {
      this.servicePointVersions = next.servicePoint;

      this.initServicePoint();
      this.displayAndSelectServicePointOnMap();
    });

    this.mapService.isGeolocationActivated.next(
      !!this.form.controls.servicePointGeolocation.controls.spatialReference.value,
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

  public initSelectedVersion() {
    if (this.selectedVersion.id) {
      this.isNew = false;
    }

    this.form = ServicePointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    this.isGeographyOn = !!this.form.value.servicePointGeolocation?.spatialReference;
    if (!this.isNew) {
      this.disableForm();
    }
    this.displayAndSelectServicePointOnMap();
    this.isSelectedVersionHighDate(this.servicePointVersions, this.selectedVersion);
    this.checkIfAbbreviationIsAllowed();
    this.hasAbbreviation = !!this.form.controls.abbreviation.value;
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
      this.enableForm();
      if (this.form.controls.operatingPointRouteNetwork.value) {
        this.form.controls.operatingPointKilometer.disable();
      }
    }
  }

  showConfirmationDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.isNew) {
            this.closeSidePanel();
          } else {
            this.initSelectedVersion();
            this.disableForm();
            this.cancelMapEditMode();
          }
        }
      });
  }

  private disableForm(): void {
    this.form.disable();
    this.isFormEnabled$.next(false);
  }

  private enableForm(): void {
    this.form.enable();
    this.isFormEnabled$.next(true);
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
      this.disableForm();
      if (this.isNew) {
        this.create(servicePointVersion);
      } else {
        servicePointVersion.numberWithoutCheckDigit = this.selectedVersion.number.number;
        this.update(this.selectedVersion.id!, servicePointVersion);
      }
    }
  }

  private create(servicePointVersion: CreateServicePointVersion) {
    this.servicePointService
      .createServicePoint(servicePointVersion)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe((servicePointVersion) => {
        this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', servicePointVersion.number.number], { relativeTo: this.route })
          .then();
      });
  }

  private update(id: number, servicePointVersion: CreateServicePointVersion) {
    this.confirmBoTransfer()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.preferredId = id;
          this.servicePointService
            .updateServicePoint(id, servicePointVersion)
            .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
            .subscribe(() => {
              this.mapService.refreshMap();
              this.cancelMapEditMode();
            this.hasAbbreviation = !!this.form.controls.abbreviation?.value;this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.EDIT_SUCCESS');
              this.router
                .navigate(['..', this.selectedVersion.number.number], { relativeTo: this.route })
                .then();
            });
        } else {
          this.enableForm();
        }
      });
  }

  private handleError = () => {
    this.enableForm();
    if (this.form.controls.operatingPointRouteNetwork.value) {
      this.form.controls.operatingPointKilometer.disable();
    }
    return EMPTY;
  };

  isFormDirty(): boolean {
    return this.form.dirty;
  }

  get currentSpatialReference(): SpatialReference | null | undefined {
    return this.form.controls.servicePointGeolocation.controls.spatialReference.value;
  }

  activateGeolocation(coordinates: CoordinatePair) {
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
    this.isSwitchVersionDisabled = true;
  }

  deactivateGeolocation() {
    this.mapService.isGeolocationActivated.next(false);
    this.cancelMapEditMode();
    this.isSwitchVersionDisabled = true;
  }

  cancelMapEditMode() {
    this.mapService.isEditMode.next(false);
    this.isSwitchVersionDisabled = false;
    this.mapService.isGeolocationActivated.next(
      !!this.form.controls.servicePointGeolocation.controls.spatialReference.value,
    );
  }

  isCoordinatesPairValidForTransformation(coordinates: CoordinatePair) {
    return this.isCoordinatePairNotZero(coordinates) && !!coordinates.north && !!coordinates.east;
  }

  isCoordinatePairNotZero(coordinates: CoordinatePair): boolean {
    return coordinates.north !== 0 && coordinates.east !== 0;
  }

  checkIfAbbreviationIsAllowed() {
    this.isAbbreviationAllowed = ServicePointAbbreviationAllowList.SBOIDS.some((element) =>
      element.includes(this.selectedVersion.businessOrganisation),
    );
  }

  isSelectedVersionHighDate(
    servicePointVersions: ReadServicePointVersion[],
    selectedVersion: ReadServicePointVersion,
  ) {
    this.isLatestVersionSelected = !servicePointVersions.some(
      (obj) => obj.validTo > selectedVersion.validTo,
    );
  }
}
