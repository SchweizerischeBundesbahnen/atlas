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

@Component({
  selector: 'app-service-point',
  templateUrl: './service-point-detail.component.html',
  styleUrls: ['./service-point-detail.component.scss'],
})
export class ServicePointDetailComponent implements OnInit, OnDestroy {
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadServicePointVersion;
  showVersionSwitch = false;
  selectedVersionIndex!: number;
  form!: FormGroup<ServicePointDetailFormGroup>;
  isNew = true;

  types = Object.values(ServicePointType);
  selectedType: ServicePointType = ServicePointType.ServicePoint;
  operatingPointTypes = (Object.values(OperatingPointType) as string[]).concat(
    Object.values(OperatingPointTechnicalTimetableType)
  );

  stopPoint = false;
  freightServicePoint = false;

  stopPointTypes = Object.values(StopPointType);
  categories = Object.values(Category);

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
    this.initType();
  }

  private initType() {
    if (
      this.selectedVersion.operatingPointType ||
      this.selectedVersion.operatingPointTechnicalTimetableType
    ) {
      this.selectedType = ServicePointType.OperatingPoint;
    }
    if (this.selectedVersion.stopPoint || this.selectedVersion.freightServicePoint) {
      this.stopPoint = this.selectedVersion.stopPoint!;
      this.freightServicePoint = this.selectedVersion.freightServicePoint!;

      this.selectedType = ServicePointType.StopPoint;
    }
    if (this.selectedVersion.fareStop) {
      this.selectedType = ServicePointType.FareStop;
    }
  }

  private displayAndSelectServicePointOnMap() {
    this.mapSubscription = this.mapService.mapInitialized.subscribe((initialized) => {
      if (initialized) {
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
      this.form.enable();
    }
  }

  private showConfirmationDialog() {
    this.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        if (this.isNew) {
          this.closeSidePanel();
        } else {
          this.form.disable();
          this.initSelectedVersion();
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
      this.form.disable();
      const servicePointVersion = this.form.value as unknown as CreateServicePointVersion;
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
    console.log('updating to ', servicePointVersion);
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
}
