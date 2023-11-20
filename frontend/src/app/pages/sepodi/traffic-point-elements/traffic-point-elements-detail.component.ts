import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CreateTrafficPointElementVersion,
  ReadServicePointVersion,
  ReadTrafficPointElementVersion,
  ServicePointsService,
  TrafficPointElementsService,
  TrafficPointElementType,
} from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { catchError, EMPTY, Observable, of, Subject } from 'rxjs';
import { Pages } from '../../pages';
import { FormGroup } from '@angular/forms';
import {
  TrafficPointElementDetailFormGroup,
  TrafficPointElementFormGroupBuilder,
} from './traffic-point-detail-form-group';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../core/validation/validation.service';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '../../../core/notification/notification.service';
import { TrafficPointMapService } from '../map/traffic-point-map.service';
import { ValidityConfirmationService } from '../validity/validity-confirmation.service';
import { DetailFormComponent } from '../../../core/leave-guard/leave-dirty-form-guard.service';

interface AreaOption {
  sloid: string | undefined;
  displayText: string;
}

const NUMBER_COLONS_PLATFORM = 1;
const NUMBER_COLONS_AREA = 0;

@Component({
  selector: 'app-traffic-point-elements',
  templateUrl: './traffic-point-elements-detail.component.html',
  styleUrls: ['./traffic-point-elements-detail.component.scss'],
})
export class TrafficPointElementsDetailComponent implements OnInit, OnDestroy, DetailFormComponent {
  readonly extractSloid = (option: AreaOption) => option.sloid;
  readonly displayExtractor = (option: AreaOption) => option.displayText;

  trafficPointVersions!: ReadTrafficPointElementVersion[];
  selectedVersion!: ReadTrafficPointElementVersion;

  maxValidity!: DateRange;
  servicePointName!: string;

  showVersionSwitch = false;
  selectedVersionIndex!: number;

