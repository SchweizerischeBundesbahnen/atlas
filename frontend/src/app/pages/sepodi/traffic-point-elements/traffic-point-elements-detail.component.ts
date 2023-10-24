import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ReadTrafficPointElementVersionModel, ServicePointsService } from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { MapService } from '../map/map.service';
import { Subscription } from 'rxjs';
import { Pages } from '../../pages';
import { FormGroup } from '@angular/forms';
import { TrafficPointElementFormGroupBuilder } from './traffic-point-detail-form-group';

@Component({
  selector: 'app-traffic-point-elements',
  templateUrl: './traffic-point-elements-detail.component.html',
  styleUrls: ['./traffic-point-elements-detail.component.scss'],
})
export class TrafficPointElementsDetailComponent implements OnInit, OnDestroy {
  trafficPointVersions!: ReadTrafficPointElementVersionModel[];
  selectedVersion!: ReadTrafficPointElementVersionModel;

  maxValidity!: DateRange;
  servicePointName!: string;

  showVersionSwitch = false;
  selectedVersionIndex!: number;

  form!: FormGroup;
  isNew = false;
  isSwitchVersionDisabled = false;
  private subscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mapService: MapService,
    private servicePointService: ServicePointsService,
  ) {}

  ngOnInit() {
    this.subscription = this.route.data.subscribe((next) => {
      this.trafficPointVersions = next.trafficPoint;
      this.initTrafficPoint();
    });
  }

  ngOnDestroy() {
    this.mapService.deselectServicePoint();
    this.subscription?.unsubscribe();
  }

  private initTrafficPoint() {
    VersionsHandlingService.addVersionNumbers(this.trafficPointVersions);
    this.maxValidity = VersionsHandlingService.getMaxValidity(this.trafficPointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.trafficPointVersions,
    );

    this.servicePointService
      .getServicePointVersions(this.selectedVersion.servicePointNumber.number)
      .subscribe((servicePoint) => {
        this.servicePointName =
          VersionsHandlingService.determineDefaultVersionByValidity(
            servicePoint,
          ).designationOfficial;
      });

    this.initSelectedVersion();
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.trafficPointVersions[newIndex];
    this.initSelectedVersion();
  }

  private initSelectedVersion() {
    if (this.selectedVersion.id) {
      this.isNew = false;
    }
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.trafficPointVersions);
    this.form = TrafficPointElementFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();
    }
  }
}
