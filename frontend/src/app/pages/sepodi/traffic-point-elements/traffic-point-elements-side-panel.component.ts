import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import {
  ActivatedRoute,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import {
  MeanOfTransport,
  ReadServicePointVersion,
  ReadTrafficPointElementVersion,
} from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { Pages } from '../../pages';
import { ReactiveFormsModule } from '@angular/forms';
import { DetailPageContainerComponent } from '../../../core/components/detail-page-container/detail-page-container.component';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';
import { MatTabLink, MatTabNav, MatTabNavPanel } from '@angular/material/tabs';
import { SectorMapService } from '../map/sector-map.service';
import { environment } from '../../../../environments/environment';
import { TrafficPointMapService } from '../map/traffic-point-map.service';

@Component({
  selector: 'app-traffic-point-elements-side-panel',
  templateUrl: './traffic-point-elements-side-panel.component.html',
  styleUrls: ['./traffic-point-elements-side-panel.component.scss'],
  imports: [
    DetailPageContainerComponent,
    DateRangeTextComponent,
    ReactiveFormsModule,
    TranslatePipe,
    MatTabLink,
    MatTabNav,
    MatTabNavPanel,
    RouterLinkActive,
    RouterOutlet,
    RouterLink,
  ],
})
export class TrafficPointElementsSidePanelComponent
  implements OnInit, OnDestroy
{
  trafficPointVersions!: ReadTrafficPointElementVersion[];
  selectedVersion!: ReadTrafficPointElementVersion;
  maxValidity!: DateRange;
  servicePointName!: string;

  isTrafficPointArea = false;
  isNew = false;

  tabs = [
    {
      link: './',
      title: 'SEPODI.TRAFFIC_POINT_ELEMENTS.HEADER',
    },
    {
      link: Pages.SECTORS.path,
      title: Pages.SECTORS.title,
    },
    {
      link: Pages.SECTOR_GROUPS.path,
      title: Pages.SECTOR_GROUPS.title,
    },
  ];
  showTabs = true;

  route = inject(ActivatedRoute);
  trafficPointMapService = inject(TrafficPointMapService);
  sectorMapService = inject(SectorMapService);

  ngOnInit() {
    this.route.data.subscribe((next) => {
      this.trafficPointVersions = next.trafficPoint;
      this.isTrafficPointArea = next.isTrafficPointArea;
      this.initTrafficPoint();

      const servicePoint: ReadServicePointVersion[] = next.servicePoint;
      const servicePointHasOneMotTrain = servicePoint.some((i) =>
        i.meansOfTransport?.includes(MeanOfTransport.Train)
      );
      this.showTabs =
        environment.sectorsEnabled &&
        !this.isTrafficPointArea &&
        !this.isNew &&
        servicePointHasOneMotTrain;

      const versionToDisplay =
        VersionsHandlingService.determineDefaultVersionByValidity(servicePoint);
      this.servicePointName = versionToDisplay.designationOfficial;

      this.trafficPointMapService.displayTrafficPointsOnMap(
        versionToDisplay.number.number
      );

      if (this.showTabs) {
        this.sectorMapService.displaySectorsOnMap(
          versionToDisplay.number.number,
          this.selectedVersion.sloid!
        );
      }
    });
  }

  private initTrafficPoint() {
    if (this.trafficPointVersions.length == 0) {
      this.isNew = true;
    } else {
      this.isNew = false;
      VersionsHandlingService.addVersionNumbers(this.trafficPointVersions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(
        this.trafficPointVersions
      );
      this.selectedVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.trafficPointVersions
        );
      this.trafficPointMapService.displayCurrentTrafficPoint(
        this.selectedVersion.trafficPointElementGeolocation?.wgs84
      );
    }
  }

  ngOnDestroy() {
    this.trafficPointMapService.clearDisplayedTrafficPoints();
    this.trafficPointMapService.clearCurrentTrafficPoint();
    this.sectorMapService.clearDisplayedSectors();
  }
}