  form!: FormGroup<TrafficPointElementDetailFormGroup>;
  isNew = false;
  isSwitchVersionDisabled = false;
  areaOptions: AreaOption[] = [];
  servicePointNumber!: number;
  servicePoint: ReadServicePointVersion[] = [];
  servicePointBusinessOrganisations: string[] = [];
  isTrafficPointArea = false;
  geographyActive = true;
  numberColons!: number;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private trafficPointMapService: TrafficPointMapService,
    private servicePointService: ServicePointsService,
    private trafficPointElementsService: TrafficPointElementsService,
    private dialogService: DialogService,
    private validityConfirmationService: ValidityConfirmationService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit() {
    this.isTrafficPointArea = history.state.isTrafficPointArea;
    this.numberColons = this.isTrafficPointArea ? NUMBER_COLONS_AREA : NUMBER_COLONS_PLATFORM;

    this.route.data.pipe(takeUntil(this.ngUnsubscribe)).subscribe((next) => {
      this.trafficPointVersions = next.trafficPoint;
      this.initTrafficPoint();
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.trafficPointMapService.clearDisplayedTrafficPoints();
    this.trafficPointMapService.clearCurrentTrafficPoint();
  }

  private initTrafficPoint() {
    if (this.trafficPointVersions.length == 0) {
      this.isNew = true;
      this.form = TrafficPointElementFormGroupBuilder.buildFormGroup();
    } else {
      this.isNew = false;
      VersionsHandlingService.addVersionNumbers(this.trafficPointVersions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.trafficPointVersions);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.trafficPointVersions,
      );
      this.selectedVersionIndex = this.trafficPointVersions.indexOf(this.selectedVersion);

      this.initSelectedVersion();
    }
    this.initServicePointInformation();
  }

  private initServicePointInformation() {
    this.servicePointNumber =
      history.state?.servicePointNumber ?? this.selectedVersion?.servicePointNumber?.number;

    if (!this.servicePointNumber) {
      this.router.navigate([Pages.SEPODI.path]).then();
    } else {
      this.trafficPointMapService.displayTrafficPointsOnMap(this.servicePointNumber);

      this.servicePointService
        .getServicePointVersions(this.servicePointNumber)
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe((servicePoint) => {
          this.servicePoint = servicePoint;
          this.servicePointName =
            VersionsHandlingService.determineDefaultVersionByValidity(
              servicePoint,
            ).designationOfficial;
          this.servicePointBusinessOrganisations = this.servicePoint.map((i) => {
            return i.businessOrganisation;
          });
        });

      this.trafficPointElementsService
        .getAreasOfServicePoint(this.servicePointNumber)
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe((areas) => {
          const options: AreaOption[] = [{ sloid: undefined, displayText: '' }];
          options.push(
            ...areas.objects!.map((i) => {
              return { sloid: i.sloid, displayText: `${i.designation} - ${i.sloid}` };
            }),
          );
          this.areaOptions = options;
        });
    }
  }

  get servicePointNumberPartForSloid() {
    const numberAsString = String(this.servicePointNumber);
    if (numberAsString.startsWith('85')) {
      return String(this.servicePointNumber % 100);
    }
    return numberAsString;
  }

  backToTrafficPointElements(destination: string) {
    this.router
      .navigate([
        Pages.SEPODI.path,
        Pages.SERVICE_POINTS.path,
        this.servicePointNumber,
        destination,
      ])
      .then();
  }

  confirmCancel() {
    this.isTrafficPointArea
      ? this.backToTrafficPointElements(Pages.TRAFFIC_POINT_ELEMENTS_AREA.path)
      : this.backToTrafficPointElements(Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path);
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.trafficPointVersions[newIndex];
    this.initSelectedVersion();
  }

  private initSelectedVersion() {
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.trafficPointVersions);
    this.form = TrafficPointElementFormGroupBuilder.buildFormGroup(this.selectedVersion);
    this.geographyActive = !!this.selectedVersion?.trafficPointElementGeolocation?.spatialReference;
    if (!this.isNew) {
      this.form.disable();
    }
    this.trafficPointMapService.displayCurrentTrafficPoint(
      this.selectedVersion.trafficPointElementGeolocation?.wgs84,
    );
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.isSwitchVersionDisabled = true;
      this.form.enable();
    }
  }

  private showConfirmationDialog() {
    this.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        if (this.isNew) {
          this.confirmCancel();
        } else {
          this.initSelectedVersion();
          this.form.disable();
        }
        this.isSwitchVersionDisabled = false;
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
      this.confirmValidityOverServicePoint().subscribe((confirmed) => {
        if (confirmed) {
          const trafficPointElementVersion = this.form
            .value as unknown as CreateTrafficPointElementVersion;

          this.isTrafficPointArea
            ? (trafficPointElementVersion.trafficPointElementType = TrafficPointElementType.Area)
            : (trafficPointElementVersion.trafficPointElementType =
                TrafficPointElementType.Platform);

          this.form.disable();
          trafficPointElementVersion.numberWithoutCheckDigit = this.servicePointNumber;
          if (this.isNew) {
            this.create(trafficPointElementVersion);
          } else {
            this.update(this.selectedVersion.id!, trafficPointElementVersion);
          }
        }
      });
    }
  }

  private confirmValidityOverServicePoint(): Observable<boolean> {
    const stopPoint = this.servicePoint.filter((i) => i.stopPoint);
    return this.validityConfirmationService.confirmValidityOverServicePoint(
      stopPoint,
      this.form.controls.validFrom.value!,
      this.form.controls.validTo.value!,
    );
  }

  private create(trafficPointElementVersion: CreateTrafficPointElementVersion) {
    this.trafficPointElementsService
      .createTrafficPoint(trafficPointElementVersion)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((trafficPointElementVersion) => {
        this.notificationService.success('SEPODI.TRAFFIC_POINT_ELEMENTS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', trafficPointElementVersion.sloid], { relativeTo: this.route })
          .then();
        this.isSwitchVersionDisabled = false;
      });
  }

  private update(id: number, trafficPointElementVersion: CreateTrafficPointElementVersion) {
    this.trafficPointElementsService
      .updateTrafficPoint(id, trafficPointElementVersion)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('SEPODI.TRAFFIC_POINT_ELEMENTS.NOTIFICATION.EDIT_SUCCESS');
        this.router.navigate(['..', this.selectedVersion.sloid], { relativeTo: this.route }).then();
        this.isSwitchVersionDisabled = false;
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
}
