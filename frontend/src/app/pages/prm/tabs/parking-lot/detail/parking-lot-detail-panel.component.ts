import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../../../core/versioning/date-range';
import {
  ReadParkingLotVersion,
  ReadServicePointVersion,
} from '../../../../../api';
import { PrmMeanOfTransportHelper } from '../../../util/prm-mean-of-transport-helper';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { PrmDetailPanelComponent } from '../../detail-panel/prm-detail-panel.component';
import { DetailWithRelationTabComponent } from '../../relation/tab/detail-with-relation-tab.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-parking-lot-detail-panel',
  templateUrl: './parking-lot-detail-panel.component.html',
  imports: [
    DetailPageContainerComponent,
    PrmDetailPanelComponent,
    DetailWithRelationTabComponent,
    TranslatePipe,
  ],
})
export class ParkingLotDetailPanelComponent implements OnInit {
  isNew = false;
  isReduced = false;

  selectedVersion!: ReadParkingLotVersion;
  parkingLot: ReadParkingLotVersion[] = [];

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      this.parkingLot = data.parkingLot;
      this.isReduced = PrmMeanOfTransportHelper.isReduced(
        data.stopPoint[0].meansOfTransport
      );

      const servicePointVersions: ReadServicePointVersion[] = data.servicePoint;
      this.servicePoint =
        VersionsHandlingService.determineDefaultVersionByValidity(
          servicePointVersions
        );

      this.isNew = this.parkingLot.length === 0;

      if (!this.isNew) {
        this.maxValidity = VersionsHandlingService.getMaxValidity(
          this.parkingLot
        );
        this.selectedVersion =
          VersionsHandlingService.determineDefaultVersionByValidity(
            this.parkingLot
          );
      }
    });
  }
}
