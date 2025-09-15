import { Component, inject, OnInit } from '@angular/core';
import {
  ActivatedRoute,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { ReadTrafficPointElementVersion } from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { Pages } from '../../pages';
import { ReactiveFormsModule } from '@angular/forms';
import { ValidityService } from '../validity/validity.service';
import { DetailPageContainerComponent } from '../../../core/components/detail-page-container/detail-page-container.component';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';
import { MatTabLink, MatTabNav, MatTabNavPanel } from '@angular/material/tabs';
import { ServicePointService } from '../../../api/service/sepodi/service-point.service';

@Component({
  selector: 'app-traffic-point-elements-side-panel',
  templateUrl: './traffic-point-elements-side-panel.component.html',
  styleUrls: ['./traffic-point-elements-side-panel.component.scss'],
  providers: [ValidityService],
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
export class TrafficPointElementsSidePanelComponent implements OnInit {
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
  servicePointService = inject(ServicePointService);

  ngOnInit() {
    this.route.data.subscribe((next) => {
      this.trafficPointVersions = next.trafficPoint;
      this.isTrafficPointArea = next.isTrafficPointArea;
      this.initTrafficPoint();
      this.showTabs = !this.isTrafficPointArea && !this.isNew;
      this.initStopPointName();
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
    }
  }

  private initStopPointName() {
    this.servicePointService
      .getServicePointVersions(this.route.snapshot.params['servicePointNumber'])
      .subscribe((servicePoint) => {
        const versionToDisplay =
          VersionsHandlingService.determineDefaultVersionByValidity(
            servicePoint
          );
        this.servicePointName = versionToDisplay.designationOfficial;
      });
  }
}
