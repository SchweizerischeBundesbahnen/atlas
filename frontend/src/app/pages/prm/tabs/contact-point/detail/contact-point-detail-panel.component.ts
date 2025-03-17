import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {VersionsHandlingService} from '../../../../../core/versioning/versions-handling.service';
import {DateRange} from '../../../../../core/versioning/date-range';
import {ReadContactPointVersion, ReadServicePointVersion,} from '../../../../../api';
import {PrmMeanOfTransportHelper} from "../../../util/prm-mean-of-transport-helper";

@Component({
    selector: 'app-contact-point-detail-panel',
    templateUrl: './contact-point-detail-panel.component.html',
    standalone: false
})
export class ContactPointDetailPanelComponent implements OnInit {
  isNew = false;
  isReduced = false;

  contactPoint: ReadContactPointVersion[] = [];
  selectedVersion!: ReadContactPointVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  constructor(
    private route: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.contactPoint = data.contactPoint;
      this.isReduced = PrmMeanOfTransportHelper.isReduced(data.stopPoint[0].meansOfTransport);

      this.servicePoint = VersionsHandlingService.determineDefaultVersionByValidity(data.servicePoint);

      this.isNew = this.contactPoint.length === 0;

      if (!this.isNew) {
        this.maxValidity = VersionsHandlingService.getMaxValidity(this.contactPoint);
        this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(this.contactPoint);
      }
    });
  }
}
