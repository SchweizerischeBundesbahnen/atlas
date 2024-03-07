import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {VersionsHandlingService} from '../../../../../core/versioning/versions-handling.service';
import {DateRange} from '../../../../../core/versioning/date-range';
import {ReadParkingLotVersion, ReadServicePointVersion,} from '../../../../../api';
import {PrmMeanOfTransportHelper} from "../../../util/prm-mean-of-transport-helper";

@Component({
  selector: 'app-parking-lot-detail-panel',
  templateUrl: './parking-lot-detail-panel.component.html',
})
export class ParkingLotDetailPanelComponent implements OnInit {
  isNew = false;
  reduced = false;

  selectedVersion!: ReadParkingLotVersion;
  parkingLot: ReadParkingLotVersion[] = [];

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  constructor(
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.parkingLot = this.route.snapshot.data.parkingLot;
    this.reduced = PrmMeanOfTransportHelper.isReduced(this.route.snapshot.data.stopPoint[0].meansOfTransport);

    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);

    this.isNew = this.parkingLot.length === 0;

    if (!this.isNew) {
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.parkingLot);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.parkingLot,
      );
    }
  }

}
