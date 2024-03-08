import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {VersionsHandlingService} from '../../../../../core/versioning/versions-handling.service';
import {DateRange} from '../../../../../core/versioning/date-range';
import {ReadContactPointVersion, ReadServicePointVersion,} from '../../../../../api';
import {PrmMeanOfTransportHelper} from "../../../util/prm-mean-of-transport-helper";

@Component({
  selector: 'app-contact-point-detail-panel',
  templateUrl: './contact-point-detail-panel.component.html',
})
export class ContactPointDetailPanelComponent implements OnInit {
  isNew = false;
  reduced = false;

  contactPoint: ReadContactPointVersion[] = [];
  selectedVersion!: ReadContactPointVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  constructor(
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.contactPoint = this.route.snapshot.data.contactPoint;
    this.reduced = PrmMeanOfTransportHelper.isReduced(this.route.snapshot.data.stopPoint[0].meansOfTransport);

    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);

    this.isNew = this.contactPoint.length === 0;

    if (!this.isNew) {
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.contactPoint);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.contactPoint,
      );
    }
  }

}
