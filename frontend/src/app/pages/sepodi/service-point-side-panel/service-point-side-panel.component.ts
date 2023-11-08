import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReadServicePointVersion, TrafficPointElementsService } from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { MapService } from '../map/map.service';
import { Subscription } from 'rxjs';
import { DisplayableTrafficPoint } from './traffic-point-elements/displayable-traffic-point';

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
    private trafficPointElementsService: TrafficPointElementsService,
  ) {}

  ngOnInit() {
    this.servicePointSubscription = this.route.data.subscribe((next) => {
      this.servicePointVersions = next.servicePoint;
      this.initVersioning();

      this.displayTrafficPointsOnMap();
    });
  }

  private displayTrafficPointsOnMap() {
    this.mapService.mapInitialized.subscribe((initialized) => {
      if (initialized) {
        this.trafficPointElementsService
          .getTrafficPointsOfServicePointValidToday(this.servicePointVersions[0].number.number)
          .subscribe((points) => {
            const trafficPoints: DisplayableTrafficPoint[] = points
              .filter((point) => !!point.trafficPointElementGeolocation?.wgs84)
              .map((point) => {
                return {
                  sloid: point.sloid!,
                  designation: point.designation!,
                  type: point.trafficPointElementType,
                  coordinates: point.trafficPointElementGeolocation!.wgs84!,
                };
              });
            this.mapService.setDisplayedTrafficPoints(trafficPoints);
          });
      }
    });
  }

  ngOnDestroy() {
    this.mapService.deselectServicePoint();
    this.mapService.clearDisplayedTrafficPoints();
    this.servicePointSubscription?.unsubscribe();
  }

  private initVersioning() {
    this.maxValidity = VersionsHandlingService.getMaxValidity(this.servicePointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.servicePointVersions,
    );
  }
}
