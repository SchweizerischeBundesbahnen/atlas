import {Component, OnInit} from '@angular/core';
import {ReadServicePointVersion, ReadToiletVersion} from "../../../../../api";
import {DateRange} from "../../../../../core/versioning/date-range";
import {ActivatedRoute} from "@angular/router";
import {VersionsHandlingService} from "../../../../../core/versioning/versions-handling.service";
import {PrmMeanOfTransportHelper} from "../../../util/prm-mean-of-transport-helper";

@Component({
  selector: 'app-toilet-detail-panel',
  templateUrl: './toilet-detail-panel.component.html',
})
export class ToiletDetailPanelComponent implements OnInit {
  isNew = false;
  reduced = false;

  toiletVersions: ReadToiletVersion[] = [];
  selectedVersion!: ReadToiletVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  constructor(
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.toiletVersions = this.route.snapshot.data.toilet;
    this.reduced = PrmMeanOfTransportHelper.isReduced(this.route.snapshot.data.stopPoint[0].meansOfTransport);

    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);

    this.isNew = this.toiletVersions.length === 0;

    if (!this.isNew) {
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.toiletVersions);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.toiletVersions,
      );
    }
  }

}
