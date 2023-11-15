import { Component } from '@angular/core';
import { ReadServicePointVersion, ReadStopPointVersion, ServicePointsService } from '../../../api';
import { DateRange } from '../../../core/versioning/date-range';
import { Subscription } from 'rxjs';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { ActivatedRoute } from '@angular/router';

export const TABS = [
  {
    link: 'stop-point',
    title: 'PRM.STOP_POINT',
  },
  {
    link: 'reference-point',
    title: 'PRM.REFERENCE_POINT',
  },
  {
    link: 'platform',
    title: 'PRM.PLATFORM',
  },
  {
    link: 'ticket-counter',
    title: 'PRM.TICKET_COUNTER',
  },
  {
    link: 'information-desk',
    title: 'PRM.INFORMATION_DESK',
  },
  {
    link: 'toilette',
    title: 'PRM.TOILETTE',
  },
  {
    link: 'parking-lot',
    title: 'PRM.PARKING_LOT',
  },
  {
    link: 'connection',
    title: 'PRM.CONNECTION',
  },
];

@Component({
  selector: 'app-prm-panel',
  templateUrl: './prm-panel.component.html',
  styleUrls: ['./prm-panel.component.scss'],
})
export class PrmPanelComponent {
  selectedServicePointVersion!: ReadServicePointVersion;
  stopPointVersions!: ReadStopPointVersion[];
  selectedVersion!: ReadStopPointVersion;
  maxValidity!: DateRange;

  tabs = TABS;

  private stopPointSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private servicePointsService: ServicePointsService,
  ) {}

  ngOnInit() {
    this.stopPointSubscription = this.route.data.subscribe((next) => {
      this.stopPointVersions = next.stopPoint;
      this.initStopPointVersioning();
      this.servicePointsService
        .getServicePointVersions(this.selectedVersion.number.number)
        .subscribe((servicePointVersions: ReadServicePointVersion[]) => {
          this.initServicePointVersioning(servicePointVersions);
        });
    });
  }

  ngOnDestroy() {
    this.stopPointSubscription?.unsubscribe();
  }

  private initStopPointVersioning() {
    this.maxValidity = VersionsHandlingService.getMaxValidity(this.stopPointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.stopPointVersions,
    );
  }

  private initServicePointVersioning(servicePointVersions: ReadServicePointVersion[]) {
    this.selectedServicePointVersion =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);
  }
}
