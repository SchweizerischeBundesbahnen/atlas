import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../../../core/versioning/date-range';
import {
  ReadPlatformVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
  ReadTrafficPointElementVersion,
} from '../../../../../api';
import { PrmMeanOfTransportHelper } from '../../../util/prm-mean-of-transport-helper';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { NgIf } from '@angular/common';
import { DateRangeTextComponent } from '../../../../../core/versioning/date-range-text/date-range-text.component';
import { DetailWithRelationTabComponent } from '../../relation/tab/detail-with-relation-tab.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-platform-detail-panel',
  templateUrl: './platform-detail-panel.component.html',
  imports: [
    DetailPageContainerComponent,
    NgIf,
    DateRangeTextComponent,
    DetailWithRelationTabComponent,
    TranslatePipe,
  ],
})
export class PlatformDetailPanelComponent implements OnInit {
  isNew = false;
  platform: ReadPlatformVersion[] = [];
  selectedVersion!: ReadPlatformVersion;

  servicePoint!: ReadServicePointVersion;
  trafficPoint!: ReadTrafficPointElementVersion;
  maxValidity!: DateRange;
  stopPoint!: ReadStopPointVersion[];

  isReduced = false;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      this.servicePoint =
        VersionsHandlingService.determineDefaultVersionByValidity(
          data.servicePoint
        );
      this.trafficPoint =
        VersionsHandlingService.determineDefaultVersionByValidity(
          data.trafficPoint
        );

      this.platform = data.platform;

      this.isNew = this.platform.length === 0;
      if (!this.isNew) {
        this.maxValidity = VersionsHandlingService.getMaxValidity(
          this.platform
        );
      }
      this.stopPoint = data.stopPoint;
      this.isReduced = PrmMeanOfTransportHelper.isReduced(
        this.stopPoint[0].meansOfTransport
      );
    });
  }
}
