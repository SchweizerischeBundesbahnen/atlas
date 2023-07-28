import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReadServicePointVersion } from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';

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
export class ServicePointSidePanelComponent implements OnInit {
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadServicePointVersion;
  maxValidity!: DateRange;

  tabs = TABS;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.servicePointVersions = this.route.snapshot.data.servicePoint;

    this.maxValidity = VersionsHandlingService.getMaxValidity(this.servicePointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.servicePointVersions
    );
  }
}
