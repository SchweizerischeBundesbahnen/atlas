import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReadServicePointVersion } from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { MapService } from '../map/map.service';
import { Subscription } from 'rxjs';
import { TrafficPointMapService } from '../map/traffic-point-map.service';
import { Countries } from '../../../core/country/Countries';

export const TABS = [
  {
    link: 'service-point',
    title: 'SEPODI.SERVICE_POINTS.SERVICE_POINT',
  },
  {
    link: 'areas',
    title: 'SEPODI.SERVICE_POINTS.AREAS',
  },
  {
    link: 'traffic-point-elements',
    title: 'SEPODI.TRAFFIC_POINT_ELEMENTS.TRAFFIC_POINT_ELEMENTS',
  },
  {
    link: 'loading-points',
    title: 'SEPODI.LOADING_POINTS.LOADING_POINTS',
  },
  {
    link: 'comment',
    title: 'SEPODI.SERVICE_POINTS.FOT_COMMENT',
  },
];

export const FOREIGN_TABS = TABS.filter((i) =>
  ['service-point', 'loading-points', 'comment'].includes(i.link),
);

@Component({
  selector: 'app-service-point-side-panel',
  templateUrl: './service-point-side-panel.component.html',
  styleUrls: ['./service-point-side-panel.component.scss'],
})
export class ServicePointSidePanelComponent implements OnInit, OnDestroy {
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadServicePointVersion;
  maxValidity!: DateRange;

  tabs = TABS;

  private servicePointSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private mapService: MapService,
    private trafficPointMapService: TrafficPointMapService,
  ) {}

  ngOnInit() {
    this.servicePointSubscription = this.route.data.subscribe((next) => {
      this.servicePointVersions = next.servicePoint;
      this.initVersioning();
      if (Countries.geolocationCountries.includes(this.servicePointVersions[0].country)) {
        this.tabs = TABS;
      } else {
        this.tabs = FOREIGN_TABS;
      }

      this.trafficPointMapService.displayTrafficPointsOnMap(
        this.servicePointVersions[0].number.number,
      );
    });
  }

  ngOnDestroy() {
    this.mapService.deselectServicePoint();
    this.trafficPointMapService.clearDisplayedTrafficPoints();
    this.servicePointSubscription?.unsubscribe();
  }

  private initVersioning() {
    this.maxValidity = VersionsHandlingService.getMaxValidity(this.servicePointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.servicePointVersions,
    );
  }
}
