import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import {
  Category,
  CreateServicePointVersion,
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
import { catchError, EMPTY, Observable, of, Subject, Subscription } from 'rxjs';
import { Pages } from '../../../pages';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';

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
  test: any;
  types = Object.values(ServicePointType);
  operatingPointTypes = (Object.values(OperatingPointType) as string[]).concat(
    Object.values(OperatingPointTechnicalTimetableType)
  );

  previouslySelectedType!: ServicePointType;
  stopPointTypes = Object.values(StopPointType);
  categories = Object.values(Category);

  private readonly ZOOM_LEVEL_FOR_DETAIL = 14;

  private mapSubscription!: Subscription;
  private servicePointSubscription?: Subscription;
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private servicePointService: ServicePointsService,
    private notificationService: NotificationService,
    private mapService: MapService
  ) {}

  ngOnInit() {
    this.servicePointSubscription = this.route.parent?.data.subscribe((next) => {
      this.servicePointVersions = next.servicePoint;
      this.mapSubscription?.unsubscribe();

      this.initServicePoint();
      this.displayAndSelectServicePointOnMap();
    });
  }

  ngOnDestroy() {
    this.mapSubscription?.unsubscribe();
    this.servicePointSubscription?.unsubscribe();
  }

  switchVersion(newIndex: number) {
    this.selectedVersion = this.servicePointVersions[newIndex];
    this.initSelectedVersion();
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }

  private initServicePoint() {
    VersionsHandlingService.addVersionNumbers(this.servicePointVersions);
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.servicePointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.servicePointVersions
    );

    this.initSelectedVersion();
  }

  private initSelectedVersion() {
    if (this.selectedVersion.id) {
      this.isNew = false;
    }

    this.selectedVersionIndex = this.servicePointVersions.indexOf(this.selectedVersion);

    this.form = ServicePointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();
    }
    this.initTypeChangeInformationDialog();
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

  private displayAndSelectServicePointOnMap() {
    this.mapSubscription = this.mapService.mapInitialized.subscribe((initialized) => {
      if (initialized && this.form.value.servicePointGeolocation?.spatialReference) {
        if (this.mapService.map.getZoom() <= this.ZOOM_LEVEL_FOR_DETAIL) {
          this.mapService.map.setZoom(this.ZOOM_LEVEL_FOR_DETAIL);
        }
        this.mapService
          .centerOn(this.selectedVersion.servicePointGeolocation?.wgs84)
          .then(() => this.mapService.selectServicePoint(this.selectedVersion.number.number));
      }
    });
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.mapService.isEditModus.next(true);
      this.form.enable();
    }
  }

  private showConfirmationDialog() {
    this.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        if (this.isNew) {
          this.closeSidePanel();
        } else {
          this.initSelectedVersion();
          this.form.disable();
        }
      }
    });
  }

  private confirmLeave(): Observable<boolean> {
    if (this.form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
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
    this.servicePointService
      .updateServicePoint(id, servicePointVersion)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate(['..', this.selectedVersion.number.number], { relativeTo: this.route })
          .then();
      });
  }

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }

  isFormDirty(): boolean {
    return this.form.dirty;
  }

  handleGeolocationToggle(hasGeolocation: boolean) {
    if (hasGeolocation) {
      this.form.controls.servicePointGeolocation.controls.spatialReference.setValue(
        SpatialReference.Lv95
      );
    } else {
      this.form.controls.servicePointGeolocation.controls.spatialReference.setValue(null);
    }
    this.form.markAsDirty();
  }
}
