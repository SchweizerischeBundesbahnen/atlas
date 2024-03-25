import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {
  CreateTrafficPointElementVersion,
  ReadServicePointVersion,
  ReadTrafficPointElementVersion,
  ServicePointsService,
  TrafficPointElementsService,
  TrafficPointElementType,
} from '../../../api';
import {VersionsHandlingService} from '../../../core/versioning/versions-handling.service';
import {DateRange} from '../../../core/versioning/date-range';
import {catchError, EMPTY, Observable, of, take} from 'rxjs';
import {Pages} from '../../pages';
import {FormGroup} from '@angular/forms';
import {TrafficPointElementDetailFormGroup, TrafficPointElementFormGroupBuilder,} from './traffic-point-detail-form-group';
import {DialogService} from '../../../core/components/dialog/dialog.service';
import {ValidationService} from '../../../core/validation/validation.service';
import {NotificationService} from '../../../core/notification/notification.service';
import {TrafficPointMapService} from '../map/traffic-point-map.service';
import {ValidityConfirmationService} from '../validity/validity-confirmation.service';
import {DetailFormComponent} from '../../../core/leave-guard/leave-dirty-form-guard.service';
import {GeographyFormGroup, GeographyFormGroupBuilder} from '../geography/geography-form-group';
import {ServicePointFormGroupBuilder} from "../service-point-side-panel/service-point/service-point-detail-form-group";
import {Moment} from "moment/moment";

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
  servicePointSloid = "";
  servicePoint: ReadServicePointVersion[] = [];
  servicePointBusinessOrganisations: string[] = [];
  isTrafficPointArea = false;
  numberColons!: number;
  initValidFrom!: Moment | null | undefined;
  initValidTo!: Moment | null | undefined;
  trafficPointElementVersion!:CreateTrafficPointElementVersion;


  private _savedGeographyForm?: FormGroup<GeographyFormGroup>;

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

    this.route.data.subscribe((next) => {
      this.trafficPointVersions = next.trafficPoint;
      this.initTrafficPoint();
    });
  }

  ngOnDestroy() {
    this.trafficPointMapService.clearDisplayedTrafficPoints();
    this.trafficPointMapService.clearCurrentTrafficPoint();
  }

  private initTrafficPoint() {
    if (this.trafficPointVersions.length == 0) {
      this.isNew = true;
      this.form = TrafficPointElementFormGroupBuilder.buildFormGroup();
      TrafficPointElementFormGroupBuilder.addGroupToForm(
        this.form,
        'trafficPointElementGeolocation',
        GeographyFormGroupBuilder.buildFormGroup(),
      );
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
        .subscribe((servicePoint) => {
          this.servicePoint = servicePoint;
          const versionToDisplay = VersionsHandlingService.determineDefaultVersionByValidity(servicePoint);
          this.servicePointName = versionToDisplay.designationOfficial;
          this.servicePointSloid = versionToDisplay.sloid!;
          this.servicePointBusinessOrganisations = this.servicePoint.map((i) => {
            return i.businessOrganisation;
          });
        });

      this.trafficPointElementsService
        .getAreasOfServicePoint(this.servicePointNumber)
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
    if (!this.isNew) {
      this.disableForm();
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
      this.initValidity()
      this.form.enable({ emitEvent: false });
    }
  }

  private showConfirmationDialog() {
    this.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        if (this.isNew) {
          this.confirmCancel();
        } else {
          this.initSelectedVersion();
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
          this.trafficPointElementVersion = this.form
            .value as unknown as CreateTrafficPointElementVersion;

          this.isTrafficPointArea
            ? (this.trafficPointElementVersion.trafficPointElementType = TrafficPointElementType.Area)
            : (this.trafficPointElementVersion.trafficPointElementType =
                TrafficPointElementType.Platform);

          this.trafficPointElementVersion.numberWithoutCheckDigit = this.servicePointNumber;
          if (this.isNew) {
            this.create(this.trafficPointElementVersion);
            this.disableForm()
          }
          else{
            this.confirmValidity(this.trafficPointElementVersion);
          }
        }
      });
    }
  }

  disableForm() {
    this.form.disable();
    this._savedGeographyForm = undefined;
  }

  private confirmValidityOverServicePoint(): Observable<boolean> {
    return this.validityConfirmationService.confirmValidityOverServicePoint(
      this.servicePoint,
      this.form.controls.validFrom.value!,
      this.form.controls.validTo.value!,
    );
  }

  private create(trafficPointElementVersion: CreateTrafficPointElementVersion) {
    this.trafficPointElementsService
      .createTrafficPoint(trafficPointElementVersion)
      .pipe(catchError(this.handleError()))
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
      .pipe(catchError(this.handleError()))
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

  geographyEnabled() {
    if (this.form && !this.form.controls.trafficPointElementGeolocation) {
      const groupToAdd = this._savedGeographyForm ?? GeographyFormGroupBuilder.buildFormGroup();
      TrafficPointElementFormGroupBuilder.addGroupToForm(
        this.form,
        'trafficPointElementGeolocation',
        groupToAdd,
      );
      this.form.markAsDirty();
    }
  }

  geographyDisabled() {
    if (this.form.controls.trafficPointElementGeolocation) {
      this._savedGeographyForm = this.form.controls.trafficPointElementGeolocation;
      TrafficPointElementFormGroupBuilder.removeGroupFromForm(
        this.form,
        'trafficPointElementGeolocation',
      );
      this.form.markAsDirty();
    }
  }

  initValidity(){
    this.initValidTo = this.form?.value.validTo;
    this.initValidFrom = this.form?.value.validFrom;
  }

  confirmValidity(trafficPointElementVersion: CreateTrafficPointElementVersion){
    this.validityConfirmationService.confirmValidity(
      this.form.controls.validTo.value,
      this.form.controls.validFrom.value,
      this.initValidTo,
      this.initValidFrom
    ).pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.update(this.selectedVersion.id!, trafficPointElementVersion);
          this.disableForm();
        }
      });
  }
}
