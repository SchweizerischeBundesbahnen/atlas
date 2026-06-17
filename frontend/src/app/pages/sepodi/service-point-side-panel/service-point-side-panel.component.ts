import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ReadServicePointVersion } from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { MapService } from '../map/map.service';
import { Subscription } from 'rxjs';
import { TrafficPointMapService } from '../map/traffic-point-map.service';
import { Countries } from '../../../core/country/Countries';
import { DetailPageContainerComponent } from '../../../core/components/detail-page-container/detail-page-container.component';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { MatTabLink, MatTabNav, MatTabNavPanel } from '@angular/material/tabs';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { SloidContainerComponent } from '../../../core/sloid-container/sloid-container.component';
import { AtlasClipboardComponent } from '../../../core/form-components/atlas-clipboard/atlas-clipboard.component';

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

export const FOREIGN_TABS = TABS.filter((i) => ['service-point', 'loading-points', 'comment'].includes(i.link));

@Component({
  selector: 'atlas-service-point-side-panel',
  templateUrl: './service-point-side-panel.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./service-point-side-panel.component.scss'],
  imports: [
    DetailPageContainerComponent,
    DateRangeTextComponent,
    MatTabNav,
    MatTabLink,
    RouterLinkActive,
    RouterLink,
    MatTabNavPanel,
    RouterOutlet,
    SplitServicePointNumberPipe,
    TranslatePipe,
    SloidContainerComponent,
    AtlasClipboardComponent,
  ],
})
export class ServicePointSidePanelComponent implements OnInit, OnDestroy {
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadServicePointVersion;
  maxValidity!: DateRange;

  tabs = TABS;

  private servicePointSubscription?: Subscription;

  private readonly route = inject(ActivatedRoute);
  private readonly mapService = inject(MapService);
  private readonly trafficPointMapService = inject(TrafficPointMapService);

  ngOnInit() {
    this.servicePointSubscription = this.route.data.subscribe((next) => {
      this.servicePointVersions = next.servicePoint;
      this.initVersioning();
      if (Countries.geolocationCountries.includes(this.servicePointVersions[0].country)) {
        this.tabs = TABS;
      } else {
        this.tabs = FOREIGN_TABS;
      }

      this.trafficPointMapService.displayTrafficPointsOnMap(this.servicePointVersions[0].number.number);
    });
  }

  ngOnDestroy() {
    this.mapService.deselectServicePoint();
    this.trafficPointMapService.clearDisplayedTrafficPoints();
    this.servicePointSubscription?.unsubscribe();
  }

  private initVersioning() {
    this.maxValidity = VersionsHandlingService.getMaxValidity(this.servicePointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(this.servicePointVersions);
  }
}
