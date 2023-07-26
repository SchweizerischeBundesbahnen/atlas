import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReadServicePointVersion } from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/VersionsHandling.service';
import { DateRange } from '../../../core/versioning/date-range';

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './service-point-detail.component.html',
  styleUrls: ['./service-point-detail.component.scss'],
})
export class ServicePointDetailComponent implements OnInit {
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadServicePointVersion;
  showVersionSwitch = false;
  maxValidity!: DateRange;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.servicePointVersions = this.route.snapshot.data.servicePoint;

    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.servicePointVersions);
    this.maxValidity = VersionsHandlingService.getMaxValidity(this.servicePointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.servicePointVersions
    );
  }
}
