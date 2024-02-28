import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {VersionsHandlingService} from '../../../../../core/versioning/versions-handling.service';
import {DateRange} from '../../../../../core/versioning/date-range';
import {
  ReadPlatformVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
  ReadTrafficPointElementVersion,
} from '../../../../../api';
import {PrmMeanOfTransportHelper} from "../../../util/prm-mean-of-transport-helper";

@Component({
  selector: 'app-platforms',
  templateUrl: './platform-detail-panel.component.html',
})
export class PlatformDetailPanelComponent implements OnInit {
  isNew = false;
  platform: ReadPlatformVersion[] = [];
  selectedVersion!: ReadPlatformVersion;

  servicePoint!: ReadServicePointVersion;
  trafficPoint!: ReadTrafficPointElementVersion;
  maxValidity!: DateRange;
  stopPoint!: ReadStopPointVersion[];
  businessOrganisations: string[] = [];

  reduced = false;

  constructor(
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);
    this.trafficPoint = VersionsHandlingService.determineDefaultVersionByValidity(
      this.route.snapshot.data.trafficPoint,
    );

    this.platform = this.route.snapshot.data.platform;
    this.isNew = this.platform.length === 0;
    if (!this.isNew) {
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.platform);
    }
    this.stopPoint = this.route.snapshot.data.stopPoint;
    this.reduced = PrmMeanOfTransportHelper.isReduced(this.stopPoint[0].meansOfTransport);
  }

}
