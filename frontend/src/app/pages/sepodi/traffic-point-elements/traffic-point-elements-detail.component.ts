import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ReadTrafficPointElementVersion,
  ServicePointsService,
  TrafficPointElementsService,
  TrafficPointElementType,
} from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { MapService } from '../map/map.service';
import { Subscription } from 'rxjs';
import { Pages } from '../../pages';
import { FormGroup } from '@angular/forms';
import {
  TrafficPointElementDetailFormGroup,
  TrafficPointElementFormGroupBuilder,
} from './traffic-point-detail-form-group';

interface AreaOption {
  sloid: string | undefined;
  displayText: string;
}

@Component({
  selector: 'app-traffic-point-elements',
  templateUrl: './traffic-point-elements-detail.component.html',
  styleUrls: ['./traffic-point-elements-detail.component.scss'],
})
export class TrafficPointElementsDetailComponent implements OnInit, OnDestroy {
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
  private subscription?: Subscription;
  areaOptions: AreaOption[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mapService: MapService,
    private servicePointService: ServicePointsService,
    private trafficPointElementsService: TrafficPointElementsService,
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
    this.selectedVersionIndex = this.trafficPointVersions.indexOf(this.selectedVersion);

    this.initServicePointInformation();
    this.initSelectedVersion();
  }

  private initServicePointInformation() {
    const servicePointNumber = this.selectedVersion.servicePointNumber.number;
    this.servicePointService
      .getServicePointVersions(servicePointNumber)
      .subscribe((servicePoint) => {
        this.servicePointName =
          VersionsHandlingService.determineDefaultVersionByValidity(
            servicePoint,
          ).designationOfficial;
      });

    this.trafficPointElementsService
      .getTrafficPointElements(
        undefined,
        [String(servicePointNumber)],
        undefined,
        undefined,
        undefined,
        undefined,
        TrafficPointElementType.Area,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        500,
        ['sloid,asc'],
      )
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
