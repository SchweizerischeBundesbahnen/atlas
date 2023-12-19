import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import {
  ApplicationRole,
  ApplicationType,
  CoordinatePair,
  CreateServicePointVersion,
  Geolocation,
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
import { BehaviorSubject, catchError, EMPTY, Observable, of, skip, Subject, take } from 'rxjs';
import { Pages } from '../../../pages';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { AuthService } from '../../../../core/auth/auth.service';
import { ServicePointAbbreviationAllowList } from './service-point-abbreviation-allow-list';
import { GeographyFormGroupBuilder } from '../../geography/geography-form-group';
import { GeographyChangedEvent } from '../../geography/geography-changed-event';

@Component({
  selector: 'app-service-point',
  templateUrl: './service-point-detail.component.html',
  styleUrls: ['./service-point-detail.component.scss'],
  viewProviders: [GeographyChangedEvent],
})
export class ServicePointDetailComponent implements OnInit, OnDestroy, DetailFormComponent {
  servicePointVersions!: ReadServicePointVersion[];
  _selectedVersion?: ReadServicePointVersion;

  set selectedVersion(version: ReadServicePointVersion) {
    this._selectedVersion = version;
    if (version.servicePointGeolocation?.spatialReference) {
      this.geographyChangedEvent.emit(true);
    } else {
      this.geographyChangedEvent.emitOnlyWhenValueChanged(false);
    }
  }

  showVersionSwitch = false;
  selectedVersionIndex!: number;
  form?: FormGroup<ServicePointDetailFormGroup>;
  hasAbbreviation = false;
  isAbbreviationAllowed = false;

  isLatestVersionSelected = false;
  preferredId?: number;

  isSwitchVersionDisabled = false;

  public isFormEnabled$ = new BehaviorSubject<boolean>(false);
  private readonly ZOOM_LEVEL_FOR_DETAIL = 14;
  private ngUnsubscribe = new Subject<void>();
  private _savedGeographyForm?: Geolocation;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private servicePointService: ServicePointsService,
    private notificationService: NotificationService,
    private mapService: MapService,
    private authService: AuthService,
    private readonly geographyChangedEvent: GeographyChangedEvent,
  ) {}

  ngOnInit() {
    this.geographyChangedEvent
      .get()
      .pipe(skip(1), takeUntil(this.ngUnsubscribe))
      .subscribe((enabled) => {
        enabled ? this.onGeographyEnabled() : this.onGeographyDisabled();
      });

    this.route.parent?.data.pipe(takeUntil(this.ngUnsubscribe)).subscribe((next) => {
      this.servicePointVersions = next.servicePoint;

      this.initServicePoint();
      this.displayAndSelectServicePointOnMap();
    });
  }

  private onGeographyEnabled() {
    if (this.form && !this.form.controls.servicePointGeolocation) {
      ServicePointFormGroupBuilder.addGroupToForm(
        this.form,
        'servicePointGeolocation',
        GeographyFormGroupBuilder.buildFormGroup(this._savedGeographyForm),
      );
      this.form.markAsDirty();
    }
  }

  private onGeographyDisabled() {
    if (this.form?.controls.servicePointGeolocation) {
      if (
        this.form.controls.servicePointGeolocation.value.spatialReference === SpatialReference.Wgs84
      ) {
        const coordinatePair = {
          spatialReference: SpatialReference.Wgs84,
          north: this.form.controls.servicePointGeolocation.value.north!,
          east: this.form.controls.servicePointGeolocation.value.east!,
        };
        this._savedGeographyForm = {
          spatialReference: this.form.controls.servicePointGeolocation.value.spatialReference!,
          height: this.form.controls.servicePointGeolocation.value.height ?? undefined,
          wgs84: coordinatePair,
        } as Geolocation;
      } else {
        const coordinatePair = {
          spatialReference: SpatialReference.Lv95,
          north: this.form.controls.servicePointGeolocation.value.north!,
          east: this.form.controls.servicePointGeolocation.value.east!,
        };
        this._savedGeographyForm = {
          spatialReference: this.form.controls.servicePointGeolocation.value.spatialReference!,
          height: this.form.controls.servicePointGeolocation.value.height ?? undefined,
          lv95: coordinatePair,
        } as Geolocation;
      }
      ServicePointFormGroupBuilder.removeGroupFromForm(this.form, 'servicePointGeolocation');
      this.form.markAsDirty();
    }
  }

  ngOnDestroy() {
    this.mapService.deselectServicePoint();
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.unsubscribe();
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.initSelectedVersion(this.servicePointVersions[newIndex]);
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }

  private initServicePoint() {
    VersionsHandlingService.addVersionNumbers(this.servicePointVersions);
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.servicePointVersions);

    let selectedVersion: ReadServicePointVersion;
    if (this.preferredId) {
      selectedVersion =
        this.servicePointVersions.find((i) => i.id === this.preferredId) ??
        VersionsHandlingService.determineDefaultVersionByValidity(this.servicePointVersions);
      this.preferredId = undefined;
    } else {
      selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.servicePointVersions,
      );
    }

    this.selectedVersionIndex = this.servicePointVersions.indexOf(selectedVersion);
    this.initSelectedVersion(selectedVersion);
  }

  public initSelectedVersion(version: ReadServicePointVersion) {
    this.form = ServicePointFormGroupBuilder.buildFormGroup(version);
    this.disableForm();
    this.isSwitchVersionDisabled = false;
    this.selectedVersion = version;
    this.displayAndSelectServicePointOnMap();
    this.isSelectedVersionHighDate(this.servicePointVersions, version);
    this.checkIfAbbreviationIsAllowed();
    this.hasAbbreviation = !!this.form.controls.abbreviation.value;
  }

  private displayAndSelectServicePointOnMap() {
    this.mapService.mapInitialized.pipe(takeUntil(this.ngUnsubscribe)).subscribe((initialized) => {
      if (initialized) {
        if (this.mapService.map.getZoom() <= this.ZOOM_LEVEL_FOR_DETAIL) {
          this.mapService.map.setZoom(this.ZOOM_LEVEL_FOR_DETAIL);
        }
        this.mapService.centerOn(this._selectedVersion?.servicePointGeolocation?.wgs84);
        this.mapService.displayCurrentCoordinates(
          this._selectedVersion?.servicePointGeolocation?.wgs84,
        );
      }
    });
  }

  toggleEdit() {
    if (this.form?.enabled) {
      this.showConfirmationDialog();
    } else {
      this.isSwitchVersionDisabled = true;
      this.enableForm();
      if (this.form?.controls.operatingPointRouteNetwork.value) {
        this.form.controls.operatingPointKilometer.disable();
      }
    }
  }

  showConfirmationDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.initSelectedVersion(this._selectedVersion!);
          this.disableForm();
          this.isSwitchVersionDisabled = false;
        }
      });
  }

  private disableForm(): void {
    this.form?.disable({ emitEvent: false });
    this.isFormEnabled$.next(false);
    this._savedGeographyForm = undefined;
  }

  private enableForm(): void {
    this.form?.enable({ emitEvent: false });
    this.isFormEnabled$.next(true);
  }

  confirmLeave(): Observable<boolean> {
    if (this.form?.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  private confirmBoTransfer(): Observable<boolean> {
    const currentlySelectedBo = this.form?.controls.businessOrganisation.value;
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
    ValidationService.validateForm(this.form!);
    if (this.form?.valid) {
      const servicePointVersion = ServicePointFormGroupBuilder.getWritableServicePoint(this.form);
      this.disableForm();
      this.update(this._selectedVersion!.id!, servicePointVersion);
    }
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
              this.hasAbbreviation = !!this.form?.controls.abbreviation.value;
              this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.EDIT_SUCCESS');
              this.router
                .navigate(['..', this._selectedVersion!.number.number], { relativeTo: this.route })
                .then(() => this.mapService.refreshMap());
            });
        } else {
          this.enableForm();
        }
      });
  }

  private handleError = () => {
    this.enableForm();
    if (this.form?.controls.operatingPointRouteNetwork.value) {
      this.form.controls.operatingPointKilometer.disable();
    }
    return EMPTY;
  };

  isFormDirty(): boolean {
    return !!this.form?.dirty;
  }

  checkIfAbbreviationIsAllowed() {
    this.isAbbreviationAllowed = ServicePointAbbreviationAllowList.SBOIDS.some((element) =>
      element.includes(this._selectedVersion!.businessOrganisation),
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

  revoke() {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.REVOKE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_REVOKE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.servicePointService
            .revokeServicePoint(this.selectedVersion.number.number)
            .pipe((takeUntil(this.ngUnsubscribe), catchError(this.handleError)))
            .subscribe(() => {
              this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.REVOKE_SUCCESS');
              this.router
                .navigate(['..', this.selectedVersion.number.number], {
                  relativeTo: this.route,
                })
                .then(() => this.mapService.refreshMap());
            });
        }
      });
  }

  validate() {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.VALIDATE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_VALIDATE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.servicePointService
            .validateServicePoint(this.selectedVersion.id!)
            .pipe((takeUntil(this.ngUnsubscribe), catchError(this.handleError)))
            .subscribe(() => {
              this.notificationService.success(
                'SEPODI.SERVICE_POINTS.NOTIFICATION.VALIDATE_SUCCESS',
              );
              this.router.navigate(['..', this.selectedVersion.number.number], {
                relativeTo: this.route,
              });
            });
        }
      });
  }
}
